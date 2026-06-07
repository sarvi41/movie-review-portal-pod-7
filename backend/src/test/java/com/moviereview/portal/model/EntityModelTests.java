package com.moviereview.portal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class EntityModelTests {

    @Test
    void userEntityShouldPreserveFieldsAndRoles() {
        User user = new User("alice@example.com", "hashed-password", "Alice", UserStatus.ACTIVE);
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("hashed-password", user.getPasswordHash());
        assertEquals("Alice", user.getName());
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        UserRole role = new UserRole(user, Role.USER);
        user.addRole(role);
        assertFalse(user.getRoles().isEmpty());
        assertEquals(Role.USER, role.getRole());
        assertEquals(user, role.getUser());
    }

    @Test
    void movieGenreAssociationShouldLinkMovieAndGenre() {
        Movie movie = new Movie("Example Movie", 1995, "A classic film.", "Director", 120, null);
        Genre genre = new Genre("Drama");
        MovieGenre movieGenre = new MovieGenre(movie, genre);

        assertNotNull(movieGenre.getMovie());
        assertNotNull(movieGenre.getGenre());
        assertEquals(movie, movieGenre.getMovie());
        assertEquals(genre, movieGenre.getGenre());
    }

    @Test
    void reviewEntityShouldCaptureReviewTextAndStatus() {
        User user = new User("bob@example.com", "hash", "Bob", UserStatus.ACTIVE);
        Movie movie = new Movie("Review Movie", 2020, "Description", "Director", 95, null);
        Review review = new Review(user, movie, "A positive review with enough length.");

        assertEquals(user, review.getUser());
        assertEquals(movie, review.getMovie());
        assertEquals("A positive review with enough length.", review.getReviewText());
        assertTrue(review.isActive());
    }

    @Test
    void ratingEntityShouldStoreScoreWithMovieAndUser() {
        User user = new User("carol@example.com", "hash", "Carol", UserStatus.ACTIVE);
        Movie movie = new Movie("Rated Movie", 2021, "Description", "Director", 105, null);
        Rating rating = new Rating(user, movie, 4);

        assertEquals(user, rating.getUser());
        assertEquals(movie, rating.getMovie());
        assertEquals(4, rating.getScore());
    }

    @Test
    void auditLogEntityShouldKeepJsonDetailsAndActorReference() {
        User actor = new User("admin@example.com", "hash", "Admin", UserStatus.ACTIVE);
        UUID entityId = UUID.randomUUID();
        String details = "{\"change\":\"created\"}";
        AuditLog auditLog = new AuditLog(actor, "MOVIE", entityId, "CREATE", details);

        assertEquals(actor, auditLog.getActor());
        assertEquals("MOVIE", auditLog.getEntityType());
        assertEquals(entityId, auditLog.getEntityId());
        assertEquals("CREATE", auditLog.getAction());
        assertEquals(details, auditLog.getDetails());
    }
}
