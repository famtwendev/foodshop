package com.famtwen.identity_service.validators;

import com.famtwen.identity_service.dto.request.PasswordChangeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordChangeRequest> {


    @Override
    public boolean isValid(PasswordChangeRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getRetypepassword() == null) {
            return false;
        }
        if (!request.getPassword().equals(request.getRetypepassword())) {
            // ❗ Tắt default message
            context.disableDefaultConstraintViolation();

            // ❗ Tạo custom message và gán cho field "retypepassword". LỖI PASSWORD_FAILD phải có trong Error Code
            context.buildConstraintViolationWithTemplate("PASSWORD_FAILD")
                   .addPropertyNode("retypepassword")
                   .addConstraintViolation();

            return false;
        }

        return true;
    }
}
