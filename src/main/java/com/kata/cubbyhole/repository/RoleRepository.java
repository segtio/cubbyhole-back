package com.kata.cubbyhole.repository;

import com.kata.cubbyhole.model.Role;
import com.kata.cubbyhole.model.enumeration.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
