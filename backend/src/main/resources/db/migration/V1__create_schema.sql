-- Flyway migration: initial normalized schema for Movie Review Portal

CREATE TABLE users (
    id uuid PRIMARY KEY,
    email varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    name varchar(150) NOT NULL,
    status varchar(50) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE movies (
    id uuid PRIMARY KEY,
    title varchar(255) NOT NULL,
    release_year int NOT NULL,
    description text NOT NULL,
    director varchar(200) NOT NULL,
    runtime_minutes int NOT NULL,
    poster_url text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT uq_movies_title_release_year UNIQUE (title, release_year),
    CONSTRAINT chk_movies_release_year CHECK (release_year >= 1888 AND release_year <= 2100),
    CONSTRAINT chk_movies_runtime CHECK (runtime_minutes > 0)
);

CREATE INDEX idx_movies_title_release_year ON movies(title, release_year);
CREATE INDEX idx_movies_release_year ON movies(release_year);

CREATE TABLE genres (
    id uuid PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE movie_genres (
    movie_id uuid NOT NULL,
    genre_id uuid NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genres_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_genres_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE RESTRICT
);

CREATE INDEX idx_movie_genres_movie_id ON movie_genres(movie_id);
CREATE INDEX idx_movie_genres_genre_id ON movie_genres(genre_id);

CREATE TABLE reviews (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    movie_id uuid NOT NULL,
    review_text text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    is_active boolean NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT chk_reviews_text CHECK (char_length(review_text) >= 10)
);

CREATE INDEX idx_reviews_movie_id ON reviews(movie_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);

CREATE TABLE ratings (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    movie_id uuid NOT NULL,
    score int NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_ratings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ratings_movie FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT chk_ratings_score CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT uq_ratings_user_movie UNIQUE (user_id, movie_id)
);

CREATE INDEX idx_ratings_movie_id ON ratings(movie_id);
CREATE INDEX idx_ratings_user_id ON ratings(user_id);
CREATE INDEX idx_ratings_movie_score ON ratings(movie_id, score);

CREATE TABLE user_roles (
    user_id uuid NOT NULL,
    role varchar(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_user_roles_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE audit_logs (
    id uuid PRIMARY KEY,
    actor_user_id uuid,
    entity_type varchar(100) NOT NULL,
    entity_id uuid NOT NULL,
    action varchar(100) NOT NULL,
    details jsonb,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_user_id);
