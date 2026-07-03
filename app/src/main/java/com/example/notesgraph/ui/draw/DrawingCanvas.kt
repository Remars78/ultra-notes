name: Android CI

on:
  push:
    branches: [ main, master ]
  pull_request:
  workflow_dispatch:

permissions:
  contents: read

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Ensure Gradle wrapper
        run: |
          if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
            echo "Wrapper jar missing, generating..."
            gradle wrapper --gradle-version 8.7
          fi
          chmod +x ./gradlew || true

      # Собираем и одновременно пишем весь вывод в build.log
      - name: Build debug APK
        id: build
        run: |
          set -o pipefail
          ./gradlew assembleDebug --stacktrace --console=plain 2>&1 | tee build.log

      # Если сборка упала — печатаем ТОЛЬКО строки с реальными ошибками
      - name: Show compile errors
        if: failure()
        run: |
          echo "================ ONLY ERROR LINES ================"
          grep -nE "^e:|error:|\[ksp\]|\[Room\]" build.log || echo "No e:/error: lines found"

      - name: Upload full log
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: build-log
          path: build.log
          if-no-files-found: warn

      - name: Upload APK
        if: success()
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/*.apk
          if-no-files-found: warn
