package com.moviereview.portal.security;

import com.moviereview.portal.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final java.util.UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final boolean active;
    private final List<GrantedAuthority> authorities;
    private final Set<String> roles;

    private UserPrincipal(java.util.UUID id,
                          String email,
                          String password,
                          String name,
                          boolean active,
                          List<GrantedAuthority> authorities,
                          Set<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.active = active;
        this.authorities = authorities;
        this.roles = roles;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(userRole -> userRole.getRole().name())
            .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
            .collect(Collectors.toList());
        Set<String> roles = user.getRoles().stream()
            .map(userRole -> userRole.getRole().name())
            .collect(Collectors.toSet());
        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getName(),
            user.getStatus() == com.moviereview.portal.model.UserStatus.ACTIVE,
            authorities,
            roles
        );
    }

    public java.util.UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
