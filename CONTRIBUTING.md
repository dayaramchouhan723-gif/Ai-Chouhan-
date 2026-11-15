# Contributing to CHOUHAN STAR

Thank you for helping improve CHOUHAN STAR!

## Development Rules
1. Maintain pure Native Android Kotlin + Jetpack Compose + Gradle architecture (`com.chouhan.star.security`).
2. Keep `signingConfigs` in `app/build.gradle.kts` and `debug.keystore` untouched.
3. Never hardcode API keys or secrets; store credentials exclusively via `SecurityPreferences` (AES-256-GCM).
4. Ensure all user-facing sensitive actions (`PAY`, `SEND`, `DELETE`) route through `SafetyConfirmationOverlay`.
5. Run unit and Robolectric tests before pushing changes:
   ```bash
   gradle :app:testDebugUnitTest
   ```
