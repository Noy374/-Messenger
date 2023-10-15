package com.example.messenger.validations;

import com.example.messenger.anatations.UniqueEmail;
import com.example.messenger.service.EmailService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail,Object> {
    private final EmailService emailService;
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return emailService.checkUserByEmail((String) o);
    }
}
