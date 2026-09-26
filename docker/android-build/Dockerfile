# Build environment matching CI (.github/workflows/build.yaml): JDK 21 + Android SDK.
# Keep the SDK versions in sync with buildSrc/src/main/kotlin/Versions.kt.
# Used by scripts/docker-gradle.sh; see CLAUDE.md.
FROM eclipse-temurin:21-jdk

ARG CMDLINE_TOOLS=13114758
ARG PLATFORM=android-37.0
ARG BUILD_TOOLS=37.0.0

RUN apt-get update && apt-get install -y --no-install-recommends unzip git \
 && rm -rf /var/lib/apt/lists/*

ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

RUN mkdir -p $ANDROID_HOME/cmdline-tools && cd /tmp \
 && curl -fsSL -o tools.zip https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS}_latest.zip \
 && unzip -q tools.zip && mv cmdline-tools $ANDROID_HOME/cmdline-tools/latest && rm tools.zip

# World-writable so the unprivileged build user can let AGP fetch anything missing.
RUN yes | sdkmanager --licenses >/dev/null \
 && sdkmanager "platforms;${PLATFORM}" "build-tools;${BUILD_TOOLS}" "platform-tools" >/dev/null \
 && chmod -R a+rwX $ANDROID_HOME
