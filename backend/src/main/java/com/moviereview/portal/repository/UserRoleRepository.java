package com.moviereview.portal.repository;

import com.moviereview.portal.model.UserRole;
import com.moviereview.portal.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
