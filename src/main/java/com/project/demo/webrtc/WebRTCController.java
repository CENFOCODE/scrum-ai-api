package com.project.demo.webrtc;

import com.project.demo.logic.entity.auth.EmailService;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.service.rtc.service.WebRTCService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para las operaciones WebRTC.
 * Expone endpoints para unirse a salas y enviar mensajes de señalización.
 */
@RestController
@RequestMapping("/api/webrtc")
@CrossOrigin(origins = "http://localhost:4200")
public class WebRTCController {

    private final WebRTCService webRTCService;
    private final EmailService emailService;

    public WebRTCController(WebRTCService webRTCService, EmailService emailService) {
        this.webRTCService = webRTCService;
        this.emailService = emailService;
    }

    @PostMapping("/join/{roomId}")
    public String joinRoom(@PathVariable String roomId) {
        return webRTCService.joinRoom(roomId);
    }

    @PostMapping("/signal/{roomId}")
    public String sendSignal(@PathVariable String roomId, @RequestBody String message) {
        webRTCService.sendSignal(roomId, message);
        return "📨 Señal procesada correctamente.";
    }

    /**
     * Envía una invitación por email para unirse a una sala WebRTC.
     *
     * @param request JSON con: email, roomId, inviterName, ceremonyType
     * @param httpRequest request HTTP para manejo de respuesta
     * @return ResponseEntity con el resultado
     */
    @PostMapping("/send-invitation")
    public ResponseEntity<?> sendInvitation(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest
    ) {
        String toEmail = request.get("email");
        String roomId = request.get("roomId");
        String inviterName = request.get("inviterName");
        String ceremonyType = request.get("ceremonyType");
        String scenarioId = request.get("scenarioId");

        if (toEmail == null || toEmail.trim().isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "El email es requerido",
                    HttpStatus.BAD_REQUEST,
                    httpRequest
            );
        }

        if (roomId == null || roomId.trim().isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "El ID de sala es requerido",
                    HttpStatus.BAD_REQUEST,
                    httpRequest
            );
        }

        try {
            String invitationLink = "http://localhost:4200/app/scenario?room="
                    + roomId + "&autoJoin=true&scenarioId=" + scenarioId;
            emailService.sendInvitation(toEmail, inviterName, ceremonyType, invitationLink, roomId);

            return new GlobalResponseHandler().handleResponse(
                    "Invitación enviada exitosamente a " + toEmail,
                    HttpStatus.OK,
                    httpRequest
            );
        } catch (Exception e) {
            System.err.println("Error enviando invitación: " + e.getMessage());
            return new GlobalResponseHandler().handleResponse(
                    "Error al enviar la invitación: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    httpRequest
            );
        }
    }
}
