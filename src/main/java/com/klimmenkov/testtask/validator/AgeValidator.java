package com.klimmenkov.testtask.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class AgeValidator implements ConstraintValidator<AgeConstraint, Date> {

    private int minAge;

    @Override
    public void initialize(AgeConstraint constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Date birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false;
        }

        LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(localBirthDate, currentDate).getYears();

        return age >= minAge;
    }
}