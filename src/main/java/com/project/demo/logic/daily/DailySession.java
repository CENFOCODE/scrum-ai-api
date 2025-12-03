package com.project.demo.logic.daily;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_sessions")
public class DailySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;   // ID del usuario que realizó la Daily

    @Lob
    @Column(columnDefinition = "TEXT")
    private String boardJson;  // Guardamos el Kanban como JSON

    @Column(columnDefinition = "TEXT")
    private String answersJson;  // Guardamos las respuestas como JSON

    @Column(columnDefinition = "TEXT")
    private String aiSummary;  // Resumen generado por IA

    private LocalDateTime createdAt = LocalDateTime.now();
    private Long simulationId;
    private String dailyJson;

    public DailySession() {}

    // Getters y setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getBoardJson() { return boardJson; }

    public void setBoardJson(String boardJson) { this.boardJson = boardJson; }

    public String getAnswersJson() { return answersJson; }

    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }

    public String getAiSummary() { return aiSummary; }

    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }


    public String getDailyJson() {
        return dailyJson;
    }

    public void setDailyJson(String dailyJson) {
        this.dailyJson = dailyJson;
    }
    }

