package com.famtwen.identity_service.repository.http;

import com.famtwen.identity_service.dto.request.RoleRequest;
import com.famtwen.identity_service.dto.request.keycloak.Credential;
import com.famtwen.identity_service.dto.request.keycloak.TokenExchangeResponse;
import com.famtwen.identity_service.dto.request.keycloak.UserCreationParam;
import com.famtwen.identity_service.dto.response.OutboundUserResponse;
import com.famtwen.identity_service.dto.response.RoleResponse;
import com.famtwen.identity_service.dto.response.TokenIntrospectionResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "identity-client", url = "${keycloak.url}")
public interface    IdentityClient {
    @PostMapping(value = "/realms/{realm-name}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(
            @PathVariable("realm-name") String realmname,
            @QueryMap Map<String, ?> params);

    // Thêm phương thức introspect - Sử dụng @QueryMap với form data
    @PostMapping(value = "/realms/{realm-name}/protocol/openid-connect/token/introspect",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenIntrospectionResponse introspectToken(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("Authorization") String authorization,
            @QueryMap Map<String, ?> formData);


    @PostMapping(value = "/admin/realms/{realm-name}/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("authorization") String token,
            @RequestBody UserCreationParam param);

    // Phương thức mới để lấy thông tin người dùng từ Keycloak
    @GetMapping(value = "/realms/{realm-name}/protocol/openid-connect/userinfo")
    OutboundUserResponse getUser(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("Authorization") String token);


    @PutMapping(value = "/admin/realms/{realm-name}/users/{userId}/reset-password")
    void changePassword(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("Authorization") String adminToken,
            @PathVariable("userId") String userId,
            @RequestBody Credential credential);


    @PutMapping(value = "/admin/realms/{realm-name}/users/{userId}")
    void updateUser(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("Authorization") String adminToken,
            @PathVariable("userId") String userId,
            @RequestBody UserCreationParam request);


    @DeleteMapping(value = "/admin/realms/{realm-name}/users/{userId}")
    void deleteUser(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("Authorization") String adminToken,
            @PathVariable("userId") String userId);


    // 🚪 NEW: Logout endpoint
    @PostMapping(value = "/admin/realms/{realm-name}/users/{userId}/logout",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Void> logoutSession(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("authorization") String adminToken,
            @PathVariable("userId") String userId
    );

    @DeleteMapping(value = "/admin/realms/{realm-name}/sessions/{sessionId}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Void> logout(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("authorization") String adminToken,
            @PathVariable("sessionId") String sessionId
    );

    //===================================ROLE ======================================
    @GetMapping(value = "/admin/realms/{realm-name}/roles/{role-name}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    RoleResponse existsRoleByName(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("authorization") String adminToken,
            @PathVariable("role-name") String rolename
    );

    @PostMapping(value = "/admin/realms/{realm-name}/roles",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Void createRole(
            @PathVariable("realm-name") String realmname,
            @RequestHeader("authorization") String adminToken,
            @RequestBody RoleRequest roleRequest
    );

}
