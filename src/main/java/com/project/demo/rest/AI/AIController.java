package com.project.demo.rest.AI;

import com.project.demo.logic.dtos.AIRequestDTO;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.service.rtc.service.GroqService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de manejar las interacciones con el servicio de IA.
 *
 * <p>
 * Este controlador recibe los mensajes (prompts) enviados desde el frontend y
 * delega el procesamiento al {@link GroqService}, devolviendo la respuesta generada
 * por el modelo de IA.
 * </p>
 *
 * <p>Ruta base del controlador: <strong>/ai</strong></p>
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    /** Servicio encargado de comunicarse con la API de Groq. */
    private final GroqService groqService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param groqService Servicio que procesa los prompts utilizando Groq.
     */
    public AIController(GroqService groqService) {
        this.groqService = groqService;
    }

    /**
     * Maneja solicitudes POST para enviar un prompt a la IA y recibir una respuesta.
     *
     * <p>Endpoint: <strong>/ai/ask</strong></p>
     *
     * <p>Flujo:</p>
     * <ol>
     *     <li>Recibe un cuerpo JSON con el prompt del usuario.</li>
     *     <li>Invoca {@link GroqService#askGroq(String)} para obtener la respuesta.</li>
     *     <li>Retorna un objeto estándar mediante {@link GlobalResponseHandler}.</li>
     * </ol>
     *
     * @param request Objeto que contiene el texto enviado por el usuario.
     * @param req Información de la solicitud HTTP (para logs o metadata).
     * @return {@link ResponseEntity} con la respuesta generada por la IA.
     */
    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody AIRequestDTO request, HttpServletRequest req) {
        String prompt = request.getPrompt();

        return new GlobalResponseHandler().handleResponse(
                "AI running",
                groqService.askGroq(prompt),
                HttpStatus.OK,
                req
        );
    }
}
