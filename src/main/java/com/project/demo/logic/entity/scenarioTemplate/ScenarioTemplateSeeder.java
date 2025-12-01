package com.project.demo.logic.entity.scenarioTemplate;

import com.project.demo.logic.entity.scenario.Scenario;
import com.project.demo.logic.entity.scenario.ScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;

@Component
public class ScenarioTemplateSeeder {

    @Autowired
    private ScenarioTemplateRepository scenarioTemplateRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    // Roles disponibles
    private final String[] ROLES = {"Scrum Master", "Developer", "Product Owner", "QA"};

    // Dificultades: 1=Baja, 2=Media, 3=Alta
    private final int[] DIFFICULTIES = {1, 2, 3};

    @PostConstruct
    private void createScenarioTemplate() {
        if (scenarioTemplateRepository.count() > 0) return;

        List<ScenarioTemplate> templates = new ArrayList<>();

        // ===========================
        //  SCENARIO 1 - PLANNING
        // ===========================
        Scenario planning = scenarioRepository.findById(1L).orElse(null);
        if (planning != null) {
            templates.addAll(createTemplatesForScenario(planning, getPlanningPrompts()));
        }

        // ===========================
        //  SCENARIO 2 - DAILY
        // ===========================
        Scenario daily = scenarioRepository.findById(2L).orElse(null);
        if (daily != null) {
            templates.addAll(createTemplatesForScenario(daily, getDailyPrompts()));
        }

        // ===========================
        //  SCENARIO 3 - REVIEW
        // ===========================
        Scenario review = scenarioRepository.findById(3L).orElse(null);
        if (review != null) {
            templates.addAll(createTemplatesForScenario(review, getReviewPrompts()));
        }

        // ===========================
        //  SCENARIO 4 - RETROSPECTIVE
        // ===========================
        Scenario retro = scenarioRepository.findById(4L).orElse(null);
        if (retro != null) {
            templates.addAll(createTemplatesForScenario(retro, getRetrospectivePrompts()));
        }

        scenarioTemplateRepository.saveAll(templates);
    }

    private List<ScenarioTemplate> createTemplatesForScenario(Scenario scenario, PromptMatrix prompts) {
        List<ScenarioTemplate> templates = new ArrayList<>();

        for (int difficulty : DIFFICULTIES) {
            for (String role : ROLES) {
                String prompt = prompts.getPrompt(difficulty, role);

                // Crear un ID único basado en: scenario + difficulty + role
                int stepOrder = (difficulty * 1000) + getRoleIndex(role);

                templates.add(createTemplate(scenario, stepOrder, prompt));
            }
        }

        return templates;
    }

    private int getRoleIndex(String role) {
        switch (role) {
            case "Scrum Master": return 1;
            case "Developer": return 2;
            case "Product Owner": return 3;
            case "QA": return 4;
            default: return 1;
        }
    }

