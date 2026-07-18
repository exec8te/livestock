package com.livestock.validations.annotations;

import com.livestock.validations.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {

    String message() default
            "Password must contain at least 12 characters, one uppercase letter, one lowercase letter, one number and one special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}