package com.moviereview.portal.dto;

import java.time.OffsetDateTime;
import java.util.Set;

public class UserResponse {

    private String id;
    private String email;
    private String name;
    private Set<String> roles;
    private OffsetDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(String id, String email, String name, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public UserResponse(String id, String email, String name, Set<String> roles, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
