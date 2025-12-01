package com.project.demo.rest.scenarioTemplate;

import com.project.demo.logic.entity.scenarioTemplate.ScenarioTemplate;
import com.project.demo.logic.entity.scenarioTemplate.ScenarioTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ScenarioTemplateRestController {
    @Autowired
    private ScenarioTemplateRepository scenarioTemplateRepository;

    /**
     * Endpoint para obtener template de prompt específico
     * Ejemplo: GET /scenario-template?scenarioId=1&difficulty=3&role=Developer
     */
    @GetMapping("/scenario-template")
    public ResponseEntity<Optional<ScenarioTemplate>> getTemplate(
            @RequestParam("scenarioId") Long scenarioId,
            @RequestParam("difficulty") Integer difficulty,
            @RequestParam("role") String role) {

        // Calcular stepOrder usando la misma fórmula que en el Seeder
        Integer stepOrder = calculateStepOrder(difficulty, role);

        // Buscar template en base de datos
        Optional<ScenarioTemplate> template = scenarioTemplateRepository
                .findByScenarioIdAndStepOrder(scenarioId, stepOrder);

        if (template != null) {
            return ResponseEntity.ok(template);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Calcula stepOrder usando misma lógica que ScenarioTemplateSeeder
     * Fórmula: (difficulty * 1000) + roleIndex
     */
    private Integer calculateStepOrder(Integer difficulty, String role) {
        int roleIndex = getRoleIndex(role);
        return (difficulty * 1000) + roleIndex;
    }

    /**
     * Mapea rol a índice (misma lógica que en ScenarioTemplateSeeder)
     */
    private int getRoleIndex(String role) {
        switch (role) {
            case "Scrum Master": return 1;
            case "Developer": return 2;
            case "Product Owner": return 3;
            case "QA": return 4;
            default: return 1;
        }
    }
}