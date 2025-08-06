package com.famtwen.gateway.repositories.httpclients;

import com.famtwen.gateway.dtos.ApiResponse;
import com.famtwen.gateway.dtos.request.IntrospectRequest;
import com.famtwen.gateway.dtos.response.IntrospectResponse;
import com.famtwen.gateway.dtos.response.TokenIntrospectionResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;


public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<TokenIntrospectionResponse>> introspect(@RequestBody IntrospectRequest request);
}