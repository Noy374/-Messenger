package com.example.messenger.anatations;


import com.example.messenger.validations.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "Email must be unique";

    Class<?>[] groups() default{};

    Class<? extends Payload>[] payload() default {};
}
