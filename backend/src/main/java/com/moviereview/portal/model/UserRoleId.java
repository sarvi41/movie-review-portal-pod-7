package com.moviereview.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRoleId implements Serializable {

    @Column(name = "user_id", columnDefinition = "uuid")
    private java.util.UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private Role role;

    public UserRoleId() {
    }

    public UserRoleId(java.util.UUID userId, Role role) {
        this.userId = userId;
        this.role = role;
    }

    public java.util.UUID getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleId that)) return false;
        return Objects.equals(userId, that.userId) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role);
    }
}
