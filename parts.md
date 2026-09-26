# Larder — Android Kotlin System Architecture & Implementation Plan

Larder is a smart household grocery and pantry inventory management system built natively in Kotlin for Android (Android Studio).
It features photo-based receipt/shelf scanning (OCR + CNN inference), real-time household sync, expiry estimation, local offline caching with Room SQLite, and Model Context Protocol (MCP) integrations.

The project is split into **7 independent, modular parts** for incremental build, testing, and deployment.

---

## Part 1: Core Domain Models, Local Database & Repository Layer (Data Layer)
- **Objective**: Establish the core domain entities, Room database (SQLite), Data Access Objects (DAOs), and Repository pattern for offline-first operation.
- **Components**:
  - Entity classes: `Household`, `HouseholdMember`, `Item`, `ReceiptScan`, `Category`, `UnitType`.
  - Expiry Calculation Engine: `ExpiryCalculator` with default category shelf life rules (e.g., dairy 7d, produce 5d, pantry 180d, frozen 90d).
  - Room Database & DAOs: `LarderDatabase`, `ItemDao`, `ReceiptScanDao`, `HouseholdDao`.
  - Local Repository: `LocalInventoryRepository` providing CRUD, expiring-soon filters (`< N days`), low-stock queries, and item status state transitions (`confirmed`, `needs_review`).
- **Target Directories/Files**:
  - `app/src/main/java/com/larder/app/domain/model/`
  - `app/src/main/java/com/larder/app/domain/calculator/`
  - `app/src/main/java/com/larder/app/data/local/db/`
  - `app/src/main/java/com/larder/app/data/local/dao/`
  - `app/src/main/java/com/larder/app/data/repository/`

---

## Part 2: Design System, Jetpack Compose UI & App Navigation
- **Objective**: Implement Larder's cohesive design system (Cream base `#F8F4EA`, Deep Olive `#2E3324`, Clay Accent `#C17A50`), reusable UI components, and Jetpack Compose navigation structure.
- **Components**:
  - Theme & Typography: `Theme.kt`, `Color.kt`, `Type.kt`, custom typography rules (no gradients, no em-dashes).
  - Component Library: `LarderButton`, `ItemCard`, `StatusPill` (Needs Review, Confirmed, Expiring Soon), `QuantityStepper`, `CategoryIcon`.
  - Screens & Navigation Graph: `LarderNavGraph`, `HomeScreen` (Inventory List & Filters), `ExpiringScreen`, `ScanScreen`, `HouseholdSettingsScreen`.
- **Target Directories/Files**:
  - `app/src/main/java/com/larder/app/ui/theme/`
  - `app/src/main/java/com/larder/app/ui/components/`
  - `app/src/main/java/com/larder/app/ui/navigation/`
  - `app/src/main/java/com/larder/app/ui/screens/`

---

## Part 3: Remote Supabase Backend Integration & Realtime Sync Engine
- **Objective**: Connect the Android client to Supabase Postgres, Authentication (household invite codes), Storage, and Realtime channels.
- **Components**:
  - Supabase Auth Manager: User session, registration, household invite token generation & joining.
  - Remote Repository & Sync Engine: `SyncManager` handling bidirectional sync between Room SQLite and Supabase Postgres using `updated_at` Last-Write-Wins strategy.
  - Image Storage Uploader: Upload receipt and shelf photos to Supabase Storage with signed time-limited URLs.
  - Supabase Realtime Listener: WebSockets subscription to `items` table for instant multi-device household sync.
- **Target Directories/Files**:
  - `app/src/main/java/com/larder/app/data/remote/api/`
  - `app/src/main/java/com/larder/app/data/remote/supabase/`
  - `app/src/main/java/com/larder/app/data/sync/`
  - `supabase/migrations/`

---

## Part 4: CameraX Capture & ML Inference Pipeline (OCR + CNN Edge Connector)
- **Objective**: Build photo capture (receipts & pantry shelves) and integrate with the Supabase `/infer` Edge Function running OCR + MobileNetV3-Small CNN.
- **Components**:
  - Camera Capture Module: `CameraManager` built with CameraX, handling image capture, cropping, and compression.
  - Inference API Client: Calls `/infer` Edge Function with image payload; parses OCR line-items and CNN category outputs with confidence scores.
  - Confidence Threshold Engine: Automatically flags items below confidence threshold as `status = 'needs_review'`.
  - Offline Scan Queue: `ScanQueueWorker` via WorkManager to store failed/offline scans and auto-retry upon network reconnect.
- **Target Directories/Files**:
  - `app/src/main/java/com/larder/app/feature/camera/`
  - `app/src/main/java/com/larder/app/feature/inference/`
  - `app/src/main/java/com/larder/app/data/worker/`
  - `supabase/functions/infer/`

---

## Part 5: Model Context Protocol (MCP) Server & Client Integration
- **Objective**: Expose household inventory to AI clients via MCP Server and enable manual export of shopping lists to Notion / Google Sheets via MCP Client.
- **Components**:
  - MCP Server Service (`/mcp` Edge Function & Kotlin SDK client): Endpoints for `list_expiring`, `check_stock`, and `get_category_summary` using user JWT and strictly enforced Row Level Security (RLS).
  - MCP Client Connector (`/export-job` Edge Function): Job handler pushing structured shopping lists (item, qty, category) to user's connected Notion page or Google Sheet.
  - Export UI & Connector Settings: UI flow in app to select target provider, view preview, and trigger manual push.
- **Target Directories/Files**:
  - `app/src/main/java/com/larder/app/feature/mcp/`
  - `supabase/functions/mcp/`
  - `supabase/functions/export-job/`

---

## Part 6: Household Management & Multi-Member Collaboration
- **Objective**: Enable full multi-user household collaboration, role-based access, shared inventory audit history, and low-stock alert triggers.
- **Components**:
  - Household Management ViewModel & Flow: Create household, invite members via code/link, manage member roles (`owner`, `member`).
  - Activity & Audit Log: Track item additions, edits, and deletions across household members.
  - Alert & Notification System: Local system notifications for expiring items and low-stock items.
- **Target Directories/Files**:
  - `app/src/main/java/com/larder/app/feature/household/`
  - `app/src/main/java/com/larder/app/feature/notifications/`

---

## Part 7: Hardening, Automated Testing, CI/CD Pipeline & Production Build
- **Objective**: Configure model evaluation CI gates, unit & UI test suites, security retention policies, and Android Studio production release setup.
- **Components**:
  - Unit & Integration Test Suite: Tests for `ExpiryCalculator`, `SyncManager`, Room DAOs, and API models.
  - Android UI Tests: Compose UI tests for receipt review, item edit, and inventory filtering.
  - Model Evaluation Gate: GitHub Actions workflow testing CNN model top-1 accuracy on held-out dataset before deployment.
  - Data Retention Worker: 30-day photo cleanup worker and account/household deletion routines.
  - Build Configuration: `build.gradle.kts`, Proguard rules, release signing config.
- **Target Directories/Files**:
  - `app/src/test/java/com/larder/app/`
  - `app/src/androidTest/java/com/larder/app/`
  - `.github/workflows/ci.yml`
  - `.github/workflows/model_eval.yml`
