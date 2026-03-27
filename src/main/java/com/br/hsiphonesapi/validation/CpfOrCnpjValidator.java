package com.br.hsiphonesapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfOrCnpjValidator implements ConstraintValidator<CpfOrCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Deixa o @NotBlank ou @NotNull validar se é obrigatório
        }

        String cleanValue = value.replaceAll("\\D", "");

        if (cleanValue.length() == 11) {
            return isValidCPF(cleanValue);
        } else if (cleanValue.length() == 14) {
            return isValidCNPJ(cleanValue);
        }

        return false; // Se não tem 11 nem 14 dígitos, é inválido
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false; // Rejeita CPFs com números repetidos (ex: 111.111.111-11)

        try {
            int calc1 = 0, calc2 = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(cpf.charAt(i));
                calc1 += digito * (10 - i);
                calc2 += digito * (11 - i);
            }

            int resto1 = (calc1 * 10) % 11;
            if (resto1 == 10) resto1 = 0;

            calc2 += resto1 * 2;
            int resto2 = (calc2 * 10) % 11;
            if (resto2 == 10) resto2 = 0;

            return resto1 == Character.getNumericValue(cpf.charAt(9)) &&
                    resto2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) return false; // Rejeita CNPJs com números repetidos

        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int calc1 = 0, calc2 = 0;
            for (int i = 0; i < 12; i++) {
                int digito = Character.getNumericValue(cnpj.charAt(i));
                calc1 += digito * pesos1[i];
                calc2 += digito * pesos2[i];
            }

            int resto1 = calc1 % 11;
            int digito1 = resto1 < 2 ? 0 : 11 - resto1;

            calc2 += digito1 * pesos2[12];
            int resto2 = calc2 % 11;
            int digito2 = resto2 < 2 ? 0 : 11 - resto2;

            return digito1 == Character.getNumericValue(cnpj.charAt(12)) &&
                    digito2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }
}