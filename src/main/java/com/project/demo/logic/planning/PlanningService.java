package com.project.demo.logic.planning;

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
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final SimulationUserRepository simulationUserRepository;
    private final FeedbackRepository feedbackRepository;
    private final GroqService groqService;

    public PlanningService(
            PlanningRepository planningRepository,
            SimulationUserRepository simulationUserRepository,
            FeedbackRepository feedbackRepository,
            GroqService groqService
    ) {
        this.planningRepository = planningRepository;
        this.simulationUserRepository = simulationUserRepository;
        this.feedbackRepository = feedbackRepository;
        this.groqService = groqService;
    }

    public Map<String, Object> save(Map<String, Object> request) {

        Object simIdObj = request.get("simulationId");
        Object simUserIdObj = request.get("simulationUserId");
        Object textObj = request.get("planning");

        if (simIdObj == null) throw new RuntimeException("simulationId es requerido");
        if (simUserIdObj == null) throw new RuntimeException("simulationUserId es requerido");
        if (textObj == null) throw new RuntimeException("planning es requerido");

        Long simulationId = Long.valueOf(simIdObj.toString());
        Long simulationUserId = Long.valueOf(simUserIdObj.toString());

        SimulationUser simUser = simulationUserRepository.findById(simulationUserId)
                .orElseThrow(() -> new RuntimeException("SimulationUser no encontrado"));

        if (!simUser.getSimulation().getId().equals(simulationId)) {
            throw new RuntimeException("El simulationUser no pertenece a esta simulación.");
        }

        ObjectMapper mapper = new ObjectMapper();
        String textJson;
        try {
            textJson = mapper.writeValueAsString(textObj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Planning plan = new Planning();
        plan.setSimulationId(simulationId);
        plan.setPlanningText(textJson);
        planningRepository.save(plan);

        String role = simUser.getScrumRole();
        String basePrompt = getPromptByRole(role);

        String finalPrompt =
                basePrompt +
                        "\n\n--- Notas del usuario (String) ---\n" +
                        textJson +
                        "\n\nDame retroalimentación concreta basada en estas notas.";

        AIResponseDTO aiResponse = groqService.askGroq(finalPrompt);
        String feedbackText = aiResponse.getAnswer();

        // Guardar feedback en BD
        Feedback fb = new Feedback();
        fb.setSimulation(simUser.getSimulation());
        fb.setUser(simUser.getUser());
        fb.setMessage(feedbackText);
        feedbackRepository.save(fb);

        Map<String, Object> response = new HashMap<>();
        response.put("planning", plan);
        response.put("feedbackMessage", feedbackText);

        return response;
    }

    public Planning getBySimulation(Long simulationId) {
        return planningRepository.findBySimulationId(simulationId)
                .orElseThrow(() -> new RuntimeException("No existe planning para esta simulación."));
    }

    private String getPromptByRole(String role) {
        switch (role) {
            case "Product Owner":
                return "Actúa como Product Owner en una reunión de Retrospective. Dificultad: Alta.\n" +
                        "Dame una situación simulada y retroalimentación basada en mis notas.";
            case "Scrum Master":
                return "Actúa como Scrum Master en una Retrospective. Dificultad: Alta.\n" +
                        "Analiza mis notas y genera retroalimentación enfocada en facilitation y mejoras de equipo.";
            case "Developer":
                return "Actúa como desarrollador del equipo Scrum, en una Retrospective.\n" +
                        "Dame retroalimentación técnica y de colaboración basada en mis notas.";
            default:
                return "Actúa como experto en Retrospectives de Scrum.";
        }
    }
}
