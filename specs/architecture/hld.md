# High-Level Design

## Architecture Overview
The Movie Review Portal is a two-tier application with an Angular 19 frontend and a Spring Boot 3 backend. The backend exposes RESTful APIs secured with JWT authentication and manages persistence in PostgreSQL. The system is designed as an API-first, stateless backend with clear separation between presentation, business logic, and data layers.

## Key Design Principles
- API-first design for frontend/backend decoupling.
- Separation of concerns between authentication, movie management, review management, search, and administration.
- Stateless backend services to support horizontal scaling and independent deployment.
- Secure-by-default configuration with strong password hashing, JWT validation, and sensitive data protection.
- Data quality enforcement with duplicate prevention and auditability.

## Deployment Model
- Frontend: Angular 19 SPA hosted independently (static content via CDN or web server).
- Backend: Spring Boot 3 application deployed as a JVM service or container.
- Database: PostgreSQL instance with secure network access.
- Authentication: JWTs issued by the backend and validated on each request.