    // ===========================
    //  PROMPTS PARA PLANNING
    // ===========================
    private PromptMatrix getPlanningPrompts() {
        PromptMatrix matrix = new PromptMatrix();

        // DIFICULTAD BAJA
        matrix.setPrompt(1, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Planning. Dificultad: Baja.\n" +
                        "Tu rol es facilitar la reunión y ayudar al equipo a definir qué trabajo realizarán en el próximo sprint.\n" +
                        "Haz preguntas básicas sobre prioridades, capacidad del equipo y dependencias.");

        matrix.setPrompt(1, "Developer",
                "Actúa como Developer en una reunión de Planning. Dificultad: Baja.\n" +
                        "Participa activamente estimando tareas, identificando dependencias técnicas y comprometiéndote con el trabajo del sprint.\n" +
                        "Haz preguntas técnicas básicas sobre implementación.");

        matrix.setPrompt(1, "Product Owner",
                "Actúa como Product Owner en una reunión de Planning. Dificultad: Baja.\n" +
                        "Tu rol es explicar las historias de usuario, sus criterios de aceptación y prioridades.\n" +
                        "Responde preguntas del equipo sobre funcionalidad y valor de negocio.");

        matrix.setPrompt(1, "QA",
                "Actúa como QA en una reunión de Planning. Dificultad: Baja.\n" +
                        "Participa identificando criterios de aceptación, casos de prueba y posibles riesgos de calidad.\n" +
                        "Haz preguntas sobre testeo y definición de 'Done'.");

        // DIFICULTAD MEDIA
        matrix.setPrompt(2, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Planning. Dificultad: Media.\n" +
                        "Facilita refinamiento de historias, análisis de riesgos y definición del Sprint Goal.\n" +
                        "Guía al equipo en estimaciones y resolución de impedimentos.");

        matrix.setPrompt(2, "Developer",
                "Actúa como Developer en una reunión de Planning. Dificultad: Media.\n" +
                        "Lidera estimaciones técnicas, identifica dependencias complejas y propone soluciones de arquitectura.\n" +
                        "Cuestiona viabilidad técnica y sugiere alternativas.");

        matrix.setPrompt(2, "Product Owner",
                "Actúa como Product Owner en una reunión de Planning. Dificultad: Media.\n" +
                        "Negocia scope vs capacidad, ajusta prioridades y define Sprint Goal alineado con objetivos de negocio.\n" +
                        "Toma decisiones sobre trade-offs y acepta compromisos.");

        matrix.setPrompt(2, "QA",
                "Actúa como QA en una reunión de Planning. Dificultad: Media.\n" +
                        "Define estrategias de testing, identifica riesgos de calidad y estima esfuerzo de testing.\n" +
                        "Propone criterios de aceptación detallados y plan de pruebas.");

        // DIFICULTAD ALTA
        matrix.setPrompt(3, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Planning. Dificultad: Alta.\n" +
                        "Maneja conflictos de prioridades, facilita decisiones complejas y aplica técnicas avanzadas de estimación.\n" +
                        "Coaching avanzado y gestión de impedimentos críticos.");

        matrix.setPrompt(3, "Developer",
                "Actúa como Developer en una reunión de Planning. Dificultad: Alta.\n" +
                        "Lidera análisis de arquitectura compleja, identifica deuda técnica crítica y propone refactoring estratégico.\n" +
                        "Mentoriza en estimaciones y cuestiona asunciones técnicas.");

        matrix.setPrompt(3, "Product Owner",
                "Actúa como Product Owner en una reunión de Planning. Dificultad: Alta.\n" +
                        "Maneja stakeholders con intereses conflictivos, optimiza ROI del sprint y toma decisiones estratégicas.\n" +
                        "Balancea deuda técnica vs nuevas features.");

        matrix.setPrompt(3, "QA",
                "Actúa como QA en una reunión de Planning. Dificultad: Alta.\n" +
                        "Diseña estrategias de testing complejas, automatización y análisis de riesgos avanzado.\n" +
                        "Lidera discusiones sobre calidad y performance.");

        return matrix;
    }

