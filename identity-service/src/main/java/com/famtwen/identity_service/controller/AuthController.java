package com.famtwen.identity_service.controller;

import com.famtwen.identity_service.dto.ApiResponse;
import com.famtwen.identity_service.dto.request.AuthenticationRequest;
import com.famtwen.identity_service.dto.request.PasswordChangeRequest;
import com.famtwen.identity_service.dto.request.IntrospectionRequest;
import com.famtwen.identity_service.dto.request.keycloak.TokenExchangeResponse;
import com.famtwen.identity_service.dto.response.TokenIntrospectionResponse;
import com.famtwen.identity_service.exception.AppException;
import com.famtwen.identity_service.exception.ErrorCode;
import com.famtwen.identity_service.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/token")
    public TokenExchangeResponse login(@RequestBody AuthenticationRequest request) {
        return authenticationService.generateUserToken(request);
    }

    @PostMapping("/refresh")
    public TokenExchangeResponse refresh(@RequestBody IntrospectionRequest refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }


    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestHeader("Authorization") String userToken,
                                            @Valid @RequestBody PasswordChangeRequest request) {
        authenticationService.changePassword(userToken, request);
        return ApiResponse.<Void>builder()
                          .message("Password changed successfully")
                          .build();
    }

    // 🔍 NEW: Token Introspection endpoint for Internal services, backend modules, hoặc clients tin cậy (public client không nên gọi).
    @PostMapping("/introspect")
    public ApiResponse<TokenIntrospectionResponse> introspectToken(
            @Valid @RequestBody IntrospectionRequest request) {
        TokenIntrospectionResponse response = authenticationService.introspectToken(request);
        return ApiResponse.<TokenIntrospectionResponse>builder()
                          .result(response)
                          .build();
    }



    // 🔍 NEW: Simple token validation endpoint for Public clients hoặc frontend apps cần kiểm tra token nhanh chóng.
    @PostMapping("/validate")
    public Map<String, Object> validateToken(@RequestBody IntrospectionRequest request) {
        String token = request.getToken();
        if (token == null || token.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        boolean isValid = authenticationService.isTokenValid(token);

        return Map.of(
                "valid", isValid,
                "timestamp", Instant.now().getEpochSecond()
        );
    }

    // 🔍 NEW: Get token information endpoint for ADMIN
    @PostMapping("/token-info")
    public ApiResponse<TokenIntrospectionResponse> getTokenInfo(
            @RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        Optional<TokenIntrospectionResponse> tokenInfo = authenticationService.getTokenInfo(token);

        return ApiResponse.<TokenIntrospectionResponse>builder()
                          .message("Token info fetched")
                          .result(tokenInfo.orElse(TokenIntrospectionResponse.inactive()))
                          .build();
    }


    // 🔍 NEW: Validate token từ Authorization header
    @GetMapping("/me")
    public ApiResponse<TokenIntrospectionResponse> getCurrentTokenInfo(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Optional<TokenIntrospectionResponse> tokenInfo = authenticationService.getTokenInfo(token);

        return ApiResponse.<TokenIntrospectionResponse>builder()
                          .message("Token introspection result")
                          .result(tokenInfo.orElse(TokenIntrospectionResponse.inactive()))
                          .build();
    }

    // 🚪 Logout endpoint
    @PostMapping("/logout-session")
    public ApiResponse<Void> logoutSession(@RequestBody IntrospectionRequest token) {
        authenticationService.logoutSession(token);

        return ApiResponse.<Void>builder()
                          .message("Logout successful")
                          .build();
    }

    // 🚪 Logout endpoint
    @DeleteMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody IntrospectionRequest token) {
        authenticationService.logout(token);

        return ApiResponse.<Void>builder()
                          .message("Logout successful")
                          .build();
    }
}
