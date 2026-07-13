# LineMan Pay

A modern field-agent utility bill collection and management app for Android — built for door-to-door electricity bill collectors to log payments, generate receipts, and manage collection history, fully offline.

## Features

- 📊 **Dashboard** — daily collection targets and cash-in-hand summary at a glance
- 💵 **New Payment** — quick entry for consumer name, ID, and amount, with instant receipt generation
- 🧾 **Digital Receipts** — pixel-accurate "Eastern Power" style receipt, mirroring the official paper slip
- 📜 **History** — searchable, filterable log of past collections with full receipt detail on tap
- 📴 **Offline-first** — all records are stored locally via Room (SQLite); no internet required to log or view collections
- 🤖 **Gemini-assisted features** — optional AI-assisted flows powered by the Gemini API (see setup below)

## Tech Stack

- **Language:** Kotlin (100%)
- **UI:** Jetpack Compose, Material Design 3
- **Local storage:** Room (SQLite)
- **Concurrency:** Kotlin Coroutines & Flow (StateFlow / SharedFlow)
- **Navigation:** Compose Navigation
- **Build:** Gradle (Kotlin DSL), version catalog (`libs.versions.toml`)

## Architecture

MVVM with a clean separation between UI, domain/data, and persistence layers:

```
UI Layer (Compose Screens) → ViewModel (StateFlow) → Repository → DAO → Room Database
```

- **Presentation:** `Dashboard`, `New Payment`, `History` screens + `MainViewModel` as the single state holder
- **Domain/Data:** `CollectionRepository` — the single source of truth, abstracting storage from the UI
- **Persistence:** `CollectionRecord` (entity), `CollectionDao`, `AppDatabase`

## Run Locally

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Clone this repo and open it in Android Studio
2. Let Android Studio sync Gradle and resolve any suggested fixes
3. Create a file named `.env` in the project root and set `GEMINI_API_KEY` to your own Gemini API key (see `.env.example`). This is only required for the AI-assisted features — the core app works without it.
4. Run the app on an emulator or physical device (`minSdk 24`)

> ⚠️ **Note on the Gemini key:** the key is compiled into the app binary via `BuildConfig`. Don't distribute a release APK built with your personal key — anyone can extract it from the APK. This is fine for local development and debug builds only.

## Building a Debug APK

```bash
./gradlew assembleDebug
```

The output APK will be at `app/build/outputs/apk/debug/app-debug.apk`. Every push to `main` also triggers a GitHub Actions build — see the **Actions** tab or **Releases** for the latest automated build.

## Contributing

Issues and pull requests are welcome. Please avoid committing `.env`, keystores, or any `google-services.json` — these are already excluded via `.gitignore`.

## License

Licensed under the MIT License — see [LICENSE](LICENSE) for details.

---

Developed by [Jayasurya Bairedla](https://github.com/Surya762).
