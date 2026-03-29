package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.ForgotPasswordRequestDTO;
import com.br.hsiphonesapi.dto.request.ResetPasswordRequestDTO;
import com.br.hsiphonesapi.dto.response.ForgotPasswordResponseDTO;

public interface PasswordResetService {

    ForgotPasswordResponseDTO forgotPassword(ForgotPasswordRequestDTO dto);

    void resetPassword(ResetPasswordRequestDTO dto);
}
