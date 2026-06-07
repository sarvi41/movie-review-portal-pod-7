package com.moviereview.portal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.moviereview.portal.dto.AuthResponse;
import com.moviereview.portal.dto.LoginRequest;
import com.moviereview.portal.dto.RegisterRequest;
import com.moviereview.portal.dto.UserResponse;
import com.moviereview.portal.service.AuthenticationService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authenticationService);
    }

    @Test
    void registerShouldReturnCreatedResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("Password1!");
        request.setName("New User");

        UserResponse userResponse = new UserResponse("1", "new@example.com", "New User", Set.of("USER"));
        AuthResponse expected = new AuthResponse("jwt-token", 3600L, userResponse);
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(expected);

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertEquals(201, response.getStatusCode().value());
        AuthResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("jwt-token", body.getToken());
        assertEquals("new@example.com", body.getUser().getEmail());
    }

    @Test
    void loginShouldReturnOkResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("login@example.com");
        request.setPassword("Password1!");

        UserResponse userResponse = new UserResponse("1", "login@example.com", "Login User", Set.of("USER"));
        AuthResponse expected = new AuthResponse("jwt-token", 3600L, userResponse);
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(expected);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        AuthResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("jwt-token", body.getToken());
        assertEquals("login@example.com", body.getUser().getEmail());
    }
}
