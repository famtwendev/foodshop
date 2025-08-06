package com.famtwen.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    CANNOT_SEND_EMAIL(1008, "Cannot send email", HttpStatus.BAD_REQUEST),
    TEMPLATE_NOT_FOUND(1009, "Template not found", HttpStatus.NOT_FOUND),
    MISSING_REQUIRED_PARAMS(1010, "Missing required parameters", HttpStatus.BAD_REQUEST),
    TEMPLATE_CODE_EXISTS(1011, "Template code already exists", HttpStatus.CONFLICT);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
