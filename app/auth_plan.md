# Authentication Implementation Plan for Guad Mobile App

## Problem Statement

The Guad app is a Kotlin Multiplatform (KMP) project with a Spring Boot backend protected by Spring OAuth2 Resource Server + Keycloak as the authorization server. The current auth feature has a skeleton registration UI (RegisterScreen) but no domain logic, data layer, or actual authentication flow. We need to implement the proper authentication approach for a mobile app in this OAuth2/Keycloak architecture, including Google and Apple social sign-in.

## Recommended Approach: Authorization Code Flow with PKCE (Browser-Based)

### Why NOT a custom native registration/login form?

The current `RegisterScreen` collects username, email, and password directly in a native form. This approach is **not recommended** for OAuth2/Keycloak mobile apps because:

1. **Security risk**: The app would handle user credentials directly, violating RFC 8252 and OAuth2 best practices
2. **Requires exposing Keycloak Admin API** or building a custom registration backend proxy — both add attack surface
3. **Loses Keycloak features**: MFA, social login, email verification, password policies, account linking — all need reimplementation
4. **Maintenance burden**: Any Keycloak policy change requires mobile app updates

### What we should do instead

Per **RFC 8252** (OAuth 2.0 for Native Apps) and Keycloak's own recommendation:

1. Use **Authorization Code Flow with PKCE** — the industry standard for mobile apps
2. Delegate authentication/registration to **Keycloak's hosted pages** via a secure browser (Chrome Custom Tabs on Android, ASWebAuthenticationSession on iOS)
3. The app **never sees user credentials** — Keycloak handles everything
4. Registration happens on Keycloak's registration page (customizable with themes)
5. Google/Apple sign-in is handled by Keycloak as an Identity Provider broker

### Library Choice: `kotlin-multiplatform-oidc`

For KMP, the best fit is **kalinjul/kotlin-multiplatform-oidc** (`io.github.kalinjul.kotlin.multiplatform:oidc-appsupport`):

- True KMP library — shared code for Android & iOS
- Supports Authorization Code with PKCE out-of-the-box
- Uses Chrome Custom Tabs (Android) / ASWebAuthenticationSession (iOS)
- Has Ktor integration (`oidc-ktor`) for authenticated API calls
- Actively maintained, version ~0.16.x

Alternative: **AppAuth Kotlin by Yet300** or **Lokksmith** — both are newer KMP OAuth libraries.

---

## App Auth Flow (Single Screen)

There is **one auth screen** — no separate sign-in vs sign-up screens. Registration and login are both handled by Keycloak's hosted pages. The app simply needs to know: "is the user authenticated or not?"

### App Startup Flow

```
App Starts
    │
    ▼
Check secure token storage (EncryptedPrefs / Keychain)
    │
    ├── Tokens exist
    │       │
    │       ▼
    │   Is network available?
    │       │
    │       ├── Yes → Validate access token (check expiry)
    │       │           │
    │       │           ├── Not expired → Verify with server (/userinfo)
    │       │           │       │
    │       │           │       ├── Valid → Home screen (online)
    │       │           │       ├── 401 → Try refresh (max 3 retries)
    │       │           │       │           │
    │       │           │       │           ├── Success → Home screen
    │       │           │       │           └── Fail → Auth screen
    │       │           │       │
    │       │           │       └── Network/5xx error → Home screen (offline mode)
    │       │           │
    │       │           └── Expired → Try refresh (max 3 retries)
    │       │                       │
    │       │                       ├── Success → Home screen
    │       │                       └── Fail → Auth screen
    │       │
    │       └── No → Home screen (offline mode, sync when network returns)
    │
    └── No tokens → Auth screen
```

> **Offline mode:** If tokens exist but the server is unreachable, the user is allowed into the app. They already proved their identity when they originally signed in. Local tasks/data remain accessible via Room database. Changes are queued and synced when connectivity returns.

### Auth Screen Layout

