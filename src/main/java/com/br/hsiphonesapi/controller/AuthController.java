package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.ForgotPasswordRequestDTO;
import com.br.hsiphonesapi.dto.request.LoginRequestDTO;
import com.br.hsiphonesapi.dto.request.RegisterRequestDTO;
import com.br.hsiphonesapi.dto.request.ResetPasswordRequestDTO;
import com.br.hsiphonesapi.dto.response.AuthResponseDTO;
import com.br.hsiphonesapi.dto.response.ForgotPasswordResponseDTO;
import com.br.hsiphonesapi.service.AuthService;
import com.br.hsiphonesapi.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e registro de usuários")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT.")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário no sistema. Requer role (ADMIN, VENDEDOR, TECNICO).")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperação de senha", description = "Gera um token para redefinição de senha.")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO dto) {
        return ResponseEntity.ok(passwordResetService.forgotPassword(dto));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha com token", description = "Redefine a senha do usuário utilizando o token de recuperação.")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
