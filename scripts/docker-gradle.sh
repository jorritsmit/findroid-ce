#!/usr/bin/env bash
# Run Gradle in the pinned JDK 21 + Android SDK container (docker/android-build).
# No local JDK or Android SDK needed.
#
# Builds the checkout you run it from (main checkout or any git worktree). The image
# comes from the Dockerfile next to this script, so the script can live in a separate
# worktree and build branches that don't contain it:
#
#   scripts/docker-gradle.sh ktfmtCheck assembleDebug
#   ../findroid-ce-tools/scripts/docker-gradle.sh ktfmtCheck assembleDebug
set -euo pipefail

root=$(git rev-parse --show-toplevel)
common=$(cd "$root" && cd "$(git rev-parse --git-common-dir)" && pwd)

# Tag by Dockerfile hash so edits to it rebuild the image automatically.
dockerfile_dir="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")/../docker/android-build" && pwd)"
image="findroid-android-build:$(sha256sum "$dockerfile_dir/Dockerfile" | cut -c1-12)"
if ! docker image inspect "$image" >/dev/null 2>&1; then
  docker build -t "$image" "$dockerfile_dir"
fi

cache="${XDG_CACHE_HOME:-$HOME/.cache}/findroid-docker/gradle"
mkdir -p "$cache"

# Mount at identical paths; a worktree's .git file points into the main repo's .git dir.
mounts=(-v "$root:$root")
[[ "$common" != "$root/.git" ]] && mounts+=(-v "$common:$common")

tty=()
[[ -t 0 && -t 1 ]] && tty=(-it)

exec docker run --rm "${tty[@]}" \
  --user "$(id -u):$(id -g)" -e HOME=/tmp -e GRADLE_USER_HOME=/gradle \
  -v "$cache:/gradle" "${mounts[@]}" -w "$root" \
  "$image" ./gradlew --no-daemon "$@"
