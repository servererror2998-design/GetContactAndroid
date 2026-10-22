# GetContactAndroid

Android native Kotlin + Jetpack Compose scaffold for phone lookup UI, local history, and secure credential storage.

## Important status
This repository is a buildable UI scaffold, **not a working GetContact API client**. The upstream CLI uses a private, unofficial API which may change and may be restricted by the service's terms. The app's API adapter intentionally returns a clear "not configured" error until an authorized, documented integration is available. No API compatibility or successful build is claimed until GitHub Actions completes.

## Features in this scaffold
- Compose UI: profile, tags, and quota tabs
- Local search history (up to 50 entries)
- Token/finalKey storage encrypted with AES-GCM and Android Keystore
- GitHub Actions workflow to build a debug APK on push or manual dispatch

## Build APK
1. Open the **Actions** tab in this repository.
2. Select **Build Android APK** and click **Run workflow**, or push a commit.
3. Wait for the workflow to finish successfully.
4. Open the completed run and download the `GetContactAndroid-debug-apk` artifact.
5. Extract the artifact ZIP to get `app-debug.apk`.

## Local build
Open the repository root in Android Studio with JDK 17 and Android SDK Platform 35, allow Gradle sync, then select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Security
- Do not share your session token or finalKey.
- Credentials are encrypted at rest with Android Keystore.
- Search history is local but not encrypted; clear it when no longer needed.
- Use only accounts you own and data you are authorized to access.
