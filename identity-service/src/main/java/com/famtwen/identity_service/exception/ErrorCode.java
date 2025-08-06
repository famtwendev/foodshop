package com.famtwen.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "Email existed, please choose another one", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1009, "Username existed, please choose another one", HttpStatus.BAD_REQUEST),
    USERNAME_IS_MISSING(1010, "Please enter username", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1011, "User not existed", HttpStatus.BAD_REQUEST),
    NULL_EXCEPTION(1012, "Null Pointer Exception", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1013, "Token invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_FAILD(1014, "Password don't match with Retype Password", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1015, "Invalid credentials", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1016, "Email type invalid", HttpStatus.BAD_REQUEST),
    INVALID_NUMBERPHONE(1017,  "Number phone type invalid", HttpStatus.BAD_REQUEST),
    INVALID_DATE(1018,  "Invalid datetime", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
