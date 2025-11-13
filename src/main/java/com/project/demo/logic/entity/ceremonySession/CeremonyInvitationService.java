package com.project.demo.logic.entity.ceremonySession;

import com.project.demo.logic.utils.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de enviar invitaciones a ceremonias ScrumAI.
 * Valida la información y utiliza el EmailService para enviar
 * correos personalizados con los detalles de la ceremonia.
 */
@Service
public class CeremonyInvitationService {

    @Autowired
    private EmailService emailService;

    /**
     * Envía una invitación por correo electrónico a un participante.
     *
     * @param email correo del invitado
     * @param hostName nombre del anfitrión
     * @param ceremonyType tipo de ceremonia (Daily, Review, Planning, etc.)
     * @param difficulty nivel de dificultad (Fácil, Media, Difícil)
     * @param link enlace directo a la sala o simulación
     */
    public void sendCeremonyInvitation(String email, String hostName, String ceremonyType,
                                       String difficulty, String link) {

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("El correo del invitado es obligatorio.");

        if (ceremonyType == null || difficulty == null)
            throw new IllegalArgumentException("Debe especificar el tipo de ceremonia y la dificultad.");

        // 📨 Asunto dinámico
        String subject = String.format("📢 Invitación a %s - ScrumAI", ceremonyType);

        // 💬 Cuerpo del correo
        String body = String.format(
                "Hola 👋,\n\n%s te ha invitado a participar en una ceremonia ScrumAI.\n\n" +
                        "📅 Tipo de ceremonia: %s\n" +
                        "⚙️ Dificultad: %s\n\n" +
                        "🔗 Enlace para unirte:\n%s\n\n" +
                        "¡Nos vemos en la simulación! ☕\n\n— Equipo ScrumAI",
                hostName, ceremonyType, difficulty, link
        );

        // ✉️ Llamada al servicio de correo genérico
        emailService.sendCustomEmail(email, subject, body);

        System.out.printf("✅ Invitación enviada correctamente a %s (%s - %s)%n",
                email, ceremonyType, difficulty);
    }
}
