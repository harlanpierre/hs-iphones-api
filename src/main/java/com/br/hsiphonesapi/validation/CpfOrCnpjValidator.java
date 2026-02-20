package com.br.hsiphonesapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

public class CpfOrCnpjValidator implements ConstraintValidator<CpfOrCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Deixa o @NotBlank ou @NotNull validar se é obrigatório
        }

        // Remove caracteres não numéricos para validação
        String cleanValue = value.replaceAll("\\D", "");

        // Tenta validar como CPF
        CPFValidator cpfValidator = new CPFValidator();
        cpfValidator.initialize(null);
        if (cpfValidator.isValid(cleanValue, null)) {
            return true;
        }

        // Tenta validar como CNPJ
        CNPJValidator cnpjValidator = new CNPJValidator();
        cnpjValidator.initialize(null);
        return cnpjValidator.isValid(cleanValue, null);
    }
}