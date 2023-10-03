package com.klimmenkov.testtask.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeConstraint {

    String message() default "User's age is not allowed.";

    @Value("${user.minAge}")
    int min() default 18;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
