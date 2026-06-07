package com.moviereview.portal.service;

import com.moviereview.portal.dto.AuthResponse;
import com.moviereview.portal.dto.LoginRequest;
import com.moviereview.portal.dto.RegisterRequest;
import com.moviereview.portal.dto.UserResponse;
import com.moviereview.portal.exception.ConflictException;
import com.moviereview.portal.model.Role;
import com.moviereview.portal.model.User;
import com.moviereview.portal.model.UserRole;
import com.moviereview.portal.model.UserStatus;
import com.moviereview.portal.repository.UserRepository;
import com.moviereview.portal.security.JwtTokenProvider;
import com.moviereview.portal.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(), UserStatus.ACTIVE);
        UserRole role = new UserRole(user, Role.USER);
        user.addRole(role);
        userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(user);
        String token = jwtTokenProvider.generateToken(principal);
        return buildAuthResponse(token, principal);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(principal);
            return buildAuthResponse(token, principal);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public UserResponse getCurrentUser(String email) {
        return userRepository.findByEmail(email)
            .map(this::toDto)
            .orElseThrow(() -> new BadCredentialsException("Authenticated user not found"));
    }

    private AuthResponse buildAuthResponse(String token, UserPrincipal principal) {
        return new AuthResponse(token, jwtTokenProvider.getExpirationSeconds(), toDto(principal));
    }

    private UserResponse toDto(UserPrincipal principal) {
        return new UserResponse(
            principal.getId().toString(),
            principal.getUsername(),
            principal.getName(),
            principal.getRoles(),
            null
        );
    }

    private UserResponse toDto(User user) {
        return new UserResponse(
            user.getId().toString(),
            user.getEmail(),
            user.getName(),
            user.getRoles().stream().map(userRole -> userRole.getRole().name()).collect(java.util.stream.Collectors.toSet()),
            user.getCreatedAt()
        );
    }
}