```
┌─────────────────────────────────┐
│                                 │
│           [Guad Logo]           │
│                                 │
│        Welcome to Guad          │
│   Sign in to get started        │
│                                 │
│  ┌─────────────────────────┐    │
│  │  ● Continue with Google │    │  ← kc_idp_hint=google
│  └─────────────────────────┘    │
│                                 │
│  ┌─────────────────────────┐    │
│  │  Continue with Apple   │    │  ← kc_idp_hint=apple
│  └─────────────────────────┘    │
│                                 │
│        ─── or ───               │
│                                 │
│  ┌─────────────────────────┐    │
│  │  Sign in with Email     │    │  ← Opens Keycloak login page
│  └─────────────────────────┘    │    (has "Register" link)
│                                 │
│  By continuing, you agree to    │
│  our Terms and Privacy Policy   │
│                                 │
└─────────────────────────────────┘
```

### How the buttons work

| Button | What happens |
|---|---|
| **Continue with Google** | Opens Keycloak in browser with `kc_idp_hint=google` → user goes directly to Google's consent screen → redirected back to app with tokens. If first time, Keycloak auto-creates the user account. |
| **Continue with Apple** | Same as above with `kc_idp_hint=apple` → goes directly to Apple's sign-in. |
| **Sign in with Email** | Opens Keycloak's standard login page (no `kc_idp_hint`). User sees email/password form. If they don't have an account, they click "Register" on Keycloak's page. |

> **Note**: `kc_idp_hint` is a Keycloak-specific query parameter that skips the Keycloak login page and redirects directly to the specified Identity Provider. This gives a seamless native-like experience.
> 
> Whether the user is signing in for the first time or returning — same buttons, same flow. Keycloak handles the distinction.

---

## Implementation Todos

### 1. Replace Native Registration Screen

The current `RegisterScreen` with native username/email/password fields will be **replaced** with a single `AuthScreen`:
- **"Continue with Google"** button (branded, uses `kc_idp_hint=google`)
- **"Continue with Apple"** button (branded, uses `kc_idp_hint=apple`)
- **"Sign in with Email"** button (opens standard Keycloak login page, which has a "Register" link)
- App logo, welcome text, terms/privacy link
- All buttons trigger OIDC Authorization Code + PKCE flow via secure browser
- No separate sign-up/sign-in screens — one screen, Keycloak handles the rest

### 2. Add OIDC Library Dependency

Add `kotlin-multiplatform-oidc` to the version catalog and auth modules:
- `oidc-appsupport` in `core:data` or `feature:auth:data`
- `oidc-ktor` for authenticated Ktor HTTP client (integrates with existing `HttpClientFactory`)

### 3. Keycloak Configuration (Server-Side)

