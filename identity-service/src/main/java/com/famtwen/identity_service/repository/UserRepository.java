package com.famtwen.identity_service.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.famtwen.identity_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    Optional<User> findByKid(String kid);

    boolean existsByKid(String kid);

    boolean existsByEmail(String email);

    String kid(String kid);

    CharSequence findByUsername(String username);
}
