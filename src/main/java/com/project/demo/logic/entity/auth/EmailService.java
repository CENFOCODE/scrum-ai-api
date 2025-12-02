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


    public void sendInvitation(String toEmail, String inviterName, String ceremonyType, String invitationLink, String roomId) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(ADMIN_GMAIL);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Invitación a " + (ceremonyType != null ? ceremonyType : "Ceremonia Scrum") + " - Scrum AI");

        String emailBody = String.format("""
            Hola,
            
            %s te ha invitado a participar en una ceremonia de %s.
            
            Únete usando este enlace:
            %s
            
            ID de la sala: %s
            
            ¡Nos vemos en la sesión!
            
            ---
            Equipo Scrum AI
            """,
                inviterName != null ? inviterName : "Un usuario",
                ceremonyType != null ? ceremonyType : "Scrum",
                invitationLink,
                roomId
        );

        mailMessage.setText(emailBody);
        mailSender.send(mailMessage);

        System.out.println("Invitación enviada a: " + toEmail + " para la sala: " + roomId);
    }
}
