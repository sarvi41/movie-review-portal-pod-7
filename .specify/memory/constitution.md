# Movie Review Portal Constitution

## Product Principles

1. All movie ratings must be auditable.
2. Reviews are always linked to a registered user.
3. Average ratings are system generated.
4. Duplicate movie records are prohibited.
5. Authentication required for review submission.

## Development Principles

1. API First Design.
2. Separation of Concerns.
3. RESTful standards.
4. Secure by default.

## Security Principles

1. JWT authentication mandatory.
2. Passwords hashed using BCrypt.
3. No sensitive data in logs.
4. Authorization enforced at API layer.

## Testing Principles

1. Critical APIs require automated tests.
2. Authentication flow must be tested.
3. CRUD operations require positive and negative scenarios.

## Architecture Principles

1. Frontend and backend independently deployable.
2. Database abstraction through repositories.
3. Stateless APIs.
