package com.project.demo.logic.daily;

import java.util.List;

/**
 * Representa la solicitud enviada desde el frontend hacia el backend
 * cuando un usuario interactúa con el Chat de la ceremonia Daily.
 *
 * <p>
 * Este objeto contiene toda la información necesaria para que la IA pueda:
 * <ul>
 *     <li>Entender el estado actual del usuario en la simulación</li>
 *     <li>Identificar roles presentes y faltantes</li>
 *     <li>Reconocer si la sesión es individual o grupal</li>
 *     <li>Aplicar plantillas dinámicas según la dificultad y rol</li>
 *     <li>Analizar el Kanban (TO DO, IN PROGRESS, QA, DONE)</li>
 *     <li>Leer las respuestas del usuario (ayer, hoy, impedimentos)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Este DTO es usado directamente por:
 * <ul>
 *     <li>{@link com.project.demo.rest.daily.DailyChatController}</li>
 *     <li>{@link com.project.demo.logic.service.rtc.service.GroqService}</li>
 * </ul>
 * </p>
 */
public class DailyChatRequest {

    /** Mensaje textual enviado por el usuario en el chat. */
    private String message;

    /**
     * Respuestas de las tres preguntas oficiales de una Daily:
     * <ul>
     *     <li>¿Qué hiciste ayer?</li>
     *     <li>¿Qué harás hoy?</li>
     *     <li>¿Tienes impedimentos?</li>
     * </ul>
     */
    private DailySummaryRequest.Answers answers;

    /** Estructura del tablero KANBAN del usuario (TO DO, IN PROGRESS, QA, DONE). */
    private DailySummaryRequest.Board board;

    /** Lista con los roles humanos presentes en la sesión (Scrum Master, Developer, Product Owner, QA). */
    private List<String> activeRoles;

    /** Rol actual del usuario que está interactuando con la IA. */
    private String userRole;

    /** ID de la simulación, usado para guardar métricas y registrar análisis. */
    private Long simulationId;

    /** Dificultad seleccionada por el usuario (1 = baja, 2 = media, 3 = alta). */
    private int difficulty;


    // =====================================================
    //                     GETTERS
    // =====================================================

    /**
     * @return Texto enviado por el usuario.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Objeto con las respuestas del usuario (ayer/hoy/impedimentos).
     */
    public DailySummaryRequest.Answers getAnswers() {
        return answers;
    }

    /**
     * @return Estado completo del tablero Kanban.
     */
    public DailySummaryRequest.Board getBoard() {
        return board;
    }

    /**
     * @return Lista de roles humanos conectados.
     */
    public List<String> getActiveRoles() {
        return activeRoles;
    }

    /**
     * @return Rol actual del usuario.
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * @return ID de la simulación.
     */
    public Long getSimulationId() {
        return simulationId;
    }

    /**
     * @return Nivel de dificultad seleccionado.
     */
    public int getDifficulty() {
        return difficulty;
    }


    // =====================================================
    //                     SETTERS
    // =====================================================

    /**
     * @param message Mensaje del usuario.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param answers Respuestas de ayer/hoy/impedimentos.
     */
    public void setAnswers(DailySummaryRequest.Answers answers) {
        this.answers = answers;
    }

    /**
     * @param board Tablero Kanban actual.
     */
    public void setBoard(DailySummaryRequest.Board board) {
        this.board = board;
    }

    /**
     * @param activeRoles Roles presentes en la llamada.
     */
    public void setActiveRoles(List<String> activeRoles) {
        this.activeRoles = activeRoles;
    }

    /**
     * @param userRole Rol del usuario actual.
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * @param simulationId ID de la simulación actual.
     */
    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    /**
     * @param difficulty Nivel de dificultad seleccionado.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
