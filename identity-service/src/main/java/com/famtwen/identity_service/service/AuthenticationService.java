package com.famtwen.identity_service.service;

import com.famtwen.event.dto.NotificationEvent;
import com.famtwen.identity_service.dto.request.AuthenticationRequest;
import com.famtwen.identity_service.dto.request.PasswordChangeRequest;
import com.famtwen.identity_service.dto.request.IntrospectionRequest;
import com.famtwen.identity_service.dto.request.keycloak.Credential;
import com.famtwen.identity_service.dto.request.keycloak.TokenExchangeResponse;
import com.famtwen.identity_service.dto.response.TokenIntrospectionResponse;
import com.famtwen.identity_service.entity.User;
import com.famtwen.identity_service.exception.AppException;
import com.famtwen.identity_service.exception.ErrorCode;
import com.famtwen.identity_service.exception.ErrorNomalizer;
import com.famtwen.identity_service.repository.http.IdentityClient;
import com.famtwen.identity_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ObjectMapper objectMapper;
    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realmname;


    private final IdentityClient identityClient;

    @Autowired
    private ErrorNomalizer errorNormalizer;

    private final RedisTemplate<String, String> redisTemplate;

    private final String REDIS_KEY = "keycloak:client_credentials";
    private final String INTROSPECTION_CACHE_PREFIX = "keycloak:introspect:";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public TokenExchangeResponse getClientToken() {
        try {
            String cachedToken = redisTemplate.opsForValue()
                                              .get(REDIS_KEY);
            if (cachedToken != null) {
                TokenExchangeResponse cached = objectMapper.readValue(cachedToken, TokenExchangeResponse.class);
                return cached;
            }
            Map<String, String> params = Map.of(
                    "grant_type", "client_credentials",
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "scope", "openid profile email"
            );
            TokenExchangeResponse tokenResponse = identityClient.exchangeToken(realmname, params);
            if (tokenResponse == null) {
                throw new AppException(ErrorCode.NULL_EXCEPTION);
            }
//            ObjectMapper objectMapper = new ObjectMapper();
//            String tokenJson = objectMapper.writeValueAsString(tokenResponse);
            String tokenJson = this.objectMapper.writeValueAsString(tokenResponse);
            String expiresInStr = tokenResponse.getExpiresIn(); // Assuming it's a String
            long expiresIn = Long.parseLong(expiresInStr); // Or Integer.parseInt() if within range
            long ttl = expiresIn - 10;

            redisTemplate.opsForValue()
                         .set(REDIS_KEY, tokenJson, Duration.ofSeconds(ttl));

            log.info("==> Cached new Keycloak admin token with TTL: {} seconds", ttl);

            return tokenResponse;
        } catch (Exception e) {
            log.error("Unexpected error occurred while getting token", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public TokenExchangeResponse generateUserToken(AuthenticationRequest request) {
        Map<String, String> params = Map.of(
                "grant_type", "password",
                "client_id", clientId,
                "client_secret", clientSecret,
                "username", request.getUsername(),
                "password", request.getPassword(),
                "scope", "openid profile email"
        );

        try {
            TokenExchangeResponse tokenResponse = identityClient.exchangeToken(realmname, params);
            SignedJWT jwt = SignedJWT.parse(tokenResponse.getAccessToken());
            String kid = jwt.getJWTClaimsSet().getSubject();
            // 📩 Gửi thông báo đăng nhập thành công
            Optional<User> user = userRepository.findByKid(kid);// Lấy thông tin người dùng
            String fullName = user.get()
                                  .getLastName() + " " + user.get()
                                                             .getFirstName(); // ví dụ: "Nguyễn Văn"
            NotificationEvent notificationEvent = NotificationEvent.builder()
                                                                   .channel("EMAIL")
                                                                   .recipient(user.get()
                                                                                  .getEmail())
                                                                   .templateCode("LOGIN_SUCCESS")
                                                                   .params(Map.of(
                                                                           "userName", fullName,
                                                                           "loginTime", LocalDateTime.now()
                                                                                                     .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")),
                                                                           "appName", "FTCSHOP"
                                                                   ))
                                                                   .correlationId(UUID.randomUUID()
                                                                                      .toString())
                                                                   .userId(user.get()
                                                                               .getUserId())
                                                                   .build();

            kafkaTemplate.send("notification-delivery", notificationEvent); // hoặc emailService.sendTemplatedEmail(event);

            return tokenResponse;

        } catch (FeignException.Unauthorized ex) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // 🔐 Refresh token
    public TokenExchangeResponse refreshToken(IntrospectionRequest refreshToken) {
        log.info(refreshToken.getToken());
        Map<String, String> params = Map.of(
                "grant_type", "refresh_token",
                "client_id", clientId,
                "client_secret", clientSecret,
                "scope", "offline_access openid profile email",
                "refresh_token", refreshToken.getToken()
        );
        try {
            return identityClient.exchangeToken(realmname, params);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during logout", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }


    // 🔍 NEW: Token Introspection method
    public TokenIntrospectionResponse introspectToken(IntrospectionRequest request) {
        try {
            // Tạo cache key dựa trên token hash để tránh lưu trữ token thực
            String tokenHash = String.valueOf(request.getToken()
                                                     .hashCode());
            String cacheKey = INTROSPECTION_CACHE_PREFIX + tokenHash;

            // Kiểm tra cache trước
            String cachedResponse = redisTemplate.opsForValue()
                                                 .get(cacheKey);
            if (cachedResponse != null) {
                log.debug("==> Found cached introspection result for token");
                return objectMapper.readValue(cachedResponse, TokenIntrospectionResponse.class);
            }

            // Tạo Basic Auth header với client credentials (không dùng bearer token)
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder()
                                              .encodeToString(credentials.getBytes());
            String authHeader = "Basic " + encodedCredentials;

            // Chuẩn bị form data cho introspection request
            Map<String, Object> formData = new HashMap<>();
            formData.put("token", request.getToken());

            TokenIntrospectionResponse response = identityClient.introspectToken(realmname, authHeader, formData);

            if (response == null) {
                log.warn("==> Received null response from introspection endpoint");
                return TokenIntrospectionResponse.inactive();
            }

            // Cache kết quả nếu token active (cache ngắn hạn để đảm bảo tính real-time)
            if (response.isActive()) {
                String responseJson = objectMapper.writeValueAsString(response);
                // Cache trong 30 giây để balance giữa performance và real-time
                redisTemplate.opsForValue()
                             .set(cacheKey, responseJson, Duration.ofSeconds(30));
                log.debug("==> Cached introspection result for active token");
            }

            log.info("==> Token introspection completed. Active: {}", response.isActive());
            return response;
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during token introspection", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // 🔍 Utility method để validate token đơn giản
    public boolean isTokenValid(String token) {
        try {
            IntrospectionRequest request = IntrospectionRequest.builder()
                                                               .token(token)
                                                               // .tokenTypeHint("access_token")
                                                               .build();

            TokenIntrospectionResponse response = introspectToken(request);
            return response.isActive();
        } catch (Exception e) {
            log.error("==> Error validating token", e);
            return false;
        }
    }

    // 🔍 Method để lấy thông tin chi tiết của token
    public Optional<TokenIntrospectionResponse> getTokenInfo(String token) {
        try {
            IntrospectionRequest request = IntrospectionRequest.builder()
                                                               .token(token)
                                                               .build();

            TokenIntrospectionResponse response = introspectToken(request);
            return response.isActive() ? Optional.of(response) : Optional.empty();
        } catch (Exception e) {
            log.error("==> Error getting token info", e);
            return Optional.empty();
        }
    }

    // 🚪 Logout user all session
    public void logoutSession(IntrospectionRequest tokenIntrospectionRequest) {
        try {
            TokenExchangeResponse clientToken = getClientToken();
            SignedJWT jwt = SignedJWT.parse(tokenIntrospectionRequest.getToken());
            log.info(jwt.getJWTClaimsSet()
                        .getSubject());
            identityClient.logoutSession(realmname, "Bearer " + clientToken.getAccessToken(), jwt.getJWTClaimsSet()
                                                                                                 .getSubject());

        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during logout", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // 🚪 Logout user 1 session
    public void logout(IntrospectionRequest tokenIntrospectionRequest) {
        try {
            TokenExchangeResponse clientToken = getClientToken();
            SignedJWT jwt = SignedJWT.parse(tokenIntrospectionRequest.getToken());
            String sid = (String) jwt.getJWTClaimsSet()
                                     .getClaim("sid");
            log.info("Session ID (sid): " + sid);
            identityClient.logout(realmname, "Bearer " + clientToken.getAccessToken(), sid);

        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during logout", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }


    // 🔐 Đổi mật khẩu người dùng (admin token)
    public void changePassword(String userToken, PasswordChangeRequest request) {
        try {
            String adminToken = getClientToken().getAccessToken(); // gọi từ Redis hoặc service

            SignedJWT jwt = SignedJWT.parse(userToken.replace("Bearer ", ""));
            String userId = jwt.getJWTClaimsSet()
                               .getSubject(); // 👈 lấy userId từ claim "sub"

            if (!request.getPassword()
                        .equals(request.getRetypepassword())) {
                throw new AppException(ErrorCode.PASSWORD_FAILD);
            }

            Credential newCredential = Credential.builder()
                                                 .type("password")
                                                 .temporary(false)
                                                 .value(request.getPassword())
                                                 .build();

            identityClient.changePassword(realmname, "Bearer " + adminToken, userId, newCredential);
        } catch (ParseException e) {
            log.error("Token not sent or invalid", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Lỗi khi đổi mật khẩu", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
