# Implementation Plan - API Keys Management (Gemini, Groq, OpenAI, Claude)

## User Review Required
> [!IMPORTANT]
> - Supports secure local storage via Android `EncryptedSharedPreferences` for Gemini, Groq, OpenAI, and Claude API keys.
> - Adds live connection test verification when saving each API key.
> - Integrates UI inputs, ViewModel persistence, and test triggers.

## Proposed Changes

### 1. Security Preferences (`SecurityPreferences.kt`)
- Add fields/getters/setters for:
  - `groqKey`
  - `claudeKey`
  - (Existing `geminiKey` and `openAiKey`)
- Ensure all keys are stored encrypted via `EncryptedSharedPreferences`.

### 2. ViewModel (`StarSecurityViewModel.kt`)
- Update UI state and methods:
  - `groqKey`, `claudeKey` added to `SecurityUiState`.
  - Methods to save and test API keys: `saveGroqKey(String)`, `saveClaudeKey(String)`, `saveOpenAiKey(String)`, `saveGeminiKey(String)`, `testApiKey(String, String)`.

### 3. API Screen (`ApiScreen.kt`)
- Expand `ApiScreen` LazyColumn fields to include:
  - Gemini API Key field + Test & Save button
  - Groq API Key field + Test & Save button
  - OpenAI API Key field + Test & Save button
  - Claude API Key field + Test & Save button
  - Custom API URL field
- Add status feedback indicators (e.g. "Key Verified & Saved Encrypted").

## Verification Plan
- Run `compile_applet` to verify zero compilation errors.
- Run `gradle testDebugUnitTest` to verify test suite passes.
