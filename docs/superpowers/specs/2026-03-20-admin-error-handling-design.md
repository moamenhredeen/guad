# Admin Error Handling Design

## Problem

The admin MVC panel has no structured error handling. Controllers redirect to a nonexistent `/admin/not-found` route. The single `error.html` template doesn't use the admin layout. Non-admin users hitting the admin panel after the ADMIN role restriction get a raw whitelabel error page. Non-technical users will use the admin panel, so errors need to be clear and friendly.

## Approach

Combine an admin-scoped `@ControllerAdvice` for application errors with a custom `ErrorController` for infrastructure errors (403 from Spring Security, unexpected 500s).

## Components

### 1. `AdminExceptionHandler` (`@ControllerAdvice`)

Scoped using `@ControllerAdvice(annotations = Controller.class)` so it only applies to `@Controller`-annotated classes (admin controllers), not `@RestController` ones. Takes `@Order(Ordered.HIGHEST_PRECEDENCE)` to ensure it wins over `GlobalExceptionHandler` (which is `@RestControllerAdvice` — a specialization of `@ControllerAdvice` that also matches `@Controller` classes by default).

Additionally, scope `GlobalExceptionHandler` to `@RestControllerAdvice(annotations = RestController.class)` to make the separation explicit.

Handles:
- `ResourceNotFoundException` — renders `admin/error/404.html` with status 404
- `IllegalArgumentException` — renders `admin/error/error.html` with status 400

### 2. `AdminErrorController` (implements `ErrorController`)

Mapped to `/error`. Checks the original request path via `jakarta.servlet.RequestDispatcher.ERROR_REQUEST_URI`:

- Admin requests (`/admin/**`): resolves status code, renders the matching Thymeleaf error template using the admin layout
  - 403 → `admin/error/403.html`
  - 404 → `admin/error/404.html`
  - Other → `admin/error/error.html`
- API requests: returns JSON `ApiError` using the existing `ApiError.of()` factory method

**Security note:** Add `/error` to the admin security filter chain's `securityMatcher` and `permitAll()` it, so that forwarded errors (especially 403s from unauthenticated users) have proper security context and can render the admin templates.

### 3. Error templates

All use `admin/layout.html` fragment:

- `admin/error/403.html` — permission denied, link to `/admin/auth/login`
- `admin/error/404.html` — resource not found, link to dashboard
- `admin/error/error.html` — generic fallback showing status, link to dashboard

### 4. Controller cleanup

Replace `if (empty) return "redirect:/admin/not-found"` with `.orElseThrow(() -> new ResourceNotFoundException(...))` across all 7 admin controllers (area, project, context, action, document, attachment, inbox) in their `details` and `deleteForm` methods.

## Files to create

- `app.guad.core.AdminExceptionHandler`
- `app.guad.core.AdminErrorController`
- `templates/admin/error/403.html`
- `templates/admin/error/404.html`
- `templates/admin/error/error.html`

## Files to modify

- `SecurityConfiguration` — add `/error` to admin filter chain matcher and `permitAll()`
- `GlobalExceptionHandler` — scope to `@RestControllerAdvice(annotations = RestController.class)`
- `AreaAdminController` — 2 methods
- `ProjectAdminController` — 2 methods
- `ContextAdminController` — 2 methods
- `ActionAdminController` — 2 methods
- `DocumentAdminController` — 2 methods
- `AttachmentAdminController` — 3 methods (details, deleteForm, delete)
- `InboxAdminController` — 2 methods

## Files to delete

- `templates/error.html` — replaced by admin error templates + JSON API errors. The app only serves `/admin/**` and `/api/**` paths, so no other routes need a fallback.
