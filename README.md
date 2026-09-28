# Larder

> A pantry and grocery inventory app. Photograph a receipt or a shelf — item name, category, and estimated expiry are extracted by a CNN + OCR pipeline. The resulting inventory is queryable by any MCP-compatible AI client.

**Not a shopping list with a countdown. It answers one question well: what's in the house, and what's about to go bad.**

---

## Project Structure (7 Independent Parts)

| Part | Module | Description |
|---|---|---|
| 1 | `domain/` + `data/local/` | Core domain entities, Room SQLite DB, DAOs, ExpiryCalculator, LocalInventoryRepository |
| 2 | `ui/` | Design system (Cream/Olive/Clay), Jetpack Compose screens, components, navigation |
| 3 | `data/remote/` + `data/sync/` | Supabase Auth, Storage, SyncManager (Last-Write-Wins), Realtime WebSocket listener |
| 4 | `feature/camera/` + `feature/inference/` | CameraX capture, /infer Edge Function client, CNN confidence threshold, offline scan queue |
| 5 | `feature/mcp/` + `supabase/functions/mcp/` | MCP Server (list_expiring, check_stock, get_category_summary) + MCP Client export (Notion / Sheets) |
| 6 | `feature/household/` + `feature/notifications/` | Multi-member households, invite codes, audit log, expiry/low-stock push notifications |
| 7 | `test/` + `.github/workflows/` | Full unit test suite, CI pipeline, model evaluation accuracy gate |

---

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin (Android Studio) |
| UI | Jetpack Compose + Material3 |
| Local cache | Room SQLite |
| Remote DB | Supabase (Postgres + RLS + Realtime) |
| Auth | Supabase Auth + household invite codes |
| ML | MobileNetV3-Small ONNX via Supabase Edge Function `/infer` |
| MCP | JSON-RPC 2.0 HTTP transport via Supabase Edge Function `/mcp` |
| CI/CD | GitHub Actions |

---

## Design System

- Background: `#F8F4EA` (Cream)
- Text: `#2E3324` (Deep Olive)
- Accent: `#C17A50` (Clay)
- Buttons: Rounded-rectangle (not pill-shaped)
- Motion: list add/remove transitions only — no decorative animation

---

## Quick Start

1. Open `D:\Larder` in Android Studio
2. Sync Gradle (`File → Sync Project with Gradle Files`)
3. Run on emulator or device (`Run → Run 'app'`)
4. Add your Supabase URL + Anon Key to `SupabaseConfig.kt` for remote features

---

## Running Unit Tests

```bash
./gradlew test
```

Test reports: `app/build/reports/tests/`
