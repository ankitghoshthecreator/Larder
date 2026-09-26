# System Design Addendum — Larder

Fills the gaps from the initial PRD: capacity estimation, sequence diagrams, MCP threat model, scaling, SLOs/SLIs, and data deletion.

## 1. Capacity estimation

Assumptions, stated explicitly:

- Target: 20,000 households at 12 months, ~2.3 members/household average
- Average household: 3 scans/week (mix of receipt + shelf photos), average image size 1.5MB
- Peak scan window: weekend grocery runs, roughly 40% of weekly scans land in a 6-hour Saturday/Sunday band

| Metric | Estimate |
|---|---|
| Scans/week | 20,000 × 3 = 60,000 |
| Scans/day (average) | ≈ 8,600 |
| Peak scans/sec (weekend band) | 60,000 × 0.4 / (2 days × 6h × 3600s) ≈ 0.56/sec sustained, bursty to several/sec at the top of the hour |
| Image storage growth | 60,000 × 1.5MB × 52 ≈ 4.7 TB/year at 20K households |
| Inference calls/day | ≈ scan count, 1:1, ≈ 8,600/day |
| Realtime connections (household sync, concurrent) | ~10% of members online at once ≈ 4,600 |

Conclusion: compute (inference) is the real constraint, not database load. Image storage is the largest cost driver and the one most worth setting a retention policy against early (Section 6).

## 2. Sequence diagrams

### 2.1 Receipt scan → inference → inventory update

```
User        App UI       Supabase Storage      Edge Fn: /infer      OCR (managed)    ONNX CNN     Postgres      Realtime
 |            |                  |                    |                  |              |            |             |
 |--photo---->|                  |                    |                  |              |            |             |
 |            |--upload image--->|                    |                  |              |            |             |
 |            |<--signed path----|                    |                  |              |            |             |
 |            |--invoke(path)------------------------->|                  |              |            |             |
 |            |                  |                    |--fetch image---->|              |            |             |
 |            |                  |                    |<--raw text-------|              |            |             |
 |            |                  |                    |--crop+classify------------------>|            |             |
 |            |                  |                    |<--category, confidence-----------|            |             |
 |            |                  |                    |--insert item(s)----------------------------->|             |
 |            |                  |                    |                  |              |            |--broadcast->|
 |<--items appear in UI-----------------------------------------------------------------------------------------|
```

If confidence is below threshold, the item is inserted with `status='needs_review'` instead of `status='confirmed'`, and the broadcast still fires so other household members see the pending item.

### 2.2 MCP export job (app as MCP client)

```
User        App UI      Edge Fn: /export-job      Notion MCP Server      Postgres
 |            |                    |                       |                 |
 |--tap "send to Notion"-->|                       |                 |
 |            |--invoke(household_id)-------------->|                       |                 |
 |            |                    |--query low-stock/expiring items---------------------->|
 |            |                    |<--item list------------------------------------------|
 |            |                    |--MCP tool call: create_page/append_rows-------------->|
 |            |                    |<--confirmation-------------------------------------|
 |            |<--"list sent" status----------------|                       |                 |
```

Runs only on explicit user action; there is no scheduled or automatic export in v1.

## 3. MCP threat model

Two directions of MCP exposure here, each with distinct risk:

**As a server (AI client reads household inventory):**

| Threat | Vector | Mitigation |
|---|---|---|
| Cross-household data leakage | A bug in tool implementation queries without household scoping | MCP server calls Postgres using the caller's JWT, relying on the same RLS policies as the app itself — there is no code path that bypasses RLS, so a scoping bug fails closed, not open |
| Over-broad querying | Client repeatedly calls `list_expiring` with large day ranges to reconstruct full inventory history | Day range capped server-side (max 30), and calls are rate-limited per household per minute |
| Stale household membership | A removed household member's paired client still has a valid session | JWT validity checked against `household_members` on every call, not cached; removal takes effect immediately |

**As a client (app pushes data to Notion/Sheets):**

| Threat | Vector | Mitigation |
|---|---|---|
| Over-permissioned connector | Notion/Sheets connector granted broader access than the export needs (e.g., whole workspace instead of one page) | Onboarding flow requires the user to select a specific target page/sheet at connection time, not a workspace-wide grant, when the provider supports scoped grants |
| Silent/unexpected writes | Export job runs on a schedule the user didn't expect | No automatic scheduling in v1; every export is a manual, explicit action, and the app shows exactly what will be written before sending |
| Credential handling | Connector tokens stored insecurely | Connector OAuth tokens stored server-side (Supabase Vault or equivalent secret store), never shipped to the mobile client |

## 4. Scaling considerations

- **Inference is the bottleneck, not Postgres**: `/infer` Edge Functions should be evaluated against Supabase's concurrency limits early; if a burst exceeds the platform's Edge Function concurrency, a queue (e.g., a `receipt_scans.status='pending'` row + a polling or event-triggered worker) prevents dropped requests, at the cost of scan latency during bursts — documented as an accepted tradeoff for v1 rather than over-building for scale not yet needed
- **Storage cost control**: images are the dominant cost; a lifecycle rule (e.g., delete original images after 30 days once classification is confirmed, keep only the extracted structured data) is planned rather than retaining every photo indefinitely
- **Postgres indexing**: `items` indexed on `(household_id, expiry_estimate)` for the expiring-soon view and `(household_id, category)` for the summary view
- **Realtime**: household channel count scales with household count, not member count squared, since it's one broadcast channel per household, not pairwise connections

## 5. SLOs / SLIs

| SLI | SLO | Alert threshold |
|---|---|---|
| End-to-end scan-to-inventory latency (p95) | < 1.5s | Alert if p95 exceeds 3s for 15 minutes |
| Classification requiring manual correction | < 15% | Alert if 7-day rolling rate exceeds 20% |
| Household Realtime propagation latency | < 2s | Alert if p95 exceeds 5s |
| Edge Function error rate (`/infer`) | < 1% | Alert if 5-minute error rate exceeds 3% |

## 6. Data retention & deletion

- Account/household deletion (`delete_household(household_id)`):
  1. Deletes all `items` and `receipt_scans` rows for the household
  2. Deletes associated Storage objects (original images)
  3. Deletes `household_members` rows and, if the last member, the `households` row itself
  4. Revokes any connector tokens (Notion/Sheets) associated with the household
- Standing retention policy (independent of deletion requests): original scan images deleted automatically after 30 days, retaining only the structured item data extracted from them, unless the user has opted into contributing anonymized images for model retraining
- Backups: point-in-time recovery snapshots may retain deleted data for up to the platform's retention window, stated plainly in `/privacy`