    // ===========================
    //  PROMPTS PARA DAILY
    // ===========================
    private PromptMatrix getDailyPrompts() {
        PromptMatrix matrix = new PromptMatrix();

        // DIFICULTAD BAJA
        matrix.setPrompt(1, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Daily. Dificultad: Baja.\n" +
                        "Facilita las tres preguntas básicas: ¿Qué hiciste ayer?, ¿Qué harás hoy?, ¿Tienes impedimentos?\n" +
                        "Mantén la reunión enfocada y breve.");

        matrix.setPrompt(1, "Developer",
                "Actúa como Developer en una reunión de Daily. Dificultad: Baja.\n" +
                        "Reporta tu progreso de manera concisa, comunica impedimentos simples y coordina trabajo básico.\n" +
                        "Sé específico pero breve en tus respuestas.");

        matrix.setPrompt(1, "Product Owner",
                "Actúa como Product Owner en una reunión de Daily. Dificultad: Baja.\n" +
                        "Escucha el progreso del equipo, responde preguntas básicas sobre prioridades.\n" +
                        "Clarifica dudas simples sobre historias de usuario.");

        matrix.setPrompt(1, "QA",
                "Actúa como QA en una reunión de Daily. Dificultad: Baja.\n" +
                        "Reporta progreso en testing, comunica bloqueos simples en pruebas.\n" +
                        "Coordina entrega de funcionalidades para testear.");

        // DIFICULTAD MEDIA
        matrix.setPrompt(2, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Daily. Dificultad: Media.\n" +
                        "Identifica impedimentos complejos, facilita sincronización entre tareas interdependientes.\n" +
                        "Promueve colaboración efectiva y detecta riesgos del sprint.");

        matrix.setPrompt(2, "Developer",
                "Actúa como Developer en una reunión de Daily. Dificultad: Media.\n" +
                        "Comunica progreso técnico detallado, identifica bloqueos de dependencias.\n" +
                        "Propone soluciones colaborativas y ofrece ayuda a otros miembros.");

        matrix.setPrompt(2, "Product Owner",
                "Actúa como Product Owner en una reunión de Daily. Dificultad: Media.\n" +
                        "Evalúa progreso hacia el Sprint Goal, ajusta prioridades si es necesario.\n" +
                        "Toma decisiones sobre cambios de scope menores.");

        matrix.setPrompt(2, "QA",
                "Actúa como QA en una reunión de Daily. Dificultad: Media.\n" +
                        "Reporta resultados de testing complejos, identifica riesgos de calidad.\n" +
                        "Coordina estrategias de testing con el equipo.");

        // DIFICULTAD ALTA
        matrix.setPrompt(3, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Daily. Dificultad: Alta.\n" +
                        "Facilita resolución de impedimentos críticos, maneja conflictos del equipo.\n" +
                        "Optimiza flujo de trabajo bajo presión y toma decisiones estratégicas rápidas.");

        matrix.setPrompt(3, "Developer",
                "Actúa como Developer en una reunión de Daily. Dificultad: Alta.\n" +
                        "Lidera resolución de problemas técnicos complejos, mentoriza a otros desarrolladores.\n" +
                        "Toma decisiones arquitecturales críticas que afectan el sprint.");

        matrix.setPrompt(3, "Product Owner",
                "Actúa como Product Owner en una reunión de Daily. Dificultad: Alta.\n" +
                        "Maneja presión de stakeholders, toma decisiones estratégicas rápidas.\n" +
                        "Redefine prioridades bajo incertidumbre y maneja cambios críticos.");

        matrix.setPrompt(3, "QA",
                "Actúa como QA en una reunión de Daily. Dificultad: Alta.\n" +
                        "Maneja crisis de calidad, diseña testing de emergencia.\n" +
                        "Toma decisiones críticas sobre release readiness y gestiona riesgos mayores.");

        return matrix;
    }

    // ===========================
    //  PROMPTS PARA REVIEW
    // ===========================
    private PromptMatrix getReviewPrompts() {
        PromptMatrix matrix = new PromptMatrix();

        // DIFICULTAD BAJA
        matrix.setPrompt(1, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Review. Dificultad: Baja.\n" +
                        "Facilita la demostración de funcionalidades, ayuda a recopilar feedback básico.\n" +
                        "Coordina la participación del equipo y mantén el enfoque en los entregables.");

        matrix.setPrompt(1, "Developer",
                "Actúa como Developer en una reunión de Review. Dificultad: Baja.\n" +
                        "Demuestra las funcionalidades desarrolladas, explica implementación técnica básica.\n" +
                        "Responde preguntas del Product Owner y stakeholders sobre el trabajo realizado.");

        matrix.setPrompt(1, "Product Owner",
                "Actúa como Product Owner en una reunión de Review. Dificultad: Baja.\n" +
                        "Evalúa funcionalidades entregadas, proporciona feedback directo.\n" +
                        "Acepta o rechaza trabajo basado en criterios de aceptación simples.");

        matrix.setPrompt(1, "QA",
                "Actúa como QA en una reunión de Review. Dificultad: Baja.\n" +
                        "Presenta resultados de testing básicos, confirma criterios de aceptación cumplidos.\n" +
                        "Reporta bugs encontrados y estado general de calidad.");

        // DIFICULTAD MEDIA
        matrix.setPrompt(2, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Review. Dificultad: Media.\n" +
                        "Facilita feedback estructurado, maneja expectativas de stakeholders.\n" +
                        "Guía discusiones sobre mejoras del proceso y próximos pasos.");

        matrix.setPrompt(2, "Developer",
                "Actúa como Developer en una reunión de Review. Dificultad: Media.\n" +
                        "Presenta arquitectura técnica, explica decisiones de diseño complejas.\n" +
                        "Defiende soluciones implementadas y discute trade-offs técnicos.");

        matrix.setPrompt(2, "Product Owner",
                "Actúa como Product Owner en una reunión de Review. Dificultad: Media.\n" +
                        "Evalúa valor de negocio entregado, recolecta feedback de múltiples stakeholders.\n" +
                        "Planifica próximos pasos estratégicos y ajusta roadmap si es necesario.");

        matrix.setPrompt(2, "QA",
                "Actúa como QA en una reunión de Review. Dificultad: Media.\n" +
                        "Presenta métricas de calidad detalladas, analiza cobertura de testing.\n" +
                        "Propone mejoras en procesos de QA y evalúa riesgos residuales.");

        // DIFICULTAD ALTA
        matrix.setPrompt(3, "Scrum Master",
                "Actúa como Scrum Master en una reunión de Review. Dificultad: Alta.\n" +
                        "Maneja conflictos entre stakeholders, facilita decisiones estratégicas complejas.\n" +
                        "Optimiza procesos de feedback y maneja expectativas conflictivas.");

        matrix.setPrompt(3, "Developer",
                "Actúa como Developer en una reunión de Review. Dificultad: Alta.\n" +
                        "Lidera discusiones técnicas avanzadas, justifica decisiones arquitecturales críticas.\n" +
                        "Propone innovaciones técnicas y maneja cuestionamientos complejos.");

        matrix.setPrompt(3, "Product Owner",
                "Actúa como Product Owner en una reunión de Review. Dificultad: Alta.\n" +
                        "Negocia cambios estratégicos con stakeholders, evalúa ROI complejo.\n" +
                        "Toma decisiones de producto críticas bajo presión y maneja conflictos de intereses.");

        matrix.setPrompt(3, "QA",
                "Actúa como QA en una reunión de Review. Dificultad: Alta.\n" +
                        "Presenta análisis de riesgos complejos, evalúa readiness para producción.\n" +
                        "Toma decisiones críticas sobre calidad del release y maneja escalaciones.");

        return matrix;
    }

