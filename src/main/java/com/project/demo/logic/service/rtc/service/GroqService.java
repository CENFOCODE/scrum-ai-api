package com.project.demo.logic.service.rtc.service;

import com.project.demo.logic.dtos.AIResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Servicio encargado de comunicarse con la API de Groq (modelo Llama 3.1).
 * Envía el texto del usuario (prompt) y devuelve la respuesta generada por la IA.
 */
@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Envía una solicitud POST a la API de Groq con el prompt del usuario
     * y retorna el texto de la respuesta generada por el modelo.
     *
     * @param prompt Texto del usuario.
     * @return Respuesta de la IA como String.
     */
    public AIResponseDTO askGroq(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        // Encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Cuerpo del request
        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        AIResponseDTO airesponse = new AIResponseDTO();
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message != null && message.containsKey("content")) {
                String answer = message.get("content").toString();

                airesponse.setAnswer(answer);
                return airesponse;
            }
        }
        airesponse.setMessage("⚠️ No se pudo obtener respuesta de la IA.");
        return airesponse;
    }
}
