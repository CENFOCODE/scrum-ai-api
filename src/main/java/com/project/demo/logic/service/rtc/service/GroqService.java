package com.project.demo.logic.service.rtc.service;

import com.project.demo.logic.dtos.AIResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

/**
 * Servicio encargado de comunicarse con la API de Groq (modelo Llama 3.1).
 *
 * <p>
 * Este servicio envía un prompt de texto al endpoint de Chat Completions
 * de Groq y obtiene la respuesta generada por el modelo.
 * </p>
 *
 * <p>
 * La API de Groq utiliza un formato compatible con OpenAI, por lo que
 * el cuerpo de la petición incluye un arreglo de mensajes con los roles:
 * <strong>system</strong>, <strong>user</strong> o <strong>assistant</strong>.
 * En este caso únicamente se envía el mensaje del usuario.
 * </p>
 */
@Service
public class GroqService {

    /**
     * API Key para autenticarse ante la API de Groq.
     * Se obtiene desde application.properties:
     *
     * groq.api.key=TU_API_KEY
     */
    @Value("${groq.api.key}")
    private String apiKey;

    /** Cliente HTTP usado para ejecutar solicitudes REST. */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Envía una solicitud POST al endpoint de Groq para obtener una respuesta del modelo.
     *
     * <p>Pasos del proceso:</p>
     * <ol>
     *   <li>Construye los encabezados HTTP (incluyendo autenticación Bearer).</li>
     *   <li>Arma el cuerpo JSON en el formato esperado.</li>
     *   <li>Realiza la solicitud al endpoint de Chat Completions.</li>
     *   <li>Extrae el texto generado desde la propiedad <code>choices[0].message.content</code>.</li>
     * </ol>
     *
     * @param prompt Texto enviado por el usuario para que la IA lo analice o procese.
     * @return Un {@link AIResponseDTO} que contiene la respuesta generada por la IA.
     *         Si ocurre algún problema, se devuelve un mensaje de error en su interior.
     */
    public AIResponseDTO askGroq(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        // Encabezados HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Cuerpo de la solicitud en formato compatible con OpenAI/Groq
        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        // Extraer respuesta desde "choices"
        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.getBody().get("choices");

        AIResponseDTO airesponse = new AIResponseDTO();

        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            if (message != null && message.containsKey("content")) {
                String answer = message.get("content").toString();
                airesponse.setAnswer(answer);
                return airesponse;
            }
        }

        // Respuesta por defecto si algo salió mal
        airesponse.setMessage("No se pudo obtener respuesta de la IA.");
        return airesponse;
    }
}