    // ===========================
    //  PROMPTS PARA RETROSPECTIVE
    // ===========================
    private PromptMatrix getRetrospectivePrompts() {
        PromptMatrix matrix = new PromptMatrix();

        // DIFICULTAD BAJA
        matrix.setPrompt(1, "Scrum Master",
                 "Yo voy a ser el Scrum Master para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                         "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(1, "Developer",
                "Yo voy a ser el Developer para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(1, "Product Owner",
                "Yo voy a ser el Product Owner para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(1, "QA",
                "Yo voy a ser el QA para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        // DIFICULTAD MEDIA
        matrix.setPrompt(2, "Scrum Master",
                "Yo voy a ser el Scrum Master para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(2, "Developer",
                "Yo voy a ser el Developer para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(2, "Product Owner",
                "Yo voy a ser el Product Owner para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(2, "QA",
                "Yo voy a ser el QA para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        // DIFICULTAD ALTA
        matrix.setPrompt(3, "Scrum Master",
                "Yo voy a ser el Scrum Master para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(3, "Developer",
                "Yo voy a ser el Developer para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(3, "Product Owner",
                "Yo voy a ser el Product Owner para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        matrix.setPrompt(3, "QA",
                "Yo voy a ser el QA para esta reunion de Retrospective. Dificultad: Baja.\n" +
                        "Dame una situacion simulada para poder practicar mis abilidades y dame feedback al respecto.\n" +
                        "Solo me tienes que dar la situacion simulada y esperar recibir mis notas de la reunion.\n" +
                        "La notas de la reunion van a ser cuatro tipos: Que nos ayudó?, Que nos atrasó?, Ideas, Acciones.\n" +
                        "Basados en las notas enviadas, que feedback me darias para mejorar en mi rol de scrum?");

        return matrix;
    }

    private ScenarioTemplate createTemplate(Scenario scenario, int stepOrder, String prompt) {
        ScenarioTemplate template = new ScenarioTemplate();
        template.setScenario(scenario);
        template.setStepOrder(stepOrder);
        template.setPromptTemplate(prompt);
        return template;
    }

    // Clase auxiliar para organizar prompts
    private static class PromptMatrix {
        private final String[][] prompts = new String[4][5]; // [difficulty][role]

        public void setPrompt(int difficulty, String role, String prompt) {
            int roleIndex = getRoleIndex(role);
            prompts[difficulty][roleIndex] = prompt;
        }

        public String getPrompt(int difficulty, String role) {
            int roleIndex = getRoleIndex(role);
            return prompts[difficulty][roleIndex];
        }

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
}