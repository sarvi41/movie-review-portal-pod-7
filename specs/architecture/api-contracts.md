# Movie Review Portal API Contracts

## Authentication

### POST /api/auth/register
**Request**
```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "name": "Jane Doe"
}
```
**Response**
- `201 Created`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "Jane Doe",
  "createdAt": "2026-06-07T12:00:00Z"
}
```
**Validation**
- `email`: required, valid email format, unique.
- `password`: required, minimum 8 chars, at least one uppercase, one lowercase, one digit, one special char.
- `name`: required, non-empty.
**Error Handling**
- `400 Bad Request` for invalid request body.
- `409 Conflict` if email already exists.
- `500 Internal Server Error` for unexpected errors.

### POST /api/auth/login
**Request**
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```
**Response**
- `200 OK`
```json
{
  "token": "eyJhbGci...",
  "expiresIn": 3600,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "Jane Doe",
    "roles": ["USER"]
  }
}
```
**Validation**
- `email`: required, valid email format.
- `password`: required, non-empty.
**Error Handling**
- `400 Bad Request` for invalid body.
- `401 Unauthorized` for incorrect credentials.
- `403 Forbidden` if account is disabled.
- `500 Internal Server Error` for unexpected errors.

### GET /api/auth/me
**Request**
- Header: `Authorization: Bearer <token>`
**Response**
- `200 OK`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "Jane Doe",
  "roles": ["USER"]
}
```
**Validation**
- JWT required and valid.
**Error Handling**
- `401 Unauthorized` for missing or invalid token.
- `403 Forbidden` if token is valid but user is disabled.

## Movies

### GET /api/movies
**Request**
- Query params: `search`, `genre`, `sort`, `page`, `size`
**Response**
- `200 OK`
```json
{
  "page": 1,
  "size": 20,
  "total": 345,
  "movies": [
    {
      "id": "uuid",
      "title": "Inception",
      "releaseYear": 2010,
      "description": "A mind-bending thriller.",
      "averageRating": 4.8,
      "genres": ["Sci-Fi", "Thriller"],
      "posterUrl": "https://..."
    }
  ]
}
```
**Validation**
- `page`: optional, integer >= 1.
- `size`: optional, integer between 1 and 100.
- `sort`: optional, allowed values `title`, `releaseYear`, `averageRating`.
**Error Handling**
- `400 Bad Request` for invalid query parameters.
- `500 Internal Server Error` for unexpected errors.

### POST /api/movies
**Request**
- Header: `Authorization: Bearer <token>`
```json
{
  "title": "Inception",
  "releaseYear": 2010,
  "description": "A mind-bending thriller.",
  "director": "Christopher Nolan",
  "runtimeMinutes": 148,
  "posterUrl": "https://...",
  "genreIds": ["uuid1", "uuid2"]
}
```
**Response**
- `201 Created`
```json
{
  "id": "uuid",
  "title": "Inception",
  "releaseYear": 2010,
  "description": "A mind-bending thriller.",
  "director": "Christopher Nolan",
  "runtimeMinutes": 148,
  "posterUrl": "https://...",
  "genreIds": ["uuid1", "uuid2"],
  "createdAt": "2026-06-07T12:00:00Z"
}
```
**Validation**
- `title`: required, non-empty.
- `releaseYear`: required, integer, reasonable year (e.g. 1888-2100).
- `description`: required, non-empty.
- `director`: required, non-empty.
- `runtimeMinutes`: required, positive integer.
- `posterUrl`: optional, valid URL if present.
- `genreIds`: optional list of valid genre UUIDs.
**Error Handling**
- `400 Bad Request` for missing or invalid fields.
- `401 Unauthorized` for unauthenticated requests.
- `403 Forbidden` for non-admin users.
- `409 Conflict` when a duplicate movie exists.
- `500 Internal Server Error` for unexpected errors.

### GET /api/movies/{movieId}
**Request**
- Path param: `movieId`.
**Response**
- `200 OK`
```json
{
  "id": "uuid",
  "title": "Inception",
  "releaseYear": 2010,
  "description": "A mind-bending thriller.",
  "director": "Christopher Nolan",
  "runtimeMinutes": 148,
  "posterUrl": "https://...",
  "genres": ["Sci-Fi", "Thriller"],
  "averageRating": 4.8,
  "reviewCount": 234
}
```
**Validation**
- `movieId`: required, valid UUID.
**Error Handling**
- `400 Bad Request` for invalid `movieId`.
- `404 Not Found` if movie does not exist.
- `500 Internal Server Error` for unexpected errors.

### PUT /api/movies/{movieId}
**Request**
- Header: `Authorization: Bearer <token>`
- Path param: `movieId`
```json
{
  "title": "Inception",
  "releaseYear": 2010,
  "description": "Updated description.",
  "director": "Christopher Nolan",
  "runtimeMinutes": 148,
  "posterUrl": "https://...",
  "genreIds": ["uuid1", "uuid2"]
}
```
**Response**
- `200 OK`
```json
{
  "id": "uuid",
  "title": "Inception",
  "releaseYear": 2010,
  "description": "Updated description.",
  "director": "Christopher Nolan",
  "runtimeMinutes": 148,
  "posterUrl": "https://...",
  "genreIds": ["uuid1", "uuid2"],
  "updatedAt": "2026-06-07T12:10:00Z"
}
```
**Validation**
- Same as POST /api/movies.
- `movieId`: required, valid UUID.
**Error Handling**
- `400 Bad Request` for invalid payload.
- `401 Unauthorized` for unauthenticated requests.
- `403 Forbidden` for non-admin users.
- `404 Not Found` if movie does not exist.
- `409 Conflict` if update would create duplicate record.
- `500 Internal Server Error` for unexpected errors.

### DELETE /api/movies/{movieId}
**Request**
- Header: `Authorization: Bearer <token>`
- Path param: `movieId`
**Response**
- `204 No Content`
**Validation**
- `movieId`: required, valid UUID.
**Error Handling**
- `401 Unauthorized` for unauthenticated requests.
- `403 Forbidden` for non-admin users.
- `404 Not Found` if movie does not exist.
- `409 Conflict` if movie cannot be deleted due to review dependencies.
- `500 Internal Server Error` for unexpected errors.

## Reviews

### GET /api/movies/{movieId}/reviews
**Request**
- Path param: `movieId`
- Query params: `page`, `size`
**Response**
- `200 OK`
```json
{
  "page": 1,
  "size": 10,
  "total": 12,
  "reviews": [
    {
      "id": "uuid",
      "userId": "uuid",
      "userName": "Jane Doe",
      "movieId": "uuid",
      "reviewText": "Amazing movie!",
      "rating": 5,
      "createdAt": "2026-06-07T12:15:00Z"
    }
  ]
}
```
**Validation**
- `movieId`: required, valid UUID.
- `page`: optional integer >= 1.
- `size`: optional integer between 1 and 50.
**Error Handling**
- `400 Bad Request` for invalid params.
- `404 Not Found` if movie does not exist.
- `500 Internal Server Error` for unexpected errors.

### POST /api/movies/{movieId}/reviews
**Request**
- Header: `Authorization: Bearer <token>`
- Path param: `movieId`
```json
{
  "reviewText": "This is a thoughtful review.",
  "rating": 4
}
```
**Response**
- `201 Created`
```json
{
  "id": "uuid",
  "userId": "uuid",
  "movieId": "uuid",
  "reviewText": "This is a thoughtful review.",
  "rating": 4,
  "createdAt": "2026-06-07T12:20:00Z"
}
```
**Validation**
- `reviewText`: required, non-empty, length between 10 and 2000 characters.
- `rating`: required, integer between 1 and 5.
- `movieId`: required, valid UUID.
- User must be authenticated.
**Error Handling**
- `400 Bad Request` for invalid payload.
- `401 Unauthorized` for unauthenticated requests.
- `404 Not Found` if movie does not exist.
- `409 Conflict` if duplicate review policy is violated (if implemented).
- `500 Internal Server Error` for unexpected errors.

## Ratings

### GET /api/movies/{movieId}/ratings
**Request**
- Path param: `movieId`
**Response**
- `200 OK`
```json
{
  "movieId": "uuid",
  "averageRating": 4.7,
  "ratingCount": 124,
  "ratings": [
    {
      "id": "uuid",
      "userId": "uuid",
      "score": 5,
      "createdAt": "2026-06-07T12:25:00Z"
    }
  ]
}
```
**Validation**
- `movieId`: required, valid UUID.
**Error Handling**
- `400 Bad Request` for invalid `movieId`.
- `404 Not Found` if movie does not exist.
- `500 Internal Server Error` for unexpected errors.

### POST /api/movies/{movieId}/ratings
**Request**
- Header: `Authorization: Bearer <token>`
- Path param: `movieId`
```json
{
  "score": 5
}
```
**Response**
- `201 Created`
```json
{
  "id": "uuid",
  "userId": "uuid",
  "movieId": "uuid",
  "score": 5,
  "createdAt": "2026-06-07T12:30:00Z",
  "averageRating": 4.7,
  "ratingCount": 125
}
```
**Validation**
- `score`: required, integer between 1 and 5.
- `movieId`: required, valid UUID.
- User must be authenticated.
**Error Handling**
- `400 Bad Request` for invalid payload.
- `401 Unauthorized` for unauthenticated requests.
- `404 Not Found` if movie does not exist.
- `409 Conflict` if rating already exists for same user and movie (if uniqueness enforced).
- `500 Internal Server Error` for unexpected errors.

## Users

### GET /api/users/me
**Request**
- Header: `Authorization: Bearer <token>`
**Response**
- `200 OK`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "Jane Doe",
  "roles": ["USER"],
  "createdAt": "2026-06-07T12:00:00Z"
}
```
**Validation**
- JWT required and valid.
**Error Handling**
- `401 Unauthorized` for missing or invalid token.
- `403 Forbidden` if user is disabled.

