package com.livestock.validations.annotations;

import com.livestock.validations.validators.UppercaseValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UppercaseValidator.class)
public @interface UppercaseOnly {

    String message() default "Field must contain only uppercase characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}