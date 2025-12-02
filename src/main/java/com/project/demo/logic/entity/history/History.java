package com.project.demo.logic.entity.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "final_score")
    private Double finalScore;

    @ManyToOne
    @JoinColumn(name = "simulationUser_id")
    private SimulationUser simulationUser;

    public History() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public SimulationUser getSimulationUser() {
        return simulationUser;
    }
    public void setSimulationUser(SimulationUser simulationUser) {
        this.simulationUser = simulationUser;
    }
}
