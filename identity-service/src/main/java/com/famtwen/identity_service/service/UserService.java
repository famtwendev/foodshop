package com.famtwen.identity_service.service;

import com.famtwen.event.dto.NotificationEvent;
import com.famtwen.identity_service.constants.PredefinedRole;
import com.famtwen.identity_service.dto.request.UserUpdateRequest;
import com.famtwen.identity_service.dto.request.keycloak.TokenExchangeResponse;
import com.famtwen.identity_service.dto.response.UserResponse;
import com.famtwen.identity_service.entity.Role;
import com.famtwen.identity_service.entity.User;
import com.famtwen.identity_service.repository.http.IdentityClient;
import com.famtwen.identity_service.mapper.UserMapper;
import com.famtwen.identity_service.repository.RoleRepository;
import com.famtwen.identity_service.repository.UserRepository;
import com.famtwen.identity_service.dto.request.RegistrationRequest;
import com.famtwen.identity_service.dto.request.keycloak.Credential;
import com.famtwen.identity_service.dto.request.keycloak.UserCreationParam;
import com.famtwen.identity_service.dto.response.OutboundUserResponse;
import com.famtwen.identity_service.exception.AppException;
import com.famtwen.identity_service.exception.ErrorCode;
import com.famtwen.identity_service.exception.ErrorNomalizer;
import com.nimbusds.jwt.SignedJWT;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    IdentityClient identityClient;

    ErrorNomalizer errorNormalizer;

    private final AuthenticationService authenticationService;

    KafkaTemplate<String, Object> kafkaTemplate;
    private final RoleRepository roleRepository;
    @Value("${keycloak.client-id}")
    @NonFinal
    String clientId;

    @Value("${keycloak.client-secret}")
    @NonFinal
    String clientSecret;

    @Value("${keycloak.realm}")
    @NonFinal
    String realmname;

    @Value("${keycloak.default-roles}")
    @NonFinal
    String roleDefault;

    public void initData() {

    }

    public UserResponse getMyProfiles() {
        var authentication = SecurityContextHolder.getContext()
                                                  .getAuthentication();
        var userId = authentication.getName();
        try {
            String token = ((JwtAuthenticationToken) authentication).getToken()
                                                                    .getTokenValue();
            // Lưu thông tin người dùng nếu chưa tồn tại trong cơ sở dữ liệu
            log.info("Token: {}", token);
            if (!userRepository.existsByKid(userId)) {
                OutboundUserResponse outboundUserResponse = identityClient.getUser(realmname, "Bearer " + token);

                log.info("/my-profile: userId: {} - authentication: {}", userId, authentication);
                log.info("OutboundResponse: {}", outboundUserResponse);
                if (!userRepository.existsByEmail(outboundUserResponse.getEmail())) {
                    userRepository.save(User.builder()
                                            .kid(userId)
                                            .userId(outboundUserResponse.get_id())
                                            .username(outboundUserResponse.getEmail())
                                            .firstName(outboundUserResponse.getGivenName())
                                            .lastName(outboundUserResponse.getFamilyName())
                                            .email(outboundUserResponse.getEmail())
                                            .dob(outboundUserResponse.getDob())
                                            .build());
                }
                log.info("Saved new user to database: {}", userId);
            }
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }

        var profiles = userRepository.findByKid(userId)
                                     .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(profiles);
    }

