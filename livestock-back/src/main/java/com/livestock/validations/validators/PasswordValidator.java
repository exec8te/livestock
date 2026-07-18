package com.livestock.validations.validators;

import com.livestock.validations.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator
        implements ConstraintValidator<ValidPassword, String> {


    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[0-9])" +
                    "(?=.*[.!*?%@=$£\\[\\]+])" +
                    ".{12,255}$"
    );


    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context
    ) {

        if (password == null) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}