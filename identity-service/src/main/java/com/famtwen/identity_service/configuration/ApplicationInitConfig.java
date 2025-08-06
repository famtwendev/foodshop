package com.famtwen.identity_service.configuration;

import com.famtwen.identity_service.dto.request.RegistrationRequest;
import com.famtwen.identity_service.dto.request.RoleRequest;
import com.famtwen.identity_service.dto.response.RoleResponse;
import com.famtwen.identity_service.dto.response.UserResponse;
import com.famtwen.identity_service.service.AuthenticationService;
import com.famtwen.identity_service.service.RoleService;
import com.famtwen.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    @Value("${keycloak.default-roles}")
    @NonFinal
    String roleDefault;

    @Value("${keycloak.user-test}")
    @NonFinal
    String newUser;

    // Runner mặc định: luôn chạy, chỉ lấy client token
//    @Bean
//    ApplicationRunner defaultApplicationRunner(AuthenticationService authenticationService) {
//        return args -> {
//            log.info("============= Initializing application (default).....");
//            authenticationService.getClientToken();
//            log.info("Keycloak admin token initialized and cached");
//            log.info("Application initialization completed .....");
//        };
//    }
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserService userService, RoleService roleService, AuthenticationService authenticationService) {
        return args -> {
            log.info("============= Initializing application.....");
            authenticationService.getClientToken();


            RoleResponse responseDefaultRole = roleService.create(RoleRequest.builder()
                                          .name(roleDefault)
                                          .build());
            RoleResponse responseAdminRole = roleService.create(RoleRequest.builder()
                                          .name("ADMIN")
                                          .description("This Role has all the powers")
                                          .build());
            log.info("Init success role: {} & {}", responseDefaultRole.getId(),responseAdminRole.getId());
            UserResponse userResponse = userService.register(RegistrationRequest.builder()
                                                                                .username(newUser)
                                                                                .password(newUser)
                                                                                .build());
            log.info("Init success new user with id : {}", userResponse.getKid());
            log.info("Application initialization completed .....");
        };
    }
}