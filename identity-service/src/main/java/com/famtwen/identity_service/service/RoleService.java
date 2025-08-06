package com.famtwen.identity_service.service;

import com.famtwen.identity_service.dto.request.RoleRequest;
import com.famtwen.identity_service.dto.request.keycloak.TokenExchangeResponse;
import com.famtwen.identity_service.dto.response.RoleResponse;
import com.famtwen.identity_service.entity.Role;
import com.famtwen.identity_service.exception.AppException;
import com.famtwen.identity_service.exception.ErrorCode;
import com.famtwen.identity_service.exception.ErrorNomalizer;
import com.famtwen.identity_service.mapper.RoleMapper;
import com.famtwen.identity_service.repository.RoleRepository;
import com.famtwen.identity_service.repository.http.IdentityClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    ErrorNomalizer errorNormalizer;
    IdentityClient roleClient;
    private final AuthenticationService authenticationService;
    @Value("${keycloak.client-id}")
    @NonFinal
    String clientId;

    @Value("${keycloak.client-secret}")
    @NonFinal
    String clientSecret;

    @Value("${keycloak.realm}")
    @NonFinal
    String realmname;


    public RoleResponse create(RoleRequest request) {
        try {
            TokenExchangeResponse clientToken = authenticationService.getClientToken();

            RoleResponse existingRoleInKeycloak = null;
            try {
                existingRoleInKeycloak = roleClient.existsRoleByName(realmname, "Bearer " + clientToken.getAccessToken(), request.getName());
            } catch (FeignException.NotFound notFound) {
                log.info("Role not found in Keycloak: {}", request.getName());
            }

            Optional<Role> existingRoleInDB = roleRepository.findByName(request.getName());

            boolean keycloakHasRole = existingRoleInKeycloak != null &&
                    existingRoleInKeycloak.getId() != null &&
                    !existingRoleInKeycloak.getId()
                                           .isEmpty() &&
                    existingRoleInKeycloak.getName()
                                          .equals(request.getName());

            if (keycloakHasRole) {
                if (existingRoleInDB.isEmpty()) {
                    Role savedRole = roleRepository.save(Role.builder()
                                                             .id(existingRoleInKeycloak.getId())
                                                             .name(existingRoleInKeycloak.getName())
                                                             .description(existingRoleInKeycloak.getDescription())
                                                             .build());
                    return roleMapper.toRoleResponse(savedRole);
                } else {
                    Role updatedRole = existingRoleInDB.get();
                    updatedRole.setId(existingRoleInKeycloak.getId());
                    updatedRole = roleRepository.save(updatedRole);
                    return roleMapper.toRoleResponse(updatedRole);
                }
            } else {
                roleClient.createRole(realmname, "Bearer " + clientToken.getAccessToken(), request);
                existingRoleInKeycloak = roleClient.existsRoleByName(realmname, "Bearer " + clientToken.getAccessToken(), request.getName());
                Role role = roleMapper.toRole(request);
                role.setId(existingRoleInKeycloak.getId());
                role = roleRepository.save(role);
                return roleMapper.toRoleResponse(role);
            }
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        } catch (Exception e) {
            log.error("==> Unexpected error during role creation", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                             .stream()
                             .map(roleMapper::toRoleResponse)
                             .toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
