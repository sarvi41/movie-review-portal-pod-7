# Movie Review Portal Frontend

Angular 19 frontend for the Movie Review Portal.

## Setup

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start development server:
   ```bash
   npm start
   ```

3. Build production bundle:
   ```bash
   npm run build
   ```

## Docker

Build the frontend image:
```bash
docker build -t movie-review-portal-frontend ./frontend
```

Run the production container:
```bash
docker run --rm -p 4200:80 movie-review-portal-frontend
```
