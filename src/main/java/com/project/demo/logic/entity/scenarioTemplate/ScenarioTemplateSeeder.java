package com.project.demo.logic.entity.scenarioTemplate;

import com.project.demo.logic.entity.scenario.Scenario;
import com.project.demo.logic.entity.scenario.ScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;

@Component
@Order(3)
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
        String generateTasksByStatus = "\nGenerame una lista de tareas separadas por guion donde status puede ser TODO,DONE,QA,IN_PROGRESS de la simulacion con el siguiente formato" +
                "por ejemplo [TODO-revisar funcionalidad x,QA-revisar funcionalidad y,...] no uses las llaves en ningun otro lado para que pueda obtener los datos por medio de javascript" ;

        String generateTasksByStatus2 =
              "`Genera ademas una lista de tareas en distintos status en el siguiente formato exacto:\n" +
                      "STATUS - descripcion\n" +
                      "\n" +
                      "Donde STATUS debe ser SOLO uno de estos:\n" +
                      "TODO, IN_PROGRESS, QA, DONE\n" +
                      "\n" +
                      "Importante:\n" +
                      "- Cada tarea debe ir en una línea separada.\n" +
                      "- Usa SOLO este formato:\n" +
                      "TODO - descripcion\n" +
                      "IN_PROGRESS - descripcion\n" +
                      "QA - descripcion\n" +
                      "DONE - descripcion`;";

//        String generateTasksByStatus2 =
//                "`Genera ademas una lista de tareas en el siguiente formato exacto:\n" +
//                        "STATUS - descripcion\n" +
//                        "\n" +
//                        "Donde STATUS debe ser SOLO uno de estos:\n" +
//                        "TODO, IN_PROGRESS, QA, DONE\n" +
//                        "\n" +
//                        "Importante:\n" +
//                        "- Cada tarea debe ir en una línea separada.\n" +
//                        "- Usa SOLO este formato:\n" +
//                        "TODO - descripcion\n" +
//                        "IN_PROGRESS - descripcion\n" +
//                        "QA - descripcion\n" +
//                        "DONE - descripcion`;";

// ======================================================
// ===============   DAILY — DIFICULTAD BAJA  ===========
// ======================================================

// SCRUM MASTER — BAJA
        matrix.setPrompt(1, "Scrum Master",
                "Yo voy a ser Scrum Master en esta reunión de Daily. Dificultad: Baja.\n" +
                        "Dame una situación simulada simple del sprint donde existan tareas básicas relacionadas con pedidos y actualización del menú.\n" +
                        "Espera mis respuestas del Daily y luego dame feedback sobre cómo facilito la reunión.\n\n" +
                        "# Tareas del Daily\n" +
                        generateTasksByStatus
        );

// DEVELOPER — BAJA
        matrix.setPrompt(1, "Developer",
                "Yo voy a ser Developer en esta reunión de Daily. Dificultad: Baja.\n" +
                        "Dame una situación simulada básica del sprint con tareas sencillas como creación de citas y vista del calendario.\n" +
                        "Espera mis respuestas del Daily y luego dame feedback técnico.\n\n" +
                        "# Tareas del Daily\n" +
                        generateTasksByStatus2
        );

// PRODUCT OWNER — BAJA
        matrix.setPrompt(1, "Product Owner",
                "Yo voy a ser Product Owner en esta reunión de Daily. Dificultad: Baja.\n" +
                        "Dame una situación simulada simple donde existan historias pequeñas relacionadas con organización de tareas.\n" +
                        "Espera mis respuestas del Daily y luego dame feedback sobre claridad de prioridades.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Revisar prioridad de historias simples de organización\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Aclarar criterios de aceptación de tareas básicas\"\n" +
                        "- QA:\n" +
                        "  - \"Validar historias implementadas por el equipo\"\n" +
                        "- DONE:\n" +
                        "  - \"Aceptar tareas completadas sin cambios\"\n"
        );

// QA — BAJA
        matrix.setPrompt(1, "QA",
                "Yo voy a ser QA en esta reunión de Daily. Dificultad: Baja.\n" +
                        "Dame una situación simulada simple con pruebas básicas del módulo de inventario.\n" +
                        "Espera mis respuestas y luego dame feedback sobre mi rol como QA.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Planear pruebas básicas para manejo de productos\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Ejecutar pruebas manuales simples\"\n" +
                        "- QA:\n" +
                        "  - \"Reportar bugs menores encontrados\"\n" +
                        "- DONE:\n" +
                        "  - \"Confirmar historias que cumplen criterios básicos\"\n"
        );


// ======================================================
// ===============   DAILY — DIFICULTAD MEDIA  ===========
// ======================================================

// SCRUM MASTER — MEDIA
        matrix.setPrompt(2, "Scrum Master",
                "Yo voy a ser Scrum Master en esta reunión de Daily. Dificultad: Media.\n" +
                        "Dame una situación simulada moderada del sprint con dependencias entre carrito de compras y métodos de pago.\n" +
                        "Espera mis respuestas y luego dame feedback sobre manejo de bloqueos.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Identificar tareas bloqueadas por integración de pagos\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Facilitar sincronización entre desarrolladores del checkout\"\n" +
                        "- QA:\n" +
                        "  - \"Dar seguimiento a impedimentos de pruebas de pago\"\n" +
                        "- DONE:\n" +
                        "  - \"Cerrar tareas desbloqueadas recientemente\"\n"
        );

