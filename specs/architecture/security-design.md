# Security Design

## Authentication
- Use JWT for stateless authentication.
- Store only the JWT secret or key material on the backend.
- Tokens must include expiry and user role claims.
- All protected endpoints validate JWT signature and expiration.

## Authorization
- Role-based access control with `USER` and `ADMIN` roles.
- Enforce authorization at API layer before executing business actions.
- Admin-only endpoints for movie catalog management, duplicate resolution, and audit access.

## Password Security
- Hash passwords using BCrypt before persistence.
- Configure BCrypt with a strong cost factor appropriate for production.
- Never log raw passwords or sensitive authentication details.

## API Security
- Require HTTPS for all frontend/backend communication.
- Validate and sanitize all input payloads.
- Return structured error messages without exposing internal details.
- Implement CORS policy allowing only authorized frontend origins.
- Apply rate limiting or throttling on authentication and search endpoints to protect against abuse.

## Data Protection
- Do not log sensitive values such as passwords or JWT tokens.
- Encrypt PostgreSQL credentials at rest in configuration management.
- Use database roles and least privilege for connections.
- Store audit logs separately from user-provided content if possible.

## Operational Security
- Monitor authentication failures and abnormal access patterns.
- Log audit events for admin actions, review/submission changes, and duplicate prevention decisions.
- Ensure backend services can be deployed independently and updated without impacting frontend availability.
