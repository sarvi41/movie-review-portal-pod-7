# Service Boundaries

## Frontend Boundary
- Responsible for user interaction, display of movies, reviews, ratings, and search results.
- Communicates with backend strictly through REST APIs.
- Handles routing, state management, and client-side validation.
- Does not manage business rules such as duplicate prevention or audit logging.

## Backend Boundary
- Implements all business logic and enforces requirements from the BRD.
- Exposes RESTful endpoints for authentication, movie CRUD, reviews, ratings, search, and administration.
- Validates input, applies authorization rules, and formats responses for the frontend.
- Does not serve static UI content as part of core API behavior.

## Data Boundary
- PostgreSQL stores authoritative data for users, roles, movies, reviews, ratings, genres, and audit logs.
- The backend owns data access and schema rules.
- The frontend never accesses the database directly.

## Domain Boundaries
- Authentication Domain: user login, registration, JWT issuance, password hashing, role validation.
- Movie Domain: movie metadata lifecycle, duplicate detection, metadata validation.
- Review Domain: review creation, editing, auditability, rating aggregation.
- Search Domain: query execution, filtering, sorting, performance optimization.
- Administration Domain: admin-only catalog controls, audit oversight, role-based access.
