# Database Design

## Normalized Schema Overview
This schema supports the Movie Review Portal with normalized entities for users, movies, reviews, and ratings. It enforces data integrity, prevents duplicate movie records, and supports auditability through relational constraints.

## Tables

### users
- `id` UUID PRIMARY KEY
- `email` VARCHAR(255) NOT NULL UNIQUE
- `password_hash` VARCHAR(255) NOT NULL
- `name` VARCHAR(150) NOT NULL
- `status` VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
- `created_at` TIMESTAMPTZ NOT NULL DEFAULT now()
- `updated_at` TIMESTAMPTZ NOT NULL DEFAULT now()

### movies
- `id` UUID PRIMARY KEY
- `title` VARCHAR(255) NOT NULL
- `release_year` INT NOT NULL
- `description` TEXT NOT NULL
- `director` VARCHAR(200) NOT NULL
- `runtime_minutes` INT NOT NULL
- `poster_url` TEXT
- `created_at` TIMESTAMPTZ NOT NULL DEFAULT now()
- `updated_at` TIMESTAMPTZ NOT NULL DEFAULT now()

### genres
- `id` UUID PRIMARY KEY
- `name` VARCHAR(100) NOT NULL UNIQUE

### movie_genres
- `movie_id` UUID NOT NULL
- `genre_id` UUID NOT NULL
- PRIMARY KEY (`movie_id`, `genre_id`)

### reviews
- `id` UUID PRIMARY KEY
- `user_id` UUID NOT NULL
- `movie_id` UUID NOT NULL
- `review_text` TEXT NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL DEFAULT now()
- `updated_at` TIMESTAMPTZ NOT NULL DEFAULT now()
- `is_active` BOOLEAN NOT NULL DEFAULT TRUE

### ratings
- `id` UUID PRIMARY KEY
- `user_id` UUID NOT NULL
- `movie_id` UUID NOT NULL
- `score` INT NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL DEFAULT now()
- `updated_at` TIMESTAMPTZ NOT NULL DEFAULT now()

### user_roles
- `user_id` UUID NOT NULL
- `role` VARCHAR(50) NOT NULL
- PRIMARY KEY (`user_id`, `role`)

### audit_logs
- `id` UUID PRIMARY KEY
- `actor_user_id` UUID NOT NULL
- `entity_type` VARCHAR(100) NOT NULL
- `entity_id` UUID NOT NULL
- `action` VARCHAR(100) NOT NULL
- `details` JSONB
- `created_at` TIMESTAMPTZ NOT NULL DEFAULT now()

## Constraints

### users
- `UNIQUE(email)`
- `CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))`

### movies
- `UNIQUE (title, release_year)`
- `CHECK (release_year >= 1888 AND release_year <= 2100)`
- `CHECK (runtime_minutes > 0)`

### movie_genres
- `FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE`
- `FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE RESTRICT`

### reviews
- `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
- `FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE`
- `CHECK (char_length(review_text) >= 10)`

### ratings
- `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
- `FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE`
- `CHECK (score BETWEEN 1 AND 5)`
- `UNIQUE (user_id, movie_id)`

### user_roles
- `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
- `CHECK (role IN ('USER', 'ADMIN'))`

### audit_logs
- `FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL`

## Relationships
- `users` 1..* `reviews`
- `users` 1..* `ratings`
- `movies` 1..* `reviews`
- `movies` 1..* `ratings`
- `movies` *..* `genres` via `movie_genres`
- `users` 1..* `user_roles`
- `users` 1..* `audit_logs`

## Indexes
- `CREATE INDEX idx_users_email ON users(email);`
- `CREATE INDEX idx_movies_title_release_year ON movies(title, release_year);`
- `CREATE INDEX idx_movies_release_year ON movies(release_year);`
- `CREATE INDEX idx_movie_genres_movie_id ON movie_genres(movie_id);`
- `CREATE INDEX idx_movie_genres_genre_id ON movie_genres(genre_id);`
- `CREATE INDEX idx_reviews_movie_id ON reviews(movie_id);`
- `CREATE INDEX idx_reviews_user_id ON reviews(user_id);`
- `CREATE INDEX idx_ratings_movie_id ON ratings(movie_id);`
- `CREATE INDEX idx_ratings_user_id ON ratings(user_id);`
- `CREATE INDEX idx_ratings_movie_score ON ratings(movie_id, score);`
- `CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_user_id);`

## Normalization Notes
- The schema is normalized to 3NF: user data, movie metadata, review content, and rating values are separated into dedicated tables.
- `movie_genres` decouples movie and genre associations.
- `user_roles` supports simple role-based control without duplicating user records.
- `audit_logs` stores action history independent of operational entities.
