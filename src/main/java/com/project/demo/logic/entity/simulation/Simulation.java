package com.project.demo.logic.entity.simulation;

import com.project.demo.logic.entity.ceremonySession.CeremonySession;
import com.project.demo.logic.entity.feedback.Feedback;
import com.project.demo.logic.entity.history.History;
import com.project.demo.logic.entity.improvementPlan.ImprovementPlan;
import com.project.demo.logic.entity.scenario.Scenario;
import com.project.demo.logic.entity.simulationUser.SimulationUser;
import com.project.demo.logic.entity.simulation_metric.SimulationMetric;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "simulations")
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "difficulty_level", nullable = false)
    private String difficultyLevel;

    @Column(name = "start_date",  nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "active";

    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;

    @OneToMany(mappedBy = "simulation")
    private List<SimulationUser> simulationUserList ;

    @OneToMany(mappedBy = "simulation")
    private List<Feedback> feedbackList;

    @OneToMany(mappedBy = "simulation")
    private List<SimulationMetric> metrics;

    @OneToMany(mappedBy = "simulation")
    private List<ImprovementPlan> improvementPlans;

    @OneToMany(mappedBy = "simulation")
    private List<History> historyList;

    @OneToMany(mappedBy = "simulation")
    private List<CeremonySession> sessions;

    public Simulation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public List<SimulationUser> getSimulationUserList() {
        return simulationUserList;
    }

    public void setSimulationUserList(List<SimulationUser> simulationUserList) {
        this.simulationUserList = simulationUserList;
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
