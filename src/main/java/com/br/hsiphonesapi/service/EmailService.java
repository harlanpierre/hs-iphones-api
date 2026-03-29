package com.br.hsiphonesapi.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String userName, String resetToken);
}
