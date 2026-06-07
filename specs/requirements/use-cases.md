# Use Cases for Movie Review Portal

## Use Case 1: User Registration and Authentication

- Actor: End User
- Preconditions:
  - User has access to the Movie Review Portal.
  - User has not already registered with the same identity.
- Main Flow:
  1. User navigates to the registration page.
  2. User provides valid registration details (email, password, profile data).
  3. System validates registration input and checks for duplicate accounts.
  4. System hashes the password using BCrypt and stores the user record.
  5. System returns a success confirmation and allows user to login.
  6. User logs in using credentials.
  7. System issues a JWT token to authenticate subsequent requests.
- Alternate Flow:
  - 3a. If registration input is invalid, system returns validation errors and prompts correction.
  - 3b. If a duplicate email is detected, system returns a duplicate account error.
  - 7a. If login credentials are invalid, system returns an authentication error.
- Post Conditions:
  - User account is created and stored securely.
  - User receives a JWT token for authenticated actions.
  - User is authenticated for review and rating submission.

## Use Case 2: Discover Movies

- Actor: End User
- Preconditions:
  - The user is logged in or browsing publicly if allowed.
  - Movie catalog contains available movie records.
- Main Flow:
  1. User visits the movie discovery page.
  2. User enters search terms or filters by category.
  3. System queries the movie catalog and returns matching results.
  4. System displays movie listings, including titles, posters, and average ratings.
  5. User selects a movie to view detailed information.
- Alternate Flow:
  - 3a. If no movies match the criteria, system displays a "no results found" message.
  - 4a. If the movie data retrieval fails, system returns an error and suggests retry.
- Post Conditions:
  - User can view movie details and rating summaries.
  - User can decide which movie to review or rate next.

## Use Case 3: Submit a Movie Review

- Actor: Authenticated End User
- Preconditions:
  - User is authenticated with a valid JWT.
  - The movie exists in the catalog.
- Main Flow:
  1. User navigates to the movie detail page.
  2. User selects "Write Review" or equivalent action.
  3. User enters review text and any optional metadata.
  4. System validates review content and confirms the user is linked to the review.
  5. System stores the review record with user ID, movie ID, timestamp, and audit metadata.
  6. System displays confirmation that the review was submitted.
- Alternate Flow:
  - 4a. If review content is invalid or missing, system returns a validation error.
  - 5a. If the movie record is duplicate or unavailable, system returns an error and prevents submission.
- Post Conditions:
  - Review is stored and auditable.
  - The review is linked to the authenticated user and the movie.
  - The movie detail page reflects the new review.

## Use Case 4: Submit a Rating

- Actor: Authenticated End User
- Preconditions:
  - User is authenticated.
  - The selected movie exists and the user has permission to rate it.
- Main Flow:
  1. User chooses a rating value between 1 and 5 on the movie detail page.
  2. User confirms the rating submission.
  3. System validates the rating value and user authorization.
  4. System stores the rating with user ID, movie ID, value, and timestamp.
  5. System recalculates the movie's average rating.
  6. System displays the updated average rating and rating confirmation.
- Alternate Flow:
  - 3a. If the rating value is outside the permitted range, system rejects the submission.
  - 4a. If the user is unauthorized or the JWT is invalid, system responds with authentication failure.
- Post Conditions:
  - The rating is auditable and linked to a registered user.
  - The movie’s average rating is updated automatically.

## Use Case 5: View Movie Details and Reviews

- Actor: End User
- Preconditions:
  - Movie exists in the catalog.
  - Movie has at least one review or rating, or may have none.
- Main Flow:
  1. User opens a movie detail page.
  2. System retrieves movie metadata, reviews, and average rating.
  3. System displays the movie details, review list, and current average rating.
  4. User may choose to submit a review or rating if authenticated.
- Alternate Flow:
  - 2a. If review retrieval fails, system displays available movie details with a review loading error.
  - 3a. If no reviews exist, system displays an empty state encouraging first review submission.
- Post Conditions:
  - User sees movie information, reviews, and rating aggregates.
  - User understands if they can contribute a review or rating.

## Use Case 6: Maintain Movie Catalog

- Actor: Administrator or Content Manager
- Preconditions:
  - Admin is authenticated and authorized to manage movie data.
  - Movie does not already exist, or an existing record is being updated.
- Main Flow:
  1. Admin accesses the movie management interface.
  2. Admin submits new movie metadata or updates an existing record.
  3. System validates that the movie is not a duplicate.
  4. System creates or updates the movie record in the catalog.
  5. System confirms the successful save and publishes the updated catalog.
- Alternate Flow:
  - 3a. If a duplicate movie is detected, system rejects creation and prompts the admin to merge or edit.
  - 4a. If the update fails due to validation or authorization, system returns a descriptive error.
- Post Conditions:
  - Movie catalog remains free of duplicate records.
  - Published movie data is available for discovery and review.
