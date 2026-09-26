# Product Requirements Document — Larder

## 0. Platform decisions
| Area | Decision |
|---|---|
| Backend | Supabase (Postgres, Auth, Storage, Edge Functions, Realtime) |
| Client | React Native (Expo, bare workflow) + TypeScript |
| Design language | Cream background, single muted clay accent, rounded-rectangle buttons, no gradients, no emoji icons, no decorative motion |
| Legal/site | `/privacy`, `/terms`, custom domain, HTTPS/HSTS, real favicon set |
| Non-negotiables | No fake metrics, no fabricated reviews, no "Made with AI" badge, no em dashes in shipped copy |

## 1. Problem statement
Households lose track of what they already have and let food expire, but manual inventory entry is tedious enough that nobody keeps doing it. A photo-first flow lowers the friction; the question is whether classification is accurate enough to trust.

## 2. Goals
- G1: Turn a receipt or shelf photo into structured inventory items in under 2 seconds end-to-end
- G2: Keep every household member's inventory view in sync in near-real-time
- G3: Let the classification model's low-confidence outputs get flagged for review rather than silently guessed
- G4: Let a connected AI client answer "what's expiring" or "do we have X" without direct app access

## 3. Non-goals
- Not a payment, checkout, or price-comparison product
- Not a recipe-recommendation engine (may be a future MCP-server consumer, not a v1 feature)
- Not an offline-first classification product in v1 (classification requires network; explicitly documented)

## 4. Target users
- Primary: households of 2+ managing shared groceries
- Secondary: single users who want low-effort expiry tracking

## 5. Functional requirements

| ID | Requirement | Priority |
|---|---|---|
| F1 | User can photograph a receipt; line items are OCR'd and categorized | P0 |
| F2 | User can photograph a shelf/pantry area; visible packaged items are detected and categorized | P1 |
| F3 | Each item gets a default expiry estimate by category, user-editable per item | P0 |
| F4 | Low-confidence classifications are flagged "needs review," never silently auto-added | P0 |
| F5 | Households can have multiple members with Realtime-synced inventory | P0 |
| F6 | User can generate and manually send a shopping list to a connected Notion/Sheets MCP server | P1 |
| F7 | AI client can query expiring items and stock levels via the app's MCP server, scoped to the household via RLS-backed JWT | P1 |
| F8 | Offline: inventory is viewable and manually editable; new scans queue until reconnect | P0 |

## 6. Non-functional requirements
- NFR1: Classification pipeline (OCR + CNN) end-to-end under 1.5s at p95 on a warm Edge Function
- NFR2: Model eval accuracy gate in CI; deploy blocked if top-1 accuracy on the held-out set regresses
- NFR3: Receipt/shelf images stored with signed, time-limited URLs only, no public bucket access
- NFR4: MCP server and MCP client integrations both operate under the calling user's JWT, never a service-role key

## 7. Success metrics
- % of scanned items requiring manual correction (target: under 15% at GA)
- Median time from "open camera" to "item added to inventory"
- Household Realtime sync latency (target: under 2s for a member to see another member's update)

## 8. Risks
- R1: CNN category accuracy on unfamiliar packaging (private-label goods) — mitigated by the "needs review" flag and by collecting opted-in correction data for retraining
- R2: Cloud-only inference means no offline classification in v1 — documented as a known limitation, with MobileNetV3-Small chosen specifically so on-device migration is possible later without a model rewrite

## 9. Milestones

| Phase | Scope |
|---|---|
| M0 — Foundation | Supabase schema + RLS, auth, base navigation, design system components |
| M1 — Core loop | Photo capture → inference pipeline → inventory CRUD |
| M2 — Sync | Household Realtime sync |
| M3 — MCP | MCP server (query inventory) + MCP client export job (Notion/Sheets) |
| M4 — Hardening | CI model-eval gates, E2E suite, legal pages, custom domain, favicon |
| GA | Public release |

## 10. Open questions
- Whether the MCP client export targets Notion, Google Sheets, or both at GA (affects which connector setup flow ships first)
