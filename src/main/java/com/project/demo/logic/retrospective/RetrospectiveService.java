package com.project.demo.logic.retrospective;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.dtos.AIResponseDTO;
import com.project.demo.logic.entity.feedback.Feedback;
import com.project.demo.logic.entity.feedback.FeedbackRepository;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulationUser.SimulationUserRepository;

import com.project.demo.logic.service.rtc.service.GroqService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;
    private final SimulationUserRepository simulationUserRepository;
    private final FeedbackRepository feedbackRepository;
    private final GroqService groqService;

    public RetrospectiveService(
            RetrospectiveRepository retrospectiveRepository,
            SimulationUserRepository simulationUserRepository,
            FeedbackRepository feedbackRepository,
            GroqService groqService
    ) {
        this.retrospectiveRepository = retrospectiveRepository;
        this.simulationUserRepository = simulationUserRepository;
        this.feedbackRepository = feedbackRepository;
        this.groqService = groqService;
    }

    public Retrospective save(Map<String, Object> request) {

        System.out.println("BODY RECIBIDO EN /save => " + request);

        Object simIdObj = request.get("simulationId");
        Object simUserIdObj = request.get("simulationUserId");
        Object notesObj = request.get("retrospective");  // ← CORREGIDO

        if (simIdObj == null) throw new RuntimeException("simulationId es requerido");
        if (simUserIdObj == null) throw new RuntimeException("simulationUserId es requerido");
        if (notesObj == null) throw new RuntimeException("retrospective es requerido");

        Long simulationId = Long.valueOf(simIdObj.toString());
        Long simulationUserId = Long.valueOf(simUserIdObj.toString());

        // Validar SimulationUser
        SimulationUser simUser = simulationUserRepository.findById(simulationUserId)
                .orElseThrow(() -> new RuntimeException("SimulationUser no encontrado"));

        if (!simUser.getSimulation().getId().equals(simulationId)) {
            throw new RuntimeException("El simulationUser no pertenece a esta simulación.");
        }

        // Convertir las notas a JSON
        ObjectMapper mapper = new ObjectMapper();
        String notesJson = null;
        try {
            notesJson = mapper.writeValueAsString(notesObj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Retrospective retro = new Retrospective();
        retro.setSimulationId(simulationId);
        retro.setRetrospectiveJson(notesJson);
        retro = retrospectiveRepository.save(retro);

        // Construir prompt según el rol
        String role = simUser.getScrumRole();
        String basePrompt = getPromptByRole(role);

        String finalPrompt =
                basePrompt +
                        "\n\n--- Notas del usuario (JSON) ---\n" +
                        notesJson +
                        "\n\nDame retroalimentación concreta basada en estas notas.";

        // Llamar a Groq
        AIResponseDTO aiResponse = groqService.askGroq(finalPrompt);

        // Guardar feedback
        Feedback fb = new Feedback();
        fb.setSimulation(simUser.getSimulation());
        fb.setUser(simUser.getUser());
        fb.setMessage(aiResponse.getAnswer());
        feedbackRepository.save(fb);

        return retro;
    }

    public Retrospective getBySimulation(Long simulationId) {
        return retrospectiveRepository.findBySimulationId(simulationId)
                .orElseThrow(() -> new RuntimeException("No existe retrospectiva para esta simulación."));
    }

    private String getPromptByRole(String role) {
        switch (role) {
            case "Product Owner":
                return "Actúa como Product Owner en una reunión de Retrospective. Dificultad: Alta.\n" +
                        "Dame una situación simulada y retroalimentación basada en mis notas.";
            case "Scrum Master":
                return "Actúa como Scrum Master en una Retrospective. Dificultad: Alta.\n" +
                        "Analiza mis notas y genera retroalimentación enfocada en facilitation y mejoras de equipo.";
            case "DevTeam":
                return "Actúa como desarrollador del equipo Scrum, en una Retrospective.\n" +
                        "Dame retroalimentación técnica y de colaboración basada en mis notas.";
            default:
                return "Actúa como experto en Retrospectives de Scrum.";
        }
    }
}

