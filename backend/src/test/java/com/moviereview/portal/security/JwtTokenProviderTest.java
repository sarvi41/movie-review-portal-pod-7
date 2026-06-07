package com.moviereview.portal.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviereview.portal.model.Role;
import com.moviereview.portal.model.User;
import com.moviereview.portal.model.UserRole;
import com.moviereview.portal.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

    @Test
    void shouldGenerateAndValidateJwtToken() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", "SomeLongSecretValueThatMeetsMinimumLength123");
        ReflectionTestUtils.setField(provider, "jwtExpirationMs", 3600000L);
        provider.init();

        User user = new User("jwt@example.com", "encoded-password", "JWT User", UserStatus.ACTIVE);
        user.addRole(new UserRole(user, Role.USER));
        UserPrincipal principal = UserPrincipal.create(user);

        String token = provider.generateToken(principal);
        assertTrue(provider.validateToken(token));
        assertEquals("jwt@example.com", provider.getEmailFromToken(token));
        assertTrue(provider.getRolesFromToken(token).contains("USER"));
    }
}
