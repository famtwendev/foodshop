package com.famtwen.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String kid;
    String userId;
    String email;
    String username;
    String firstName;
    String lastName;
    String address;
    String numberPhone;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dob;

    String sex;

    String picture;
    // Chọn 1 trong 2:
    // Set<RoleResponse> roles; // Nếu cần full info
    Set<String> roleNames; // Chỉ role names
}
