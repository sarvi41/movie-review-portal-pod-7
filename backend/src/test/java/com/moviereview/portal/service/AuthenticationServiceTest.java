package com.moviereview.portal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.moviereview.portal.dto.AuthResponse;
import com.moviereview.portal.dto.LoginRequest;
import com.moviereview.portal.dto.RegisterRequest;
import com.moviereview.portal.exception.ConflictException;
import com.moviereview.portal.model.Role;
import com.moviereview.portal.model.User;
import com.moviereview.portal.model.UserRole;
import com.moviereview.portal.model.UserStatus;
import com.moviereview.portal.repository.UserRepository;
import com.moviereview.portal.security.JwtTokenProvider;
import com.moviereview.portal.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository, passwordEncoder, authenticationManager, jwtTokenProvider);
    }

    @Test
    void registerShouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setName("Test User");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));
        when(jwtTokenProvider.generateToken(any(UserPrincipal.class))).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("Password1!");
        request.setName("Duplicate");

        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        ConflictException exception = org.junit.jupiter.api.Assertions.assertThrows(
            ConflictException.class,
            () -> authenticationService.register(request)
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Password1!");

        User user = new User("user@example.com", "encoded-password", "User", UserStatus.ACTIVE);
        user.addRole(new UserRole(user, Role.USER));
        UserPrincipal principal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(principal)).thenReturn("jwt-token");
        when(jwtTokenProvider.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("user@example.com", response.getUser().getEmail());
    }
}
