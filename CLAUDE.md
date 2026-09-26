# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug APK (most common during development)
./gradlew :app:phone:assembleLibreDebug

# Release APK
./gradlew :app:phone:assembleLibreRelease

# Compile only (faster feedback, no APK)
./gradlew :app:phone:compileLibreDebugKotlin
./gradlew :modes:film:compileDebugSources

# Full build (all modules)
./gradlew build

# Clean
./gradlew clean
```

## Checks and Tests (run in Docker)

Run Gradle through `scripts/docker-gradle.sh` so you don't need a local JDK or Android SDK. It builds and caches a JDK 21 + Android SDK image from `docker/android-build/Dockerfile` (the same JDK as CI) and passes its arguments to `./gradlew`. It builds whichever checkout or worktree you run it from, and it finds the Dockerfile relative to itself, so you can also run it from another worktree against a branch that doesn't contain it.

```bash
# What CI runs (lint.yaml + build.yaml) — run before pushing
scripts/docker-gradle.sh ktfmtCheck assembleDebug

# Unit tests (core is the only library module with the libre flavor)
scripts/docker-gradle.sh testDebugUnitTest :core:testLibreDebugUnitTest

# Fix formatting
scripts/docker-gradle.sh ktfmtFormat
```

When `COMPILE_SDK` or `BUILD_TOOLS` in `buildSrc/src/main/kotlin/Versions.kt` changes, update the `PLATFORM` / `BUILD_TOOLS` args in the Dockerfile to match. The script tags the image by the Dockerfile's hash, so it rebuilds automatically. The Gradle cache persists in `~/.cache/findroid-docker/gradle`.

## Module Structure

```
app/phone        — Phone UI: screens, navigation, Compose layouts
app/tv           — TV UI: separate screen implementations for Android TV
core             — Shared DI modules, DownloaderViewModel, MainViewModel, common Composables
data             — JellyfinRepository interface + Room database + online/offline implementations
modes/film       — Feature ViewModels and State/Action classes for home, library, search, episode, movie, show, season
player/local     — ExoPlayer + mpv PlayerViewModel and playback logic
setup            — Server discovery and login onboarding flow
settings         — AppPreferences (DataStore) and settings screen
```

The `app/phone` module depends on all other modules. ViewModels live in `modes/film`, `setup`, `settings`, and `player/local` — NOT in `app/phone`. Screens in `app/phone` consume state from those ViewModels.

## Architecture

**MVI-style MVVM with Unidirectional Data Flow:**

Each screen follows this pattern:
- `FooState` — immutable data class holding all UI state
- `FooAction` — sealed interface of user intents
- `FooViewModel` — `@HiltViewModel`, exposes `StateFlow<FooState>`, handles `onAction()`
- `FooScreen` — stateful Composable that wires ViewModel to layout
- `FooScreenLayout` — stateless Composable that takes state + callbacks (used for previews)

Events that fire once (navigation, toasts) use a `Channel<FooEvent>` exposed as `Flow`, observed via `ObserveAsEvents()`.

**Dependency Injection:** Hilt throughout. DI modules are in `core/src/main/java/dev/jdtech/jellyfin/di/`.

**Navigation:** Type-safe Compose Navigation with `@Serializable` route objects. `NavigationRoot.kt` in `app/phone` is the single source of truth for the nav graph.

**Offline mode:** A `LocalOfflineMode` CompositionLocal toggles the app between online (`JellyfinRepositoryImpl`) and offline (`OfflineJellyfinRepositoryImpl`) data sources. The repository is switched at the DI level.

## Key Patterns

**Downloading:** `Downloader` interface (in `core`) is injected into ViewModels that need it. `DownloaderViewModel` (also in `core`) handles progress polling via `Handler` and exposes `DownloaderState`. Storage index is read from `AppPreferences.downloadStorageIndex` — there is no per-download dialog.

**ItemButtonsBar:** The shared download/play/favorite buttons component. Accepts `canDownload` and `isDownloaded` overrides so aggregate items (seasons, shows) can pass computed values instead of deriving from the item directly.

**Build variants:** Only one flavor (`libre`). Build types are `debug`, `release`, `staging`. Always use `Libre` variants (e.g. `assembleLibreDebug`, `compileLibreDebugKotlin`).

**App ID:** `nl.midasvo.findroid.ce` (this is a fork — do not revert to the upstream `dev.jdtech.jellyfin` ID).