// DEVELOPER — MEDIA
        matrix.setPrompt(2, "Developer",
                "Yo voy a ser Developer en esta reunión de Daily. Dificultad: Media.\n" +
                        "Dame una situación simulada del sprint con complejidad moderada entre módulos de notas y asistencia.\n" +
                        "Espera mis respuestas del Daily y luego dame retro técnica.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Iniciar implementación de componente intermedio de reportes\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Resolver dependencias entre módulos académicos\"\n" +
                        "- QA:\n" +
                        "  - \"Enviar código a revisión técnica\"\n" +
                        "- DONE:\n" +
                        "  - \"Finalizar funcionalidad con complejidad media\"\n"
        );

// PRODUCT OWNER — MEDIA
        matrix.setPrompt(2, "Product Owner",
                "Yo voy a ser Product Owner en esta reunión de Daily. Dificultad: Media.\n" +
                        "Dame una situación simulada donde existan cambios de prioridad por disponibilidad y quejas de clientes.\n" +
                        "Espera mis respuestas y luego dame retro sobre priorización.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Analizar impacto de nuevas prioridades en reservas\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Ajustar criterios de aceptación según feedback\"\n" +
                        "- QA:\n" +
                        "  - \"Validar historias revisadas con stakeholders\"\n" +
                        "- DONE:\n" +
                        "  - \"Confirmar prioridad final de funcionalidades revisadas\"\n"
        );

// QA — MEDIA
        matrix.setPrompt(2, "QA",
                "Yo voy a ser QA en esta reunión de Daily. Dificultad: Media.\n" +
                        "Dame una situación simulada con pruebas funcionales complejas relacionadas con disponibilidad de camas.\n" +
                        "Espera mis respuestas y luego dame retro sobre calidad.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Planear pruebas de funcionalidades críticas\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Ejecutar pruebas funcionales más profundas\"\n" +
                        "- QA:\n" +
                        "  - \"Detectar bugs de impacto medio\"\n" +
                        "- DONE:\n" +
                        "  - \"Verificar historias listas para validación externa\"\n"
        );


// ======================================================
// ===============   DAILY — DIFICULTAD ALTA  ===========
// ======================================================

// SCRUM MASTER — ALTA
        matrix.setPrompt(3, "Scrum Master",
                "Yo voy a ser Scrum Master en esta reunión de Daily. Dificultad: Alta.\n" +
                        "Dame una situación simulada compleja del sprint con bloqueos críticos relacionados con seguridad y transferencias.\n" +
                        "Espera mis respuestas y luego dame feedback avanzado.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Identificar riesgos críticos que afectan el sprint\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Facilitar resolución intensiva de bloqueos complejos\"\n" +
                        "- QA:\n" +
                        "  - \"Monitorear dependencias de alto impacto en seguridad\"\n" +
                        "- DONE:\n" +
                        "  - \"Documentar resoluciones críticas aplicadas\"\n"
        );

// DEVELOPER — ALTA
        matrix.setPrompt(3, "Developer",
                "Yo voy a ser Developer en esta reunión de Daily. Dificultad: Alta.\n" +
                        "Dame una situación simulada técnica compleja con problemas de ruteo avanzado y procesamiento en tiempo real.\n" +
                        "Espera mis respuestas y luego dame retro técnica experta.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Planear abordaje de refactor crítico del motor de rutas\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Resolver bugs severos en lógica de optimización\"\n" +
                        "- QA:\n" +
                        "  - \"Preparar código para pruebas avanzadas de performance\"\n" +
                        "- DONE:\n" +
                        "  - \"Completar feature técnica compleja y validada\"\n"
        );

// PRODUCT OWNER — ALTA
        matrix.setPrompt(3, "Product Owner",
                "Yo voy a ser Product Owner en esta reunión de Daily. Dificultad: Alta.\n" +
                        "Dame una situación simulada con prioridades conflictivas y urgencias médicas.\n" +
                        "Espera mis respuestas y luego dame retro avanzada.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Reevaluar valor del negocio bajo presión clínica\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Gestionar prioridades conflictivas entre módulos críticos\"\n" +
                        "- QA:\n" +
                        "  - \"Validar funcionalidades críticas con especialistas\"\n" +
                        "- DONE:\n" +
                        "  - \"Aceptar entregables estratégicos del sprint\"\n"
        );

// QA — ALTA
        matrix.setPrompt(3, "QA",
                "Yo voy a ser QA en esta reunión de Daily. Dificultad: Alta.\n" +
                        "Dame una situación simulada con riesgos críticos, fallas mayores y presión por estabilidad del sistema.\n" +
                        "Espera mis análisis y luego dame feedback experto.\n\n" +
                        "# Tareas del Daily\n" +
                        "- TODO:\n" +
                        "  - \"Analizar riesgos severos del sistema\"\n" +
                        "- IN_PROGRESS:\n" +
                        "  - \"Validar correcciones críticas bajo presión\"\n" +
                        "- QA:\n" +
                        "  - \"Ejecutar pruebas avanzadas de calidad\"\n" +
                        "- DONE:\n" +
                        "  - \"Confirmar readiness para release crítico\"\n"
        );
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