# QA Strategy for Movie Review Portal

## Test Strategy

### Objectives
- Validate core functional workflows: authentication, movie discovery, reviews, and ratings.
- Ensure secure handling of user credentials and protected endpoint access.
- Catch regressions in critical APIs and UI behavior before releases.
- Verify data quality rules, including duplicate movie prevention and audit traceability.

### Approach
- Use a mix of manual exploratory testing and automated regression tests.
- Automate critical positive and negative flows for backend APIs and frontend UI interactions.
- Apply security-focused checks for authentication, authorization, input validation, and data protection.
- Execute regression suite after each deployment candidate.

### Test Types
- Functional tests: validate business requirements and user stories.
- Negative tests: confirm the system rejects invalid inputs and unauthorized actions.
- Security tests: verify JWT authentication, BCrypt password handling, and API protection.
- Regression tests: cover end-to-end critical flows and past defect fixes.

## Functional Test Cases

### FT-01: User Registration
- Precondition: Portal registration page accessible.
- Steps:
  1. Navigate to registration page.
  2. Enter valid email, strong password, and name.
  3. Submit the form.
- Expected Result: Account is created, confirmation displayed, and user can login.

### FT-02: User Login
- Precondition: Registered user exists.
- Steps:
  1. Navigate to login page.
  2. Enter valid credentials.
  3. Submit the form.
- Expected Result: User receives a JWT token, is redirected to dashboard, and protected endpoints are accessible.

### FT-03: Movie Discovery
- Precondition: Movie catalog contains records.
- Steps:
  1. Open movie discovery page.
  2. Enter search text or apply filters.
  3. Browse returned results and select a movie.
- Expected Result: Matching movies display and movie details page loads correctly.

### FT-04: Submit Review
- Precondition: Authenticated user and existing movie.
- Steps:
  1. Navigate to movie detail page.
  2. Enter review text and submit.
- Expected Result: Review is stored, visible on movie page, and audit metadata is recorded.

### FT-05: Submit Rating
- Precondition: Authenticated user and existing movie.
- Steps:
  1. Select rating score between 1 and 5.
  2. Submit rating.
- Expected Result: Rating is stored, average rating recalculates, and display updates.

### FT-06: Admin Movie Management
- Precondition: Admin user authenticated.
- Steps:
  1. Create a new movie record with valid metadata.
  2. Edit the movie record.
  3. Delete the movie record if permitted.
- Expected Result: Movie data changes persist and duplicate prevention applies.

## Negative Test Cases

### NT-01: Invalid Registration Input
- Steps:
  1. Submit registration with invalid email.
  2. Submit registration with weak password.
- Expected Result: Registration is rejected with validation errors and no account is created.

### NT-02: Duplicate Registration
- Steps:
  1. Register with an email already in use.
- Expected Result: System returns a `409 Conflict` and the duplicate account is prevented.

### NT-03: Unauthorized Access
- Steps:
  1. Call protected API without JWT.
  2. Call admin endpoint with a normal user JWT.
- Expected Result: Requests return `401 Unauthorized` or `403 Forbidden` as appropriate.

### NT-04: Invalid Movie Creation
- Steps:
  1. Create a movie missing required fields.
  2. Create a movie with invalid release year.
- Expected Result: System rejects the payload with `400 Bad Request`.

### NT-05: Duplicate Movie Creation
- Steps:
  1. Create a movie with the same title and release year as an existing movie.
- Expected Result: System returns `409 Conflict` and prevents duplicate record creation.

### NT-06: Invalid Review/Rating Submissions
- Steps:
  1. Submit a review with empty text.
  2. Submit a rating outside the 1-5 range.
- Expected Result: System rejects submissions with validation errors.

## Security Test Cases

### ST-01: JWT Expiration and Validation
- Steps:
  1. Use a valid JWT to access protected API.
  2. Use an expired JWT.
  3. Use a tampered JWT.
- Expected Result: Only valid JWTs succeed; invalid or expired tokens are rejected.

### ST-02: Password Storage
- Steps:
  1. Create a new user.
  2. Inspect database password storage.
- Expected Result: Passwords are stored as BCrypt hashes, not plaintext.

### ST-03: Sensitive Data Handling
- Steps:
  1. Trigger authentication and review submission.
  2. Inspect application logs.
- Expected Result: Logs do not contain plaintext passwords or JWT tokens.

### ST-04: Authorization Enforcement
- Steps:
  1. Call admin-only endpoint with non-admin JWT.
  2. Call general user endpoint with expired or invalid token.
- Expected Result: Access is denied with `403 Forbidden` or `401 Unauthorized`.

### ST-05: Input Sanitization
- Steps:
  1. Submit special characters and SQL-like text in search and movie metadata.
- Expected Result: Request is sanitized, rejected if invalid, and no injection occurs.

## Regression Suite

### RS-01: Authentication Flow
- Register a user.
- Log in successfully.
- Access protected user profile endpoint.
- Attempt access with invalid token.

### RS-02: Movie Catalog Flow
- Create a movie as admin.
- Search and filter to find the movie.
- Update movie metadata.
- Delete the movie and verify it is removed.

### RS-03: Review and Rating Flow
- Submit a review and rating for a movie.
- Verify the review appears on the movie detail page.
- Verify average rating updates after rating submission.

### RS-04: Duplicate Prevention
- Attempt to create duplicate movie entries.
- Verify duplicate is rejected.
- Attempt duplicate review or rating behavior if supported.

### RS-05: Admin Controls
- Verify admin-only endpoints require admin role.
- Verify audit logs capture admin actions.
- Verify non-admin cannot perform admin operations.

### RS-06: Performance and Page Load
- Verify major pages load under 2 seconds in standard test environment.
- Verify search requests return paginated results and handle large result sets gracefully.

## Test Execution Notes
- Prioritize automated coverage for critical API and end-to-end user flows.
- Use the QA persona to emphasize edge case and negative scenario coverage.
- Review results against acceptance criteria from user stories and architecture constraints.
