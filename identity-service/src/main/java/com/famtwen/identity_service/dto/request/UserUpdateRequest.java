package com.famtwen.identity_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Email(message = "INVALID_EMAIL")
    String email;
    String firstName;
    String lastName;
    String address;

    @Size(min = 9, message = "INVALID_NUMBERPHONE")
    String numberPhone;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dob;
    String sex;
    String picture;
}
