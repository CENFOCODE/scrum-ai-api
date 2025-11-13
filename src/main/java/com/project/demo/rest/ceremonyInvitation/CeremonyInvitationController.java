package com.project.demo.rest.ceremonyInvitation;

import com.project.demo.logic.dto.ceremony.CeremonyInviteRequest;
import com.project.demo.logic.entity.ceremonySession.CeremonyInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de manejar las peticiones de invitación
 * a ceremonias Scrum. Permite a un usuario anfitrión enviar un correo
 * a otros participantes con el enlace de la ceremonia.
 *
 * ENDPOINT: POST /api/ceremonies/invite
 *
 * CUERPO JSON ESPERADO:
 * {
 *   "recipientEmail": "usuario@example.com",
 *   "senderName": "Key Morales",
 *   "ceremonyType": "Daily",
 *   "difficulty": "Media",
 *   "roomLink": "https://trycloudflare.com/room-abc123"
 * }
 */
@RestController
@RequestMapping("/api/ceremonies")
@CrossOrigin(origins = "*")
public class CeremonyInvitationController {

    private final CeremonyInvitationService invitationService;

    public CeremonyInvitationController(CeremonyInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Envía una invitación por correo a un participante.
     *
     * @param request objeto JSON con los datos de la invitación.
     * @return respuesta HTTP 200 si el correo fue enviado correctamente.
     */
    @PostMapping("/invite")
    public ResponseEntity<String> inviteParticipant(@RequestBody CeremonyInviteRequest request) {
        invitationService.sendCeremonyInvitation(
                request.getRecipientEmail(),
                request.getSenderName(),
                request.getCeremonyType(),
                request.getDifficulty(),
                request.getRoomLink()
        );

        return ResponseEntity.ok("Invitación enviada correctamente a " + request.getRecipientEmail());
    }
}
