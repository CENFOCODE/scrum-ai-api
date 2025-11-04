package com.project.demo.logic.entity.auth;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String ADMIN_GMAIL = "cenfocode@gmail.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTemporaryPassword(String toEmail, String tempPassword) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(ADMIN_GMAIL);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Contraseña temporal");
        mailMessage.setText("Esta es tu contraseña temporal: " + tempPassword);
        mailSender.send(mailMessage);
    }
}
