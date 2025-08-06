package com.famtwen.identity_service.configuration;

import com.famtwen.identity_service.constants.PredefinedRole;
import com.famtwen.identity_service.entity.Role;
import com.famtwen.identity_service.repository.RoleRepository;
import com.famtwen.identity_service.repository.UserRepository;
import com.famtwen.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

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
    ApplicationRunner applicationRunner(RoleRepository roleRepository, AuthenticationService authenticationService) {
        return args -> {
            log.info("============= Initializing application.....");
//            if (!roleRepository.existsRoleByName(PredefinedRole.ADMIN_ROLE)) {
//                roleRepository.save(
//                        Role.builder()
//                            .name(PredefinedRole.ADMIN_ROLE)
//                            .description("Admin role")
//                            .build()
//                );
//            }
//            if (!roleRepository.existsRoleByName(PredefinedRole.USER_ROLE)) {
//                roleRepository.save(
//                        Role.builder()
//                            .name(PredefinedRole.USER_ROLE)
//                            .description("User role")
//                            .build()
//                );
//            }
            authenticationService.getClientToken();
            log.info("Application initialization completed .....");
        };
    }
}