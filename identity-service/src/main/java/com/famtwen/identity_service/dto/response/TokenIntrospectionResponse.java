package com.famtwen.identity_service.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenIntrospectionResponse {
    @JsonProperty("active")
    private boolean active;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("exp")
    private Long exp;

    @JsonProperty("iat")
    private Long iat;

    @JsonProperty("nbf")
    private Long nbf;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("aud")
    private String aud;

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("jti")
    private String jti;

    // Static method để tạo response cho token không active
    public static TokenIntrospectionResponse inactive() {
        return TokenIntrospectionResponse.builder()
                                                                           .active(false)
                                                                           .build();
    }
}