**Public Client:**
- Create a **public client** in Keycloak (no client secret — mobile apps can't keep secrets)
- Enable **Authorization Code flow with PKCE** (`S256` challenge method)
- Configure redirect URI: `https://guad.app/auth/callback` (claimed HTTPS URL via App Links / Universal Links)

> **Why HTTPS redirect URI?** Custom schemes like `guad://` can be registered by any app on the device, allowing a malicious app to intercept the authorization code. Claimed HTTPS URLs (App Links on Android, Universal Links on iOS) require domain ownership verification and are recommended by RFC 8252 §7.1.

**Identity Providers:**
- Add **Google** as an Identity Provider in Keycloak (Google OAuth client ID/secret configured in Keycloak, not the mobile app)
- Add **Apple** as an Identity Provider in Keycloak (Apple Sign In service configured in Keycloak)
- Enable "First Login Flow" to auto-create users on first social login
  - **Important:** Configure the First Broker Login flow to **require email verification** before linking to an existing account. This prevents account takeover where an attacker uses a social provider with a victim's email to hijack their email/password account.
  - Use Keycloak's "Review Profile" and "Verify Existing Account by Email" steps in the First Broker Login flow

**Registration:**
- Enable user registration in Keycloak realm settings (for email/password users)
- Optionally customize the Keycloak login/registration theme for Guad branding

### 4. Auth Domain Layer

Create domain models and contracts in `feature/auth/domain`:
- `AuthRepository` interface:
  - `signIn(idpHint: String?)` — start OIDC flow
  - `signOut()` — revoke tokens on Keycloak + end session + clear local storage
  - `observeAuthState(): Flow<AuthState>` — emit current auth state
  - `validateAndRestoreSession(): Result<Boolean, DataError>` — check stored tokens on startup, verify with server via `/userinfo`, attempt refresh if expired
  - `getAccessToken(): String?` — get current valid access token
- `AuthToken` model — holds access token, refresh token, expiry
- `AuthState` enum — `AUTHENTICATED`, `UNAUTHENTICATED`, `LOADING`
- `AuthProvider` enum — `GOOGLE`, `APPLE`, `EMAIL` (maps to `kc_idp_hint` values)
- Use cases: `SignInUseCase`, `SignOutUseCase`, `ObserveAuthStateUseCase`, `ValidateSessionUseCase`

### 5. Auth Data Layer

Implement repository and data sources in `feature/auth/data`:
- `OidcAuthRepository` — implements `AuthRepository` using the OIDC library
  - `signIn(idpHint)` → builds OIDC auth request with optional `kc_idp_hint` query param
  - `signOut()` → revoke refresh token via Keycloak's `/protocol/openid-connect/revoke` endpoint → OIDC end-session → clear local secure storage. Revoke **before** clearing locally so server-side session is terminated even if local clear fails.
  - `validateAndRestoreSession()` → check secure storage for tokens → validate expiry → verify with Keycloak `/userinfo` endpoint → try refresh if expired or revoked → return auth state
- `SecureTokenStorage` — **encrypted** token persistence:
  - **Android:** `EncryptedSharedPreferences` backed by Android Keystore
  - **iOS:** Keychain Services with `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`
  - Common interface via expect/actual or a KMP secure storage library (e.g., `multiplatform-settings` with encryption backend)
- Ktor auth interceptor — automatically attach Bearer tokens to API requests using `oidc-ktor`

### 6. Auth Presentation Layer

New files replacing the register screen in `feature/auth/presentation`:
- `AuthScreen.kt` — branded screen with Google/Apple/Email buttons + logo + terms
- `AuthViewModel.kt` — on init calls `validateAndRestoreSession()`, handles OIDC flow initiation with `kc_idp_hint`
- `AuthState.kt` — `isLoading` (checking token on startup), `isAuthenticated`, `error`
- `AuthAction.kt` — `OnGoogleSignInClick`, `OnAppleSignInClick`, `OnEmailSignInClick`

### 7. Platform-Specific Configuration

**Android:**
- Configure App Links: Add `assetlinks.json` on `guad.app` domain for redirect URI `https://guad.app/auth/callback`
- Add intent filter in `AndroidManifest.xml` with `android:autoVerify="true"` for the HTTPS redirect URI
- Chrome Custom Tabs dependency (usually included by the OIDC library)

**iOS:**
- Configure Universal Links: Add `apple-app-site-association` file on `guad.app` domain
- Add Associated Domains entitlement (`applinks:guad.app`) in Xcode
- ASWebAuthenticationSession integration (handled by the OIDC library)

### 8. Authenticated HTTP Client

Enhance `HttpClientFactory` to support authenticated requests:
- Use `oidc-ktor` plugin to auto-attach Bearer tokens
- Handle 401 responses with automatic token refresh
- Integrate with existing `safeCall()` error handling
- **Enforce HTTPS** for all API and Keycloak communication
- Consider **certificate pinning** in Ktor HttpClient for Keycloak and API endpoints to prevent MITM attacks (document certificate rotation strategy if used)

---

## Architecture Diagram

```
┌───────────────────────────────────────────────────────────────────────┐
│                          Mobile App (KMP)                             │
│                                                                       │
│  ┌────────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │ AuthScreen         │─▶│ AuthViewModel    │─▶│ AuthRepository   │  │
│  │                    │  │                  │  │ (Domain)         │  │
│  │ [Google] [Apple]   │  │ signIn(idpHint)  │  └────────┬─────────┘  │
│  │ [Email]            │  │ validateSession()│           │            │
│  └────────────────────┘  └──────────────────┘  ┌────────▼─────────┐  │
│                                               │ OidcAuthRepo     │  │
│                                               │ (Data Layer)     │  │
│                                               └──┬──────────┬────┘  │
│                                                  │          │       │
│  ┌────────────────────────┐  ┌───────────────────▼─┐  ┌────▼────┐  │
│  │ Ktor HttpClient        │  │ OIDC Library        │  │Encrypted│  │
│  │ + Bearer Token (HTTPS) │  │ (Auth Code + PKCE)  │  │Storage  │  │
│  └──────────┬─────────────┘  └──────────┬──────────┘  └─────────┘  │
│             │                           │                           │
└─────────────┼───────────────────────────┼───────────────────────────┘
              │                           │
              ▼                           ▼
    ┌─────────────────┐      ┌──────────────────────────────┐
    │ Spring Boot API │      │ Keycloak                     │
    │ (Resource Server│      │ (Authorization Server)       │
    │  validates JWT) │      │                              │
    └─────────────────┘      │  ┌────────┐  ┌───────────┐  │
                             │  │ Google │  │   Apple   │  │
                             │  │  IdP   │  │    IdP    │  │
                             │  └────────┘  └───────────┘  │
                             │  Login / Register Pages     │
                             └──────────────────────────────┘
```

## Flow Examples

### App Startup Flow
1. App starts → `AuthViewModel` calls `validateAndRestoreSession()`
2. Check encrypted storage for stored tokens
3. If no tokens → show `AuthScreen`
4. If tokens exist and **network available**:
   - Access token not expired → verify with Keycloak `/userinfo`
     - Valid → Home screen (online)
     - 401 → try refresh (max 3 retries, exponential backoff)
     - Network/5xx error → Home screen (offline mode)
   - Access token expired → try refresh (max 3 retries)
     - Success → Home screen
     - Fail (401) → Auth screen
5. If tokens exist and **no network** → Home screen (offline mode, sync later)

### Google Sign-In Flow
1. User taps **"Continue with Google"** on `AuthScreen`
2. App calls `signIn(idpHint = "google")`
3. OIDC library opens Keycloak auth URL with `&kc_idp_hint=google` in Chrome Custom Tabs / ASWebAuthenticationSession
4. Keycloak immediately redirects to Google's consent screen (skips Keycloak login page)
5. User authorizes with Google
6. Google redirects back to Keycloak → Keycloak creates/links user account
7. Keycloak redirects to `https://guad.app/auth/callback` with authorization code
8. OIDC library exchanges code for tokens (access + refresh + id_token)
9. Tokens stored in encrypted storage → user is authenticated

### Email Sign-In Flow
1. User taps **"Sign in with Email"**
2. App calls `signIn(idpHint = null)`
3. OIDC library opens Keycloak's standard login page in secure browser
4. User enters email/password, or clicks "Register" to create a new account
5. Keycloak redirects to `https://guad.app/auth/callback` → tokens stored in encrypted storage → authenticated

### Sign-Out Flow
1. App calls `signOut()`
2. Revoke refresh token via Keycloak's `/protocol/openid-connect/revoke` endpoint (server-side session terminated)
3. Call OIDC end-session endpoint
4. Clear tokens from encrypted local storage
5. Navigate to `AuthScreen`

### Authenticated API Call
1. App makes API call via Ktor HttpClient
2. `oidc-ktor` plugin auto-attaches `Authorization: Bearer <access_token>` header
3. Spring Boot Resource Server validates the JWT against Keycloak's public keys
4. If token expired, `oidc-ktor` auto-refreshes using the refresh token
5. If refresh fails after 3 retries (exponential backoff), force re-authentication

## Security Considerations

### Token Storage (Critical)
- **Never store tokens in plaintext.** DataStore/SharedPreferences are not encrypted by default.
- **Android:** Use `EncryptedSharedPreferences` backed by Android Keystore. Keys are hardware-backed on supported devices.
- **iOS:** Use Keychain Services with `kSecAttrAccessibleWhenUnlockedThisDeviceOnly` to prevent backup extraction.

### Token Revocation (High)
- On sign-out, **always revoke tokens server-side** before clearing locally. Call Keycloak's `/protocol/openid-connect/revoke` with the refresh token.
- This ensures the session is terminated even if the device is compromised after sign-out.

### Redirect URI Security (Medium)
- Use **App Links** (Android) and **Universal Links** (iOS) with a claimed HTTPS domain (`https://guad.app/auth/callback`) instead of custom URI schemes.
- Custom schemes (`guad://`) can be registered by any app — a malicious app could intercept the authorization code.
- Requires hosting `assetlinks.json` (Android) and `apple-app-site-association` (iOS) on the domain.

### Session Validation (Medium)
- On startup, don't just check token expiry locally — **verify with Keycloak's `/userinfo` endpoint** to catch server-side revocations (admin action, password change, etc.).
- Distinguish between "network error" (token may still be valid) and "401 unauthorized" (token is revoked) in the refresh/validation logic.

### Social Login Account Linking (Medium)
- Configure Keycloak's **First Broker Login flow** to require email verification before linking a social login to an existing account.
- Without this, an attacker with a Google/Apple account using a victim's email could take over their email/password account.

### Transport Security (Medium)
- All communication with Keycloak and the Spring Boot API **must use HTTPS**.
- Consider certificate pinning in the Ktor HttpClient for additional MITM protection.
- If pinning is used, document the certificate rotation strategy to avoid app lockouts.

### Token Refresh Retry Limits
- Cap automatic token refresh to **max 3 retries** with **exponential backoff** (e.g., 1s → 2s → 4s).
- After max retries, stop retrying and force the user to the auth screen.
- **Why this matters:** Without a cap, a revoked token or a Keycloak outage can cause an infinite retry loop: `API call → 401 → refresh → fail → retry API → 401 → refresh → fail...`. This wastes battery, floods the server, and freezes the UI. The fix is simple and prevents a common production bug in OAuth apps.
- Distinguish failure reasons in retry logic:
  - `400/401` from refresh endpoint → token is revoked, **stop immediately** and re-authenticate
  - Network error / timeout → server may be down, **retry with backoff** up to limit
  - `5xx` from refresh endpoint → server error, **retry with backoff** up to limit

### Offline Access Strategy
- The app must remain usable when the device is offline or the server is unreachable.
- Users should be able to **create tasks, read existing tasks**, and work normally offline.
- When connectivity returns, the app **syncs local changes** to the server.

**How this interacts with auth:**
- On startup with no network: if tokens exist locally (even if expired), **allow access to the app** in offline mode. The user is "authenticated" based on the locally stored session — they already proved their identity when they signed in.
- Do **not** redirect to the auth screen on network failure. Only redirect when tokens are genuinely missing or the server explicitly rejects them (401).
- Startup validation flow with offline support:

```
Tokens exist?
    │
    ├── No → Auth screen (regardless of network)
    │
    └── Yes → Is network available?
            │
            ├── Yes → Validate with server (/userinfo)
            │       │
            │       ├── Valid → Home screen (online mode)
            │       ├── 401 → Try refresh → fail → Auth screen
            │       └── 5xx → Home screen (offline mode, retry later)
            │
            └── No → Home screen (offline mode)
                      Queue sync when network returns
```

- **Offline data sync** is a broader architecture concern (beyond auth) — requires a local database (Room, already in version catalog), a sync queue, and conflict resolution. This will be addressed in a separate plan but the auth layer must support it by not blocking app access on network errors.

### Biometric Unlock
- After the user authenticates via OAuth (first time or re-auth), offer to enable **biometric unlock** (fingerprint / Face ID) for future app opens.
- This adds a second layer: even on an unlocked phone, an attacker can't open Guad without the user's biometric.

**How it works:**
- On successful OAuth sign-in, prompt: "Enable fingerprint/Face ID to unlock Guad?"
- If enabled, the encrypted token storage key is **protected behind biometric authentication**:
  - **Android:** Use `BiometricPrompt` + `KeyGenParameterSpec.Builder.setUserAuthenticationRequired(true)` on the Android Keystore key that encrypts tokens. The key can only be used after biometric verification.
  - **iOS:** Use Keychain with `kSecAttrAccessControl` set to `.biometryCurrentSet`. Tokens are only accessible after Face ID / Touch ID verification.
- On subsequent app opens (tokens already stored), the app shows a biometric prompt **before** decrypting tokens and entering the main screen.
- If biometric fails or is cancelled → show a "Use password" fallback (which triggers full OAuth re-authentication).
- If the user disables biometrics in system settings → fall back to normal encrypted storage (no biometric gate).

**Implementation notes:**
- This is **platform-specific** — requires expect/actual pattern in KMP
- Consider a `BiometricAuthManager` interface in domain, with Android/iOS implementations
- Biometric is optional and user-controlled — never force it
- Store a `biometricEnabled: Boolean` preference in DataStore (this pref itself doesn't need encryption)

### Security Headers — Nice to Have
- Add custom HTTP headers to every API request to help the backend detect anomalies and stolen tokens:
  - `X-Device-ID` — a unique, stable device identifier (generated once, stored locally). If a token is suddenly used from a different device ID, the backend can flag it.
  - `X-App-Version` — the app build version (e.g., `1.2.0`). Helps enforce minimum version requirements and detect requests from outdated/tampered apps.
  - `X-Platform` — `android` or `ios`. Combined with device ID, helps detect cross-platform token theft.
- **How the backend uses these:** The Spring Boot API can log these headers for audit trails. Optionally, it can implement policies like:
  - Reject tokens used from a different `X-Device-ID` than the one that originally authenticated (token binding)
  - Require app update if `X-App-Version` is below a minimum
  - Alert on suspicious patterns (same token from multiple devices/platforms)
- **Why "nice to have":** These headers are defense-in-depth. They don't prevent attacks on their own but make stolen tokens significantly harder to exploit undetected. Can be added incrementally — start by logging them on the backend, then add enforcement rules later.
- **Privacy note:** `X-Device-ID` should be an app-scoped random UUID, **not** a hardware identifier (IMEI, MAC address). It should be resettable by the user (e.g., clearing app data resets it).

## Key References

- [RFC 8252 — OAuth 2.0 for Native Apps](https://datatracker.ietf.org/doc/html/rfc8252)
- [RFC 7009 — OAuth 2.0 Token Revocation](https://datatracker.ietf.org/doc/html/rfc7009)
- [Keycloak Mobile App Flow Discussion](https://github.com/keycloak/keycloak/discussions/22530)
- [Keycloak Identity Provider Hint (`kc_idp_hint`)](https://www.keycloak.org/docs/latest/securing_apps/#_idp_hint)
- [kotlin-multiplatform-oidc](https://github.com/kalinjul/kotlin-multiplatform-oidc)
- [AppAuth Best Practices](https://appauth.io/)
- [Google Identity Provider in Keycloak](https://www.keycloak.org/docs/latest/server_admin/#google)
- [Apple Identity Provider in Keycloak](https://www.keycloak.org/docs/latest/server_admin/#apple)
