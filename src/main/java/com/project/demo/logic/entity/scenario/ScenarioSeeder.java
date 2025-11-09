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
                new Scenario(null, "Planning",
                        "Reunión donde se define qué trabajo se realizará en el próximo sprint, se estiman tareas y se asignan responsabilidades.",
                        60, "Backlog del proyecto actualizado", "Equipo Scrum", null, null,
                        "Definir y planificar el trabajo del sprint", "Medium", "Planning"),

                new Scenario(null, "Daily",
                        "Reunión diaria para sincronizar al equipo, identificar impedimentos y actualizar el progreso del trabajo.",
                        60, "Tareas en curso", "Equipo Scrum", null, null,
                        "Actualizar el progreso y resolver impedimentos", "Easy", "Daily"),

                new Scenario(null, "Grooming",
                        "Sesión para revisar y priorizar el backlog, detallando las historias de usuario y asegurando que estén listas para el próximo sprint.",
                        60, "Backlog detallado y priorizado", "Equipo Scrum", null, null,
                        "Mantener el backlog refinado y listo para planificación", "Medium", "Grooming"),

                new Scenario(null, "Review",
                        "Reunión de revisión del sprint para presentar los entregables completados al Product Owner y recibir retroalimentación.",
                        60, "Demostración de funcionalidades completadas", "Equipo Scrum y stakeholders", null, null,
                        "Validar los entregables del sprint y recibir feedback", "Medium", "Review"),

                new Scenario(null, "Retrospective",
                        "Reunión de retrospectiva para reflexionar sobre el sprint pasado, identificar mejoras y planear acciones correctivas.",
                        60, "Lista de mejoras y acciones de seguimiento", "Equipo Scrum", null, null,
                        "Mejorar procesos y colaboración del equipo", "Medium", "Retrospective")
        );

        scenarioRepository.saveAll(ceremonies);
    }
}
