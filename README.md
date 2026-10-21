![Findroid banner](images/findroid-banner.png)

# Findroid CE (Community Edition)

A community-driven fork of [Findroid](https://github.com/jarnedemeulemeester/findroid) — the native
Jellyfin client for Android. Think of it as a **bleeding-edge channel**: a place to move a little
faster, try out community contributions, and experiment with new features.

## Why Findroid CE?

Findroid is a fantastic app and the foundation for everything here. CE isn't trying to replace it —
it's a faster-moving companion for people who want the latest community work sooner.

- 🚀 **Moves faster** — merges community PRs and ships new features without waiting on the upstream release cadence.
- 🔧 **Adds CE-only enhancements** — a rewritten download engine, Dolby Vision playback, richer
  subtitle support, and more (see below).
- 📦 **Stays current** — dependencies and the Jellyfin SDK are kept up to date via Renovate.
- 🤝 **Two-way street** — we sync improvements *from* upstream regularly, and upstream is welcome to
  take anything from here. It's all GPLv3.
- 📲 **Installs side-by-side** — a separate app ID (`nl.midasvo.findroid.ce`) means you can run it
  next to the original and switch back any time.

If you like living a bit closer to the edge, CE gets community features into your hands sooner. If you
prefer rock-solid stability, stick with upstream — and know that the good stuff here can flow back to it.

## What you get over stock Findroid

### Downloads

- **OkHttp resumable download engine** — replaces Android's `DownloadManager` for reliable, resumable downloads (including SD card support)
- **Per-episode download progress & inline buttons** — real-time status (pending, downloading, completed, failed) and tap-to-download/delete right in the season episode list, Netflix/Disney+ style
- **Bulk season & series download** — download an entire season or series with one tap, with queued concurrent downloading
- **Configurable max concurrent downloads** — limit how many episodes download at once (default: 2)
- **Season download status on the show screen** — see "3/10 downloaded" per season in the season selection dialog
- **Redesigned downloads screen** — active downloads with progress on top, completed items below, storage usage at the bottom

### Playback & player

- **Dolby Vision transcoding** — DV content plays everywhere via device-profile-aware transcoding
- **Wider subtitle delivery** — PGS, VobSub, and DVB subtitles delivered without transcoding
- **Configurable subtitle styling** — colors, outline, font, and size
- **Media segments** — per-type skip / ask / ignore actions
- **Chapters in the player UI**
- **"Are you still watching?"** inactivity prompt
- **Trickplay** loader behind a developer toggle

### Library & UI

- **Hide-watched filter** in the library view — persisted and shared across libraries
- **Long-press to copy text** on the movie/show/episode/season/person detail screens
- **Auto offline mode** — automatically enabled when there is no connectivity

### Diagnostics

- **Export device profile** from the About screen — handy for debugging playback/transcoding

See the [changelog](CHANGELOG.md) for the full release-by-release history.

## Install

### Via Obtainium (recommended)

Add this repo URL in [Obtainium](https://github.com/ImranR98/Obtainium):

```
https://github.com/midasvo/findroid-ce
```

Obtainium then checks for new releases automatically and notifies you of updates.

### Manual

Grab the latest APK from the [Releases](https://github.com/midasvo/findroid-ce/releases) page.

> Findroid CE uses its own application ID (`nl.midasvo.findroid.ce`), so it installs alongside the
> original Findroid rather than replacing it.

## Build from source

Use JDK 21 (the same version as CI), Android SDK Platform 37, and Android SDK Build-Tools 37.0.0.
Install the SDK packages through Android Studio's SDK Manager and accept their licences. The
repository includes the Gradle wrapper, so a separate Gradle installation is not needed.

Set `JAVA_HOME` to your JDK 21 installation and `ANDROID_HOME` to your Android SDK directory.
Alternatively, put `sdk.dir=/absolute/path/to/Android/Sdk` in an untracked `local.properties` file.
On Arch Linux, JDK 21 is normally installed at `/usr/lib/jvm/java-21-openjdk`.

```bash
# Build debug APKs for both phone and TV.
./gradlew assembleDebug

# Build only the phone app.
./gradlew :app:phone:assembleLibreDebug

# Run the existing unit tests.
./gradlew :core:testLibreDebugUnitTest :data:testDebugUnitTest \
  :modes:film:testDebugUnitTest :player:local:testDebugUnitTest
```

The first build downloads Gradle and Maven dependencies. Debug APKs are signed with a local debug
key and written to `app/phone/build/outputs/apk/libre/debug/` and
`app/tv/build/outputs/apk/libre/debug/`, with one APK per CPU architecture. Build directories,
Gradle/Kotlin caches, and `local.properties` are ignored by Git. Use the `arm64-v8a` APK for most
current Android devices. Release signing is configured separately using
`keystore.properties.example` or the environment variables in the release workflow.

## Built on Findroid

Findroid CE is a fork — all the core functionality (the native UI, ExoPlayer & mpv playback, offline
downloads, picture-in-picture, etc.) comes from upstream Findroid. We sync with it regularly; the
process is documented in [docs/upstream-sync.md](docs/upstream-sync.md).

**All credit** to [Jarne Demeulemeester](https://github.com/jarnedemeulemeester) and the
[Findroid contributors](https://github.com/jarnedemeulemeester/findroid/graphs/contributors) for
building the app this fork stands on.

## License

GPLv3 — same as upstream. See [LICENSE](LICENSE).

The logo is a combination of the Jellyfin logo and the Android robot. The Android robot is reproduced
or modified from work created and shared by Google and used according to terms described in the
Creative Commons 3.0 Attribution License.
