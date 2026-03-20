# Guad

A GTD (Getting Things Done) task management system built as a multiplatform application with a Kotlin Multiplatform mobile app, Spring Boot backend, and Vue 3 web app.

*"Guad"* means *"good"* in Swabian — the dialect of Baden-Württemberg, Germany.

## Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Mobile    │    │     Web     │    │   Backend   │
│  (KMP +     │───▶│  (Vue 3 +   │───▶│ (Spring     │
│  Compose)   │    │  TypeScript)│    │  Boot + JPA)│
└─────────────┘    └─────────────┘    └─────────────┘
                                            │
                                      ┌─────┴─────┐
                                      │ PostgreSQL │
                                      │ + Keycloak │
                                      └───────────┘
```

## Tech Stack

| Component | Stack |
|-----------|-------|
| **Mobile** (`/app`) | Kotlin Multiplatform, Jetpack Compose Multiplatform, Clean Architecture |
| **Backend** (`/backend`) | Spring Boot 4, Java 25, PostgreSQL, Flyway, OAuth2 + Keycloak |
| **Web** (`/web`) | Vue 3, TypeScript, Vite, Tailwind CSS 4, Reka UI, Pinia |

## GTD Domain

The app implements the GTD methodology with these core entities:

- **Inbox Items** — Capture point for all incoming thoughts and tasks
- **Actions** — Concrete next steps (next action, waiting for, scheduled, someday/maybe)
- **Projects** — Multi-step outcomes organized by area
- **Contexts** — Location/tool tags (e.g., @home, @computer)
- **Areas** — Ongoing life/work domains (e.g., Health, Career)
- **Reviews** — Structured review cycles (daily, weekly, monthly)

## Project Structure

```
guad/
├── app/                    # Kotlin Multiplatform mobile app
│   ├── androidApp/         # Android entry point
│   ├── iosApp/             # iOS entry point
│   ├── core/               # Shared layers (presentation, domain, data, designsystem)
│   └── feature/            # Feature modules (auth, gtd)
├── backend/                # Spring Boot REST API
│   └── src/
└── web/                    # Vue 3 web application
    └── src/
```

## Getting Started

### Backend

Requires Java 25+, Maven 3+, PostgreSQL, and a Keycloak instance.

```bash
cd backend
./mvnw spring-boot:run
```

See [`backend/README.md`](backend/README.md) for API documentation and database schema.

### Web App

Requires Node.js 18+.

```bash
cd web
npm install
npm run dev
```

### Mobile App

Requires Android Studio with KMP plugin or Xcode for iOS.

```bash
cd app
./gradlew :androidApp:installDebug   # Android
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode.

## Authentication

All components authenticate via **Keycloak** using OAuth2/OIDC:

- **Backend** — OAuth2 Resource Server validating JWT tokens
- **Web** — Authorization Code Flow with PKCE
- **Mobile** — Authorization Code Flow with PKCE (RFC 8252 compliant)

## Documentation

- [`web/README.md`](web/README.md) — GTD domain model and use cases
- [`backend/README.md`](backend/README.md) — API documentation and database schema
- [`app/auth_plan.md`](app/auth_plan.md) — Mobile authentication architecture

## Design Identity

Inspired by Baden-Württemberg corporate design with teal and yellow accents, warm typography, and Swabian dialect in UI copy (*"Alles guad!"*, *"Des passt!"*).
