package com.project.demo.logic.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.dtos.AIResponseDTO;
import com.project.demo.logic.entity.feedback.Feedback;
import com.project.demo.logic.entity.feedback.FeedbackRepository;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulationUser.SimulationUserRepository;
import com.project.demo.logic.service.rtc.service.GroqService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DailySessionService {

    private final DailySessionRepository repository;
    private final SimulationUserRepository simulationUserRepository;
    private final FeedbackRepository feedbackRepository;
    private final GroqService groqService;

    public DailySessionService(
            DailySessionRepository repository,
            SimulationUserRepository simulationUserRepository,
            FeedbackRepository feedbackRepository,
            GroqService groqService
    ) {
        this.repository = repository;
        this.simulationUserRepository = simulationUserRepository;
        this.feedbackRepository = feedbackRepository;
        this.groqService = groqService;
    }

    /**
     * Método principal: Guarda el daily, genera feedback y retorna dailySession + feedbackMessage.
     */
    public Map<String, Object> saveDaily(Map<String, Object> req) {

        Object simIdObj = req.get("simulationId");
        Object simUserIdObj = req.get("simulationUserId");
        Object dailyObj = req.get("daily");

        if (simIdObj == null) throw new RuntimeException("simulationId es requerido");
        if (simUserIdObj == null) throw new RuntimeException("simulationUserId es requerido");
        if (dailyObj == null) throw new RuntimeException("daily es requerido");

        Long simulationId = Long.valueOf(simIdObj.toString());
        Long simulationUserId = Long.valueOf(simUserIdObj.toString());

        // Validar SimulationUser
        SimulationUser simUser = simulationUserRepository.findById(simulationUserId)
                .orElseThrow(() -> new RuntimeException("SimulationUser no encontrado"));

        if (!simUser.getSimulation().getId().equals(simulationId)) {
            throw new RuntimeException("El simulationUser no pertenece a esta simulación.");
        }

        // Construir prompt según rol
        String role = simUser.getScrumRole();
        String basePrompt = getDailyPromptByRole(role);

        String finalPrompt =
                basePrompt +
                        "\n\n--- Respuestas del usuario (JSON) ---\n" +
                        dailyObj.toString() +
                        "\n\nGenera retroalimentación clara, accionable y enfocada en mi desempeño como "
                        + role + ".";

        // Llamar a la IA
        AIResponseDTO aiResponse = groqService.askGroq(finalPrompt);
        String aiFeedback = aiResponse.getAnswer();

        // Guardar DailySession
        DailySession session = new DailySession();
        session.setSimulationId(simulationId);
        session.setUserId(simUser.getUser().getId());
        session.setAnswersJson(dailyObj.toString());
        session.setAiSummary(aiFeedback);

        repository.save(session);

        // Guardar feedback en tabla feedback
        Feedback fb = new Feedback();
        fb.setSimulation(simUser.getSimulation());
        fb.setUser(simUser.getUser());
        fb.setMessage(aiFeedback);
        feedbackRepository.save(fb);

        // Respuesta estándar estilo Retrospective
        Map<String, Object> response = new HashMap<>();
        response.put("dailySession", session);
        response.put("feedbackMessage", aiFeedback);
        response.put("message", "Daily generado y feedback almacenado.");

        return response;
    }

    /**
     * Prompt base por rol (Daily)
     */
    private String getDailyPromptByRole(String role) {
        switch (role) {

            case "Scrum Master":
                return "Actúa como un Scrum Master experto en reuniones Daily.\n" +
                        "Evalúa mi comunicación, facilitación, manejo de bloqueos y claridad de objetivos.";

            case "Developer":
                return "Actúa como un Developer Senior en metodologías ágiles.\n" +
                        "Evalúa claridad técnica, manejo de dependencias, progreso y riesgos.";

            case "Product Owner":
                return "Actúa como un Product Owner experto.\n" +
                        "Evalúa visibilidad del producto, prioridades y claridad del alcance.";

            case "QA":
                return "Actúa como un QA Senior especializado en agilidad.\n" +
                        "Evalúa riesgos de calidad, pruebas necesarias y dependencias.";

            default:
                return "Actúa como experto en ceremonias Daily Scrum.";
        }
    }
}
