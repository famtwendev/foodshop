package com.famtwen.identity_service.repository;

import com.famtwen.identity_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsRoleByName(String name);

    Optional<Role> findByName(String name);
}
