package com.adrian.bank.management.system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContactNumberValidator implements ConstraintValidator<ContactNumberConstraint, String> {

    @Override
    public void initialize(ContactNumberConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return phone != null && phone.matches("[0-9]+")
                && (phone.length() > 8) && (phone.length() < 14);
    }
}
