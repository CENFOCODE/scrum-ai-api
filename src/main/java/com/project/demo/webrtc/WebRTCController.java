package com.project.demo.webrtc;

import com.project.demo.logic.service.rtc.service.WebRTCService;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para las operaciones WebRTC.
 * Expone endpoints para unirse a salas y enviar mensajes de señalización.
 */
@RestController
@RequestMapping("/api/webrtc")
@CrossOrigin(origins = "http://localhost:4200")
public class WebRTCController {

    private final WebRTCService webRTCService;

    public WebRTCController(WebRTCService webRTCService) {
        this.webRTCService = webRTCService;
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
}
