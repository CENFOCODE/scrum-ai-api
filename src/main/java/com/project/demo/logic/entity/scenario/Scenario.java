package com.project.demo.logic.entity.scenario;

import com.project.demo.logic.entity.scenarioTemplate.ScenarioTemplate;
import com.project.demo.logic.entity.simulation.Simulation;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Scenarios")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Scenario(Long id, String name, String description, int estimatedDuration, String backlog, String team, List<Simulation> simulations, List<ScenarioTemplate> templates, String goals, String difficultyLevel, String ceremonyType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.backlog = backlog;
        this.team = team;
        this.simulations = simulations;
        this.templates = templates;
        this.goals = goals;
        this.difficultyLevel = difficultyLevel;
        this.ceremonyType = ceremonyType;
    }

    @Column(length = 100, nullable = false)
    private String name;

    private String description;

    @Column(name = "ceremony_type", nullable = false, length = 50)
    private String ceremonyType;

    @Column(name = "difficulty_level", nullable = false, length = 20)
    private String difficultyLevel;

    @Column(name = "estimated_duration")
    private int estimatedDuration;

    @Column(length = 2000)
    private String backlog;

    @Column(length = 1000)
    private String goals;

    @Column(length = 1000)
    private String team;

    @OneToMany(mappedBy = "scenario")
    private List<Simulation> simulations;

    @OneToMany(mappedBy = "scenario")
    private List<ScenarioTemplate> templates;

    public Scenario() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCeremonyType() {
        return ceremonyType;
    }

    public void setCeremonyType(String ceremonyType) {
        this.ceremonyType = ceremonyType;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public String getBacklog() {
        return backlog;
    }

    public void setBacklog(String backlog) {
        this.backlog = backlog;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public List<Simulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public List<ScenarioTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ScenarioTemplate> templates) {
        this.templates = templates;
    }
}
