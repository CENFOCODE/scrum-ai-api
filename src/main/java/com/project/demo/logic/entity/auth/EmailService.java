package com.project.demo.logic.entity.auth;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String ADMIN_GMAIL = "cenfocode@gmail.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTemporaryPassword(String toEmail, String tempPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(ADMIN_GMAIL);
            helper.setTo(toEmail);
            helper.setSubject("Restablecimiento de contraseña");

            String htmlBody =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<body style='font-family: Arial, sans-serif; color: #1C3B44; background-color: #f7f7f7; padding: 20px;'>" +
                            "<div style='max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                            "<h2 style='color: #1C3B44; text-align: center;'>Restablecimiento de Contraseña</h2>" +

                            "<p>Hola,</p>" +

                            "<p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta. Como medida de seguridad, hemos generado una contraseña temporal que podrás usar para acceder nuevamente.</p>" +

                            "<p style='font-size: 16px; font-weight: bold; background: #f0f4ff; border-left: 4px solid #1C3B44; padding: 10px;'>" +
                            "Tu contraseña temporal es: <span style='color: #1C3B44;'>" + tempPassword + "</span>" +
                            "</p>" +

                            "<p>Por razones de seguridad, te recomendamos iniciar sesión y cambiar esta contraseña lo antes posible desde la sección de configuración de tu cuenta.</p>" +

                            "<p>Si no solicitaste este cambio, puedes ignorar este correo o contactarte con nuestro equipo de soporte.</p>" +

                            "<br>" +
                            "<p>Saludos cordiales,<br><strong>Equipo Scrum AI</strong></p>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlBody, true); // <<--- IMPORTANTE: true habilita HTML

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
