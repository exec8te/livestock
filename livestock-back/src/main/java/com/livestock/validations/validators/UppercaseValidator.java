package com.livestock.validations.validators;

import com.livestock.validations.annotations.UppercaseOnly;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UppercaseValidator
        implements ConstraintValidator<UppercaseOnly, String> {


    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if (value == null) {
            return true;
        }

        return value.equals(value.toUpperCase());
    }
}