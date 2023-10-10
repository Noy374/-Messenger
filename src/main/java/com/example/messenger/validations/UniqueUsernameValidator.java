package com.example.messenger.validations;

import com.example.messenger.anatations.UniqueUsername;
import com.example.messenger.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername,Object>{

    private final UserService userService;
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return userService.checkUserByUsername((String) o);
    }
}