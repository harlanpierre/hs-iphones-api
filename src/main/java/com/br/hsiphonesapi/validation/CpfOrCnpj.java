package com.br.hsiphonesapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfOrCnpjValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfOrCnpj {
    String message() default "Deve ser um CPF ou CNPJ válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}