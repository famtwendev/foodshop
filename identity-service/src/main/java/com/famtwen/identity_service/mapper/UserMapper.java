package com.famtwen.identity_service.mapper;

import com.famtwen.identity_service.dto.request.RegistrationRequest;
import com.famtwen.identity_service.dto.response.UserResponse;
import com.famtwen.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegistrationRequest request);

    UserResponse toUserResponse(User user);
}