### GET /api/users/{userId}
**Request**
- Header: `Authorization: Bearer <token>`
- Path param: `userId`
**Response**
- `200 OK`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "Jane Doe",
  "roles": ["USER"],
  "createdAt": "2026-06-07T12:00:00Z"
}
```
**Validation**
- `userId`: required, valid UUID.
- JWT required and valid.
**Error Handling**
- `400 Bad Request` for invalid `userId`.
- `401 Unauthorized` for unauthenticated requests.
- `403 Forbidden` if requester lacks permission.
- `404 Not Found` if user does not exist.
- `500 Internal Server Error` for unexpected errors.

### PUT /api/users/{userId}
**Request**
- Header: `Authorization: Bearer <token>`
- Path param: `userId`
```json
{
  "name": "Jane Doe",
  "password": "NewPassword123!"
}
```
**Response**
- `200 OK`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "Jane Doe",
  "updatedAt": "2026-06-07T12:40:00Z"
}
```
**Validation**
- `name`: optional, non-empty if provided.
- `password`: optional, meets strength requirements if provided.
- `userId`: required, valid UUID.
**Error Handling**
- `400 Bad Request` for invalid payload.
- `401 Unauthorized` for unauthenticated requests.
- `403 Forbidden` if updating another user without admin rights.
- `404 Not Found` if user does not exist.
- `500 Internal Server Error` for unexpected errors.

## Common Error Response Format
```json
{
  "timestamp": "2026-06-07T12:45:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: email is required.",
  "path": "/api/auth/register"
}
```

## Notes
- All protected endpoints require `Authorization: Bearer <token>`.
- Validation errors return `400 Bad Request` with details.
- Authentication failures return `401 Unauthorized`.
- Authorization failures return `403 Forbidden`.
- Not found resources return `404 Not Found`.
- Conflicts return `409 Conflict` for duplicate or rejected business rules.
- Use consistent pagination for list responses.
