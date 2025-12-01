package com.project.demo.logic.service.rtc.service;

import com.project.demo.logic.daily.DailyChatRequest;
import com.project.demo.logic.daily.DailySummaryRequest;
import com.project.demo.logic.daily.DailyTemplateService;
import com.project.demo.logic.dtos.AIResponseDTO;
import com.project.demo.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Servicio encargado de manejar toda la comunicación con la API de Groq (modelo Llama 3.1).
 *
 * <p>
 * Este servicio contiene:
 * <ul>
 *     <li>Un método genérico {@link #askGroq(String)} usado por todas las ceremonias y el chatbot.</li>
 *     <li>Lógica especializada para la ceremonia Daily mediante {@link #askDailyChat(DailyChatRequest)}.</li>
 *     <li>Un método interno {@link #askGroqMessages(List)} para prompts con múltiples roles (system/user).</li>
 * </ul>
 * </p>
 *
 * <p>
 * IMPORTANTE:
 * El método genérico {@code askGroq()} NO debe ser modificado porque es usado por Planning,
 * Review, Retrospective, el ChatBot general y otros módulos del proyecto.
 * </p>
 */
@Service
public class GroqService {

    /** Servicio que obtiene plantillas dinámicas de Daily almacenadas en la base de datos. */
    private final DailyTemplateService dailyTemplateService;

    public GroqService(DailyTemplateService dailyTemplateService) {
        this.dailyTemplateService = dailyTemplateService;
    }

    /** API Key para autenticarse ante Groq. Configurada en application.properties */
    @Value("${groq.api.key}")
    private String apiKey;

    /** Cliente HTTP utilizado para realizar llamadas REST contra la API de Groq. */
    private final RestTemplate restTemplate = new RestTemplate();


    // =====================================================================
    // MÉTODO GENÉRICO – NO TOCAR
    // =====================================================================

    /**
     * Ejecuta una solicitud simple al modelo Llama 3.1 enviando únicamente un prompt del usuario.
     *
     * <p>
     * Este método es utilizado globalmente por:
     * <ul>
     *   <li>Chatbot general</li>
     *   <li>Planning</li>
     *   <li>Review</li>
     *   <li>Retrospective</li>
     *   <li>Entrenamiento de roles</li>
     * </ul>
     * </p>
     *
     * @param prompt Texto enviado por el usuario.
     * @return {@link AIResponseDTO} con la respuesta generada o mensaje de error.
     */
    public AIResponseDTO askGroq(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        AIResponseDTO dto = new AIResponseDTO();

        try {
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");

            String answer =
                    ((Map<String, Object>) choices.get(0).get("message"))
                            .get("content")
                            .toString();

            dto.setAnswer(answer);

        } catch (Exception e) {
            dto.setMessage("No se pudo obtener respuesta de la IA.");
        }

        return dto;
    }


    // =====================================================================
    //  DAILY CHAT — Implementación personalizada
    // =====================================================================

    /**
     * Genera la respuesta de IA para una sesión de Daily Scrum, usando:
     * <ul>
     *     <li>Roles activos/faltantes del equipo.</li>
     *     <li>Modo individual o grupal.</li>
     *     <li>Plantillas dinámicas desde la BD según dificultad + rol.</li>
     *     <li>KANBAN actual del usuario.</li>
     *     <li>Mensaje real enviado por el usuario.</li>
     * </ul>
     *
     * <p>Este método construye un prompt completo enviando:</p>
     * <ul>
     *     <li>System Prompt (comportamiento de la IA)</li>
     *     <li>Plantilla base cargada desde DB</li>
     *     <li>Prompt dinámico con datos del usuario</li>
     * </ul>
     *
     * @param request Objeto con mensaje, roles, dificultad, board y contexto de Daily.
     * @return Texto generado por Groq respondiendo como los roles faltantes.
     */
    public String askDailyChat(DailyChatRequest request) {

        // 1) Roles activos vs faltantes
        List<String> allRoles = List.of("Scrum Master", "Developer", "Product Owner", "QA");
        List<String> active = request.getActiveRoles();

        List<String> missing = allRoles.stream()
                .filter(r -> active.stream().noneMatch(r::equalsIgnoreCase))
                .toList();

        boolean isIndividual = active.size() == 1;

        String roleInstruction = isIndividual
                ? "Modo individual → la IA debe asumir TODOS los roles faltantes: " + String.join(", ", missing)
                : "Modo grupal → la IA solo responde como los roles faltantes: " + String.join(", ", missing);

        // 2) Plantilla desde BD por dificultad + rol del usuario
        var template = dailyTemplateService.getDailyTemplate(
                request.getDifficulty(),
                request.getUserRole()
        );

        String templateText = template != null ? template.getPromptTemplate() : "";


        // 3) Prompt dinámico construido con datos reales del usuario
        String userPrompt = """
                Rol del usuario: %s
                Roles activos: %s
                Roles faltantes: %s

                Kanban:
                %s

                Instrucciones:
                %s

                Mensaje del usuario:
                %s
                """
                .formatted(
                        request.getUserRole(),
                        String.join(", ", active),
                        String.join(", ", missing),
                        JsonUtils.toJson(request.getBoard()),
                        roleInstruction,
                        request.getMessage()
                );

        // 4) System Prompt específico para Daily
        String systemPrompt = """
                Eres un asistente Scrum especializado en Daily.
                Responde SIEMPRE con este formato:
                [Rol IA]: mensaje breve.
                No expliques teoría Scrum. Mantén todo conciso.
                """;

        // 5) Construcción del mensaje en formato Chat (system + system + user)
        List<Map<String, Object>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "system", "content", templateText),
                Map.of("role", "user", "content", userPrompt)
        );

        // 6) Llamada interna a Groq
        return askGroqMessages(messages);
    }


    // =====================================================================
    //  Método interno para prompts multi-rol
    // =====================================================================

    /**
     * Envia un arreglo de mensajes (system/user/assistant) al endpoint de Groq
     * usando el mismo modelo pero sin modificar el método genérico {@link #askGroq(String)}.
     *
     * Este método solo es usado por Daily.
     *
     * @param messages Lista de mensajes con roles system/user.
     * @return Respuesta generada por la IA.
     */
    private String askGroqMessages(List<Map<String, Object>> messages) {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", messages
        );

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        ResponseEntity<Map> res = restTemplate.postForEntity(url, req, Map.class);

        try {
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) res.getBody().get("choices");

            return ((Map<String, Object>) choices.get(0).get("message"))
                    .get("content")
                    .toString();

        } catch (Exception e) {
            return "Error al obtener respuesta de la IA.";
        }
    }

    /**
     * Genera un resumen final del Daily usando Groq.
     * Actualmente se mantiene en versión simple para evitar romper otros controladores.
     */
    public String askDailySummary(DailySummaryRequest request) {

        String prompt = """
        Eres un asistente experto en Scrum.
        Genera un resumen claro y breve del Daily.

        Ayer:
        %s

        Hoy:
        %s

        Impedimentos:
        %s

        El resumen debe ser objetivo, corto y útil para métricas de Sprint.
        """.formatted(
                request.getAnswers() != null ? request.getAnswers().getYesterday() : "",
                request.getAnswers() != null ? request.getAnswers().getToday() : "",
                request.getAnswers() != null ? request.getAnswers().getImpediments() : ""
        );

        return askGroq(prompt).getAnswer();
    }

}
