# Larder

A pantry and grocery inventory app. Photograph a receipt or a shelf, the item name, category, and estimated expiry get extracted by a CNN + OCR pipeline running on Supabase Edge Functions, and the resulting inventory is queryable by any MCP-compatible AI client the user connects.

Not a shopping list with a countdown. It answers one question well: what's in the house, and what's about to go bad.

## 1. What it does

- Photograph a receipt → line items extracted, categorized, added to inventory
- Photograph a pantry shelf → CNN detects packaged goods and estimates counts
- Expiry estimation per category (perishables get a shorter default window, editable per item)
- Low-stock and expiring-soon views, computed locally from synced inventory
- MCP server: an AI client can ask "what's expiring this week" or "do I have flour"
- MCP client: the app can push a generated shopping list into a Notion database or Google Sheet the user connects, via that service's own MCP server

## 2. Tech stack

| Layer | Choice | Why |
|---|---|---|
| Mobile framework | React Native (Expo, bare workflow) | Camera + native module access |
| Language | TypeScript, strict mode | |
| Local cache | SQLite (via `expo-sqlite`) with a thin repository layer | Small dataset, doesn't need WatermelonDB's full reactive layer |
| Remote DB | Supabase (Postgres) | RLS, Storage for receipt images, Edge Functions for inference, Realtime for multi-device households |
| Auth | Supabase Auth, with household sharing via invite codes | |
| ML: OCR + CNN | Cloud-hosted, called via Supabase Edge Function (see Section 5) | Receipt OCR + item-category CNN are too heavy for reliable on-device latency across low-end Android devices |
| MCP layer | Server: `@modelcontextprotocol/sdk` HTTP transport behind Supabase Edge Function. Client: MCP client SDK inside a background Edge Function job | See Section 6 |
| State management | Zustand | |
| CI/CD | GitHub Actions + EAS Build | |
| Monitoring | Sentry + Supabase log drains to Logflare | |
| Testing | Jest, RTL, Detox | |

## 3. System architecture

```
┌───────────────────────────┐
│        Mobile App          │
│  Camera → local preview     │
│  Inventory UI (household)   │
└──────────┬──────────────────┘
           │ image upload (Storage) + RPC call
┌──────────▼──────────────────────────────┐
│              Supabase                    │
│  Storage (receipt/shelf images)          │
│  Edge Function: /infer                   │
│    → OCR (managed OCR API)               │
│    → CNN category classifier (ONNX Runtime,
│       hosted in the Edge Function container)
│  Postgres (+RLS) — inventory, households │
│  Edge Function: /mcp  (MCP server)       │
│  Edge Function: /export-job (MCP client, │
│    talks to Notion/Sheets MCP servers)   │
│  Realtime — household inventory sync     │
└───────────────────────────────────────────┘
```

## 4. Database schema (Postgres, Supabase)

```sql
create table households (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  created_at timestamptz default now()
);

create table household_members (
  household_id uuid references households(id) on delete cascade,
  user_id uuid references auth.users(id),
  role text default 'member' check (role in ('owner','member')),
  primary key (household_id, user_id)
);

create table items (
  id uuid primary key default gen_random_uuid(),
  household_id uuid not null references households(id),
  name text not null,
  category text not null,
  quantity numeric default 1,
  unit text default 'unit',
  expiry_estimate date,
  source_image_path text,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

create table receipt_scans (
  id uuid primary key default gen_random_uuid(),
  household_id uuid not null references households(id),
  image_path text not null,
  raw_ocr_text text,
  status text default 'pending' check (status in ('pending','processed','failed')),
  created_at timestamptz default now()
);

alter table items enable row level security;
alter table receipt_scans enable row level security;
alter table household_members enable row level security;

create policy "household members can read/write items" on items
  for all using (
    household_id in (select household_id from household_members where user_id = auth.uid())
  );

create policy "household members can read/write scans" on receipt_scans
  for all using (
    household_id in (select household_id from household_members where user_id = auth.uid())
  );

create policy "members see their own household rows" on household_members
  for select using (user_id = auth.uid());
```

## 5. CNN for item classification (the required ML component, cloud-deployed)

Per the requirement that the model runs on a mobile processor **or** on the cloud with mobile access — this one is cloud-hosted, and the tradeoff is stated rather than hidden:

