# User Profile & Registration Design

## Context

Guad uses Keycloak as its IAM. All domain entities store a `UUID userId` (the Keycloak subject ID) for data isolation, but no local user/profile entity exists. Users are currently created manually in Keycloak.

This design introduces:
1. A local `UserProfile` entity for Guad-specific preferences
2. Self-service email-based registration via Keycloak's built-in registration flow

## 1. UserProfile Entity

### Schema

| Field | Type | Notes |
|-------|------|-------|
| `id` | Long | PK, sequence-generated |
| `keycloakId` | UUID | Unique, not null — Keycloak subject ID |
| `email` | String | Cached from JWT, updated on login |
| `displayName` | String | Cached from JWT, updated on login |
| `timezone` | String | Default: `Europe/Berlin` |
| `defaultReviewDay` | DayOfWeek | Default: `SATURDAY` |
| `energyTrackingEnabled` | boolean | Default: `true` |
| `emailDigestsEnabled` | boolean | Default: `false` |
| `reminderNotificationsEnabled` | boolean | Default: `true` |
| `createdDate` | Instant | Set on first login |
| `updatedDate` | Instant | Updated on profile changes |

### Design Decisions

- **Long PK over Keycloak UUID as PK:** Keeps PK strategy consistent with all other entities (Action, Project, Area, etc.) and decouples identity from the external IdP.
- **`keycloakId` as unique column:** Links to the `UUID userId` stored on domain entities. The interceptor looks up profiles by this field.
- **Cached email/displayName:** Avoids needing to parse the JWT or call Keycloak when displaying user info. Updated on each authenticated request to stay reasonably fresh.

### Lazy Creation

A Spring `HandlerInterceptor` runs on authenticated API requests:

1. Extract Keycloak subject UUID from the JWT
2. Look up `UserProfile` by `keycloakId`
3. If not found → create a new profile with defaults and JWT-derived fields (email, displayName)
4. If found → update cached email/displayName if they've changed in the JWT
5. Make the profile available to downstream handlers (e.g., via request attribute)

Performance: The lookup is a single indexed query on `keycloakId` (unique constraint = index). For subsequent requests in the same session, a short-lived cache or request-scoped flag avoids redundant queries.

## 2. Keycloak Registration

### Configuration

Enable in Keycloak Admin Console:
- **Realm → Login → User registration:** ON
- **Realm → Login → Verify email:** ON
- **Realm → Email:** Configure SMTP settings for verification emails

### Custom Theme

Create a Keycloak login theme at `themes/guad/login/` with Guad's branding:
- Logo, colors, fonts matching the Guad frontend
- Applied via Realm → Themes → Login theme → `guad`

### User Flow

1. User visits Guad → clicks "Sign up" → redirected to Keycloak's themed registration page
2. User fills in email + password → Keycloak sends verification email
3. User clicks verification link → redirected back to Guad → authenticated via OIDC
4. Interceptor detects first login → creates `UserProfile` with defaults

### What Guad Does NOT Handle

- Registration form rendering (Keycloak's themed page)
- Email verification (Keycloak's built-in flow)
- Password policies (configured in Keycloak)
- Account recovery (Keycloak's built-in flow)

## 3. Profile API

### Endpoints

| Method | Path | Purpose |
|--------|------|---------|
| `GET` | `/api/profile` | Get current user's profile |
| `PUT` | `/api/profile/settings` | Update mutable preferences |

### DTOs

**`ProfileResponse`** — all profile fields:
- `id`, `email`, `displayName`, `timezone`, `defaultReviewDay`, `energyTrackingEnabled`, `emailDigestsEnabled`, `reminderNotificationsEnabled`, `createdDate`, `updatedDate`

**`UpdateSettingsRequest`** — mutable settings only:
- `timezone`, `defaultReviewDay`, `energyTrackingEnabled`, `emailDigestsEnabled`, `reminderNotificationsEnabled`

Email and displayName are not updatable via this endpoint — they are managed in Keycloak and synced on login.

### Package Structure

Lives in `app.guad.feature.profile`, following the existing feature-per-package convention:
- `UserProfile.java` — JPA entity
- `UserProfileRepository.java` — Spring Data repository
- `ProfileService.java` — business logic, lazy creation
- `ProfileController.java` — REST endpoints
- `ProfileResponse.java` — response DTO
- `UpdateSettingsRequest.java` — request DTO

### Scope

- No admin endpoints for listing/managing other profiles
- No delete endpoint — profile lifecycle follows Keycloak account lifecycle
- Profile is always scoped to the authenticated user

## 4. Database Migration

Flyway migration `V3__User_profile.sql`:

```sql
CREATE SEQUENCE user_profiles_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE user_profiles (
    id               BIGINT       NOT NULL DEFAULT nextval('user_profiles_seq'),
    keycloak_id      UUID         NOT NULL,
    email            VARCHAR(255) NOT NULL,
    display_name     VARCHAR(255) NOT NULL,
    timezone         VARCHAR(50)  NOT NULL DEFAULT 'Europe/Berlin',
    default_review_day VARCHAR(10) NOT NULL DEFAULT 'SATURDAY',
    energy_tracking_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_digests_enabled   BOOLEAN NOT NULL DEFAULT FALSE,
    reminder_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_date     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_date     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    CONSTRAINT uq_user_profiles_keycloak_id UNIQUE (keycloak_id)
);
```
