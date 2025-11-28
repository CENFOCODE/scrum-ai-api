package com.project.demo.logic.entity.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.logic.entity.ceremonySession.CeremonySession;
import com.project.demo.logic.entity.feedback.Feedback;
import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.improvementPlan.ImprovementPlan;
import com.project.demo.logic.entity.scenario.Scenario;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulationMetric.SimulationMetric;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "simulations")
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "difficulty_level", nullable = false)
    private String difficultyLevel;

    @Column(name = "start_date",  nullable = false, updatable = false)
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(nullable = false, length = 50)
    private String status = "active";

    @Column(name = "average_score")
    private Double averageScore;

    @OneToMany(mappedBy = "simulation")
    @JsonIgnore
    private List<Feedback> feedbackList;

    @OneToMany(mappedBy = "simulation")
    @JsonIgnore
    private List<SimulationMetric> metrics;

    @OneToMany(mappedBy = "relatedSimulation")
    @JsonIgnore
    private List<ImprovementPlan> improvementPlans;

    @OneToMany(mappedBy = "simulation")
    @JsonIgnore
    private List<History> historyList;

    @OneToMany(mappedBy = "simulation")
    @JsonIgnore
    private List<CeremonySession> sessions;

    public Simulation(Long id, Scenario scenario, User createdBy, String difficultyLevel, Date startDate, Date endDate, String status, Double averageScore) {
        this.id = id;
        this.scenario = scenario;
        this.createdBy = createdBy;
        this.difficultyLevel = difficultyLevel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.averageScore = averageScore;
    }

    public Simulation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }


    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public List<SimulationMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<SimulationMetric> metrics) {
        this.metrics = metrics;
    }

    public List<ImprovementPlan> getImprovementPlans() {
        return improvementPlans;
    }

    public void setImprovementPlans(List<ImprovementPlan> improvementPlans) {
        this.improvementPlans = improvementPlans;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    public List<CeremonySession> getSessions() {
        return sessions;
    }

    public void setSessions(List<CeremonySession> sessions) {
        this.sessions = sessions;
    }
}