- **Task**: classify a cropped item photo (or a receipt line-item region) into one of ~40 grocery categories (produce, dairy, canned goods, frozen, cleaning, etc.)
- **Architecture**: MobileNetV3-Small backbone, fine-tuned head (`GlobalAvgPool → Dense(256, relu) → Dense(40, softmax)`) — chosen specifically because it's mobile-friedly even though it's currently deployed server-side, so it can move on-device later without a rewrite
- **Export format**: ONNX, served via ONNX Runtime inside the Supabase Edge Function (Deno-compatible runtime, model loaded once per cold start and cached)
- **Training data**: fine-tuned on a licensed open grocery-image dataset plus household-anonymized crops the user opts into contributing
- **Pipeline per scan**:
  1. Image uploaded to Supabase Storage
  2. Edge Function pulls image, runs OCR for receipt text (if receipt mode) or directly runs the CNN (if shelf-photo mode)
  3. Category + confidence returned; below a confidence threshold, the item is flagged "needs review" instead of guessed
- **Latency budget**: under 1.5s end-to-end per image on a warm Edge Function instance
- **Migration path documented**: because MobileNetV3-Small quantizes cleanly to `.tflite`/Core ML, the README includes a note that this exact model can be moved on-device later for offline scanning, without switching architectures

## 6. MCP integration

**As an MCP server** (household inventory exposed for querying):
- `list_expiring(days: int)` — items expiring within N days
- `check_stock(item_name: str)` — whether an item is in the household inventory and in what quantity
- `get_category_summary()` — inventory grouped by category

All read-only, scoped to the requesting user's household via the same RLS the app itself uses — the MCP server calls Postgres with the user's JWT, not a service-role key, so it can't see other households' data.

**As an MCP client** (exporting a shopping list):
- A background Edge Function job (`/export-job`) acts as an MCP client to the user's connected Notion or Google Sheets MCP server
- Triggered manually from the app ("Send shopping list to Notion"), never automatically
- Writes a structured list (item, quantity, category) into a page/sheet the user designated during connector setup

## 7. Offline behavior

- Inventory list, quantities, and expiry views are fully readable offline from the local SQLite cache
- New scans queue locally if offline and upload automatically on reconnect (`expo-background-fetch` retry queue)
- Manual inventory edits sync via a simple `updated_at`-based last-write-wins, same as Marginalia, since households are small and true concurrent edits are rare

## 8. Security

- RLS scoped to household membership on every table
- Signed, time-limited URLs for Storage image access, no public buckets
- MCP server endpoint requires the same Supabase JWT as the mobile app; no separate API key floating around
- Edge Functions run with the caller's JWT forwarded, never the service-role key, for anything touching user data

## 9. Testing strategy

- Unit tests: expiry-estimate logic, category-to-default-shelf-life mapping, sync merge logic
- Integration tests: Edge Function `/infer` tested against a fixed set of sample receipt/shelf images with expected category outputs, run in CI against a local Supabase stack
- Model evaluation: held-out labeled image set, CI fails the deploy if top-1 accuracy on the eval set drops below a fixed threshold
- E2E: scan-to-inventory flow, offline scan queue, household invite flow (Detox)

## 10. CI/CD

- GitHub Actions: lint → typecheck → unit/integration → build
- Edge Functions deployed via `supabase functions deploy` in a separate job gated on the model-eval check passing
- EAS Build for mobile binaries on tagged releases
- Migrations in `supabase/migrations`, applied through the deploy pipeline only

## 11. Design system

- Palette: cream base (`#F8F4EA`), deep olive text (`#2E3324`), single clay accent (`#C17A50`) — no purple, no gradients
- Buttons: rounded-rectangle, not pill-shaped
- Icons: one custom line-icon set, no emoji
- Motion: list-item add/remove transitions only; no scroll-triggered decoration
- No AI-generated illustration; item category icons are simple flat vector shapes
- No fake "X households joined this week" counters, no review carousels on the marketing site unless every review is a real, attributed one the team can stand behind

## 12. Legal & site infrastructure

- `/privacy` — states plainly that receipt/shelf images are processed server-side for classification and explains retention (e.g. images deleted after N days unless the user opts into keeping them for retraining)
- `/terms`
- Custom domain, Cloudflare-fronted, HTTPS/HSTS
- Favicon set matching the app glyph

## 13. Explicitly not included

- No payment or UPI functionality (out of scope by design — separate system)
- No fabricated user counts, ratings, or testimonials anywhere in-app or on the site
- No "Made with AI" badge, no AI-flavored marketing copy, no em dashes in any shipped copy or site text

## 14. Suggested folder structure

```
larder/
  app/                     # Expo Router screens
  src/
    domain/                # expiry logic, category mapping, use-cases
    data/
      local/                # SQLite repository
      remote/               # Supabase client, storage upload, sync
    ui/                     # design system components
  supabase/
    migrations/
    functions/
      infer/                # OCR + CNN inference Edge Function
      mcp/                  # MCP server Edge Function
      export-job/           # MCP client Edge Function
  e2e/
  .github/workflows/
```
