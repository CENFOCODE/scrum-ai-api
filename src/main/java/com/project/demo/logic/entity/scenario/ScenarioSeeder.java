package com.project.demo.logic.entity.scenario;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScenarioSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final ScenarioRepository scenarioRepository;

    public ScenarioSeeder(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createCeremonies();
    }

    private void createCeremonies() {
        if (scenarioRepository.count() > 0) return;

        List<Scenario> ceremonies = List.of(
                new Scenario(null, null,
                        "Reunión donde se define qué trabajo se realizará en el próximo sprint, se estiman tareas y se asignan responsabilidades.",
                        120, "Backlog del proyecto actualizado", "Equipo Scrum", null, null,
                        "Definir y planificar el trabajo del sprint", null, "Planning"),

                new Scenario(null, null,
                        "Reunión diaria para sincronizar al equipo, identificar impedimentos y actualizar el progreso del trabajo.",
                        15, "Tareas en curso", "Equipo Scrum", null, null,
                        "Actualizar el progreso y resolver impedimentos", null, "Daily"),

                new Scenario(null, null,
                        "Reunión de revisión del sprint para presentar los entregables completados al Product Owner y recibir retroalimentación.",
                        60, "Demostración de funcionalidades completadas", "Equipo Scrum y stakeholders", null, null,
                        "Validar los entregables del sprint y recibir feedback", null, "Review"),

                new Scenario(null, null,
                        "Reunión de retrospectiva para reflexionar sobre el sprint pasado, identificar mejoras y planear acciones correctivas.",
                        60, "Lista de mejoras y acciones de seguimiento", "Equipo Scrum", null, null,
                        "Mejorar procesos y colaboración del equipo", null, "Retrospective")
        );



        scenarioRepository.saveAll(ceremonies);
    }
}