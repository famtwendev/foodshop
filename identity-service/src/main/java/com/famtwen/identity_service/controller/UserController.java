package com.famtwen.identity_service.controller;

import com.famtwen.identity_service.dto.ApiResponse;
import com.famtwen.identity_service.dto.request.RegistrationRequest;
import com.famtwen.identity_service.dto.request.UserUpdateRequest;
import com.famtwen.identity_service.dto.response.UserResponse;
import com.famtwen.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/users")
public class UserController {
    UserService UserService;

    @PostMapping("/registration")
    ApiResponse<UserResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(UserService.register(request))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<UserResponse>> getAllUser() {
        return ApiResponse. <List<UserResponse>>builder()
                .result(UserService.getAllUser())
                .build();
    }

    @GetMapping("/my-profile")
    ApiResponse<UserResponse> getMyProfiles() {
        return ApiResponse.<UserResponse>builder()
                .result(UserService.getMyProfiles())
                .build();
    }

    @DeleteMapping("/delete")
    ApiResponse<Void> deleteUser( @RequestHeader("Authorization") String authHeader) {
        UserService.deleteUser(authHeader);
        return ApiResponse.<Void>builder()
                          .message("Delete user success.")
                          .build();
    }


    @PutMapping("/update")
    ApiResponse<Void> updateUser( @RequestHeader("Authorization") String authHeader, @RequestBody UserUpdateRequest request) {
        UserService.updateUser(authHeader, request);
        return ApiResponse.<Void>builder()
                          .message("Update user success.")
                          .build();
    }
}
