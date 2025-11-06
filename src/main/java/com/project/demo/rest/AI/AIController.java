package com.project.demo.rest.AI;

import com.project.demo.logic.dtos.AIRequestDTO;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.service.rtc.service.GroqService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que maneja las solicitudes de chat con la IA.
 * Permite recibir mensajes desde el frontend (Angular)
 * y devolver la respuesta generada por Groq.
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    private final GroqService groqService;

    public AIController(GroqService groqService) {
        this.groqService = groqService;
    }

    /**
     * Endpoint que recibe un mensaje del usuario y devuelve la respuesta de la IA.
     * @param request Contiene el texto enviado por el usuario.
     * @return Respuesta generada por el modelo Groq.
     */
    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody AIRequestDTO request, HttpServletRequest req) {
        String prompt = request.getPrompt();

        return new GlobalResponseHandler().handleResponse("AI running",
                groqService.askGroq(prompt), HttpStatus.OK, req);
    }
}
