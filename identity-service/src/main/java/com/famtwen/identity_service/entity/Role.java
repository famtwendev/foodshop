package com.famtwen.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
    @Id
    String name;

    String id; //idrole in keycloak


    String description;


    // Thay @ManyToMany bằng @OneToMany
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();

    // Helper method để lấy users
    public Set<User> getUsers() {
        return userRoles.stream()
                        .map(UserRole::getUser)
                        .collect(Collectors.toSet());
    }
}
