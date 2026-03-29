package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@hsphones.com}")
    private String mailFrom;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        if (!mailEnabled) {
            log.info("Envio de e-mail desabilitado. Link de recuperação para '{}': {}", userName, resetLink);
            return;
        }

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("resetLink", resetLink);
        context.setVariable("token", resetToken);

        String html = templateEngine.process("password-reset", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject("HS iPhones - Recuperação de Senha");
            helper.setText(html, true);
            mailSender.send(message);
            log.info("E-mail de recuperação de senha enviado para '{}'", to);
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail de recuperação para '{}': {}", to, e.getMessage());
        }
    }
}