//    public ProfileResponse getMyProfiles() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        var userId = authentication.getName();
//
//        log.info("/my-profile: userId: {} - authentication: {}", userId, authentication);
//
//        var profiles = profileRepository.findByUserId(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        return profileMapper.toProfileResponse(profiles);
//    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUser() {

        var profiles = userRepository.findAll();
        return profiles.stream()
                       .map(userMapper::toUserResponse)
                       .toList();
    }

    public void deleteUser(String userToken) {
        try {
            TokenExchangeResponse clientToken = authenticationService.getClientToken();
            SignedJWT jwt = SignedJWT.parse(userToken.replace("Bearer ", ""));
            String sub = jwt.getJWTClaimsSet()
                            .getSubject();
            log.info(sub);
            identityClient.deleteUser(realmname, "Bearer " + clientToken.getAccessToken(), sub);
            // 👉 Logic xử lý DB tại đây
            userRepository.findByKid(sub)
                          .ifPresent(user -> {
                              userRepository.delete(user);
                              log.info("Deleted user {} from local DB", user.getUserId());
                          });
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during logout", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public void updateUser(String userToken, UserUpdateRequest request) {
        try {
            TokenExchangeResponse clientToken = authenticationService.getClientToken();
            SignedJWT jwt = SignedJWT.parse(userToken.replace("Bearer ", ""));
            String sub = jwt.getJWTClaimsSet()
                            .getSubject();
            log.info(sub);

            UserCreationParam userCreationParam = UserCreationParam.builder()
                                                                   .firstName(request.getFirstName())
                                                                   .lastName(request.getLastName())
                                                                   .email(request.getEmail())
                                                                   .enabled(true)
                                                                   .emailVerified(false)
                                                                   .build();
            identityClient.updateUser(realmname, "Bearer " + clientToken.getAccessToken(), sub, userCreationParam);
            // 👉 Logic xử lý DB tại đây
            // 👉 Cập nhật dữ liệu trong DB local
            userRepository.findByKid(sub)
                          .ifPresent(user -> {
                              // Ví dụ cập nhật thông tin từ request
                              user.setEmail(request.getEmail());
                              user.setFirstName(request.getFirstName());
                              user.setLastName(request.getLastName());
                              user.setNumberPhone(request.getNumberPhone());
                              user.setSex(request.getSex());
                              user.setDob(request.getDob());
                              user.setAddress(request.getAddress());
                              user.setPicture(request.getPicture());

                              // Lưu lại
                              userRepository.save(user);

                              log.info("Updated user {} in local DB", user.getUserId());
                          });
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during logout", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public UserResponse register(RegistrationRequest request) {
        try {
            // Get token client
            TokenExchangeResponse token = authenticationService.getClientToken();

            // Create user with client token and given info
            var creationResponse = identityClient.createUser(realmname,
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                                     .username(request.getUsername())
                                     .firstName(request.getFirstName())
                                     .lastName(request.getLastName())
                                     .email(request.getEmail())
                                     .enabled(true)
                                     .emailVerified(false)
                                     .credentials(List.of(Credential.builder()
                                                                    .type("password")
                                                                    .temporary(false)
                                                                    .value(request.getPassword())
                                                                    .build()))
                                     .build());

            // Get userId of KeyCloak account
            String kid = extractUserId(creationResponse);
            User user = userMapper.toUser(request);
            user.setKid(kid);

            if (user.getUserRoles() == null) {
                user.setUserRoles(new HashSet<>());
            }
            // Tìm role
            Role role = roleRepository.findById(roleDefault)
                                      .orElseThrow(() -> new RuntimeException("Role DEFAULT_ROLE not found"));
            // GÁN rOLE: Tạo UserRole trực tiếp
//            // Tạo UserRole object
//            UserRole userRole = UserRole.builder()
//                                        .user(user)
//                                        .role(role)
//                                        .username(user.getUsername())
//                                        .build();
//            user.getUserRoles().add(userRole);

            user.addRole(role); // Sử dụng helper method

            user.setLastName(Objects.requireNonNullElse(request.getLastName(), ""));
            user.setFirstName(Objects.requireNonNullElse(request.getFirstName(), ""));
            user.setEmail(Objects.requireNonNullElse(request.getEmail(), ""));
            user.setAddress(Objects.requireNonNullElse(request.getAddress(), ""));
            user.setNumberPhone(Objects.requireNonNullElse(request.getNumberPhone(), ""));
            user.setSex(Objects.requireNonNullElse(request.getSex(), ""));
            user.setPicture(Objects.requireNonNullElse(request.getPicture(), ""));

            if (request.getDob() != null) {
                user.setDob(request.getDob());
            }
            String fullName = user.getLastName() + " " + user.getFirstName(); // ví dụ: "Nguyễn Văn"
            user = userRepository.save(user);
            if (user.getEmail()
                    .isEmpty() || user.getEmail() == null) {
                return userMapper.toUserResponse(user);
            }
            NotificationEvent notificationEvent = NotificationEvent.builder()
                                                                   .channel("EMAIL")
                                                                   .recipient(user.getEmail())
                                                                   .templateCode("WELCOME")
                                                                   .params(Map.of(
                                                                           "userName", fullName,
                                                                           "userEmail", user.getEmail(),
                                                                           "activationLink", "https://ftcshop.com/activate?token=abc123",
                                                                           "appName", "FTCSHOP"
                                                                   ))
                                                                   .correlationId(UUID.randomUUID()
                                                                                      .toString())
                                                                   .userId(user.getUserId())
                                                                   .build();
            // Publish message to kafka
//            kafkaTemplate.send("notification-delivery", notificationEvent);
            return userMapper.toUserResponse(user);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        try {
            String location = response.getHeaders()
                                      .getFirst("Location");
            if (location != null && !location.isBlank()) {
                String[] segments = location.split("/");
                if (segments.length > 0) {
                    return segments[segments.length - 1];
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract userId from response", e);
        }
        return null;
    }
}
