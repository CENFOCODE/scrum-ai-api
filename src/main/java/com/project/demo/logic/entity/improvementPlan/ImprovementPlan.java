package com.project.demo.logic.entity.improvementPlan;

import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "improvement_plans")
public class ImprovementPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "related_simulation_id")
    private Simulation relatedSimulation;

    @CreationTimestamp
    @Column(name = "generated_date", nullable = false, updatable = false)
    private Date generatedDate;

    @Column(name = "plan_title")
    private String planTitle;

    @Column(name = "plan_description", columnDefinition = "TEXT", nullable = false)
    private String planDescription;

    @Column(name = "recommended_scenarios", length = 500)
    private String recommendedScenarios;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    public ImprovementPlan() {}

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

    public Simulation getRelatedAimulation() {
        return relatedSimulation;
    }

    public void setRelatedAimulation(Simulation relatedAimulation) {
        this.relatedSimulation = relatedAimulation;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getPlanTitle() {
        return planTitle;
    }

    public void setPlanTitle(String planTitle) {
        this.planTitle = planTitle;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public String getRecommendedScenarios() {
        return recommendedScenarios;
    }

    public void setRecommendedScenarios(String recommendedScenarios) {
        this.recommendedScenarios = recommendedScenarios;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
