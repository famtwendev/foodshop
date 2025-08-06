package com.famtwen.identity_service.entity;

import com.famtwen.identity_service.dto.response.RoleResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"userRoles"}) // QUAN TRỌNG: Exclude userRoles
@ToString(exclude = {"userRoles"}) // QUAN TRỌNG: Exclude userRoles
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Nếu userId là tự động tăng
    @Column(name = "userId", nullable = false, unique = true)
    private String userId; // id from mysql

    @Column(name = "kid", nullable = false, unique = true)
    private String kid; // Đổi từ userId, là Keycloak ID

    @Email(message = "INVALID_EMAIL")
    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String email;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String username;

    @Column(name = "firstName", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String firstName;

    @Column(name = "lastName", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String lastName;

    private LocalDate dob;

    @Column(name = "address", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String address;

    @Column(name = "numberPhone", columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String numberPhone;

    private String sex;

    private String picture;


    // QUAN TRỌNG: Khởi tạo userRoles và thêm @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    // Helper method để thêm role - SỬA LẠI ĐÂY
    public void addRole(Role role) {
        // Khởi tạo nếu null
        if (userRoles == null) {
            userRoles = new HashSet<>();
        }

        // Kiểm tra duplicate
        boolean hasRole = userRoles.stream()
                                   .anyMatch(ur -> ur.getRole().getName().equals(role.getName()));

        if (!hasRole) {
            UserRole userRole = UserRole.builder()
                                        .user(this)
                                        .role(role)
                                        .username(this.username)
                                        .build();
            userRoles.add(userRole);
        }
    }

    // Helper method để remove role
    public void removeRole(Role role) {
        if (userRoles != null) {
            userRoles.removeIf(ur -> ur.getRole().getName().equals(role.getName()));
        }
    }

    public Set<RoleResponse> getRoles() {
        if (userRoles == null || userRoles.isEmpty()) {
            return new HashSet<>();
        }
        return userRoles.stream()
                        .map(userRole -> RoleResponse.builder()
                                                     .name(userRole.getRole().getName())
                                                     .description(userRole.getRole().getDescription())
                                                     .build())
                        .collect(Collectors.toSet());
    }

    // Giữ method này để lấy role names
    public Set<String> getRoleNames() {
        if (userRoles == null || userRoles.isEmpty()) {
            return new HashSet<>();
        }
        return userRoles.stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet());
    }
}