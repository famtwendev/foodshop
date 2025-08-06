package com.famtwen.gateway.services;

import com.famtwen.gateway.dtos.ApiResponse;
import com.famtwen.gateway.dtos.request.IntrospectRequest;
import com.famtwen.gateway.dtos.response.IntrospectResponse;
import com.famtwen.gateway.dtos.response.TokenIntrospectionResponse;
import com.famtwen.gateway.repositories.httpclients.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<TokenIntrospectionResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder().token(token).build())
                             .map(apiResponse -> {
                                 log.info("apiResponse: {}",apiResponse);
                                 TokenIntrospectionResponse rawResult = apiResponse.getResult();
                                 return ApiResponse.<TokenIntrospectionResponse>builder()
                                                   .code(apiResponse.getCode())
                                                   .message(apiResponse.getMessage())
                                                   .timestamp(apiResponse.getTimestamp())
                                                   .result(rawResult) // <-- Đây là phần bạn đang thiếu
                                                   .build();
                             });
    }
}
