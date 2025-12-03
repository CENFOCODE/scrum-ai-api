package com.project.demo.logic.planning;

import jakarta.persistence.*;

@Entity
@Table(name = "planning")
public class Planning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long simulationId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String planningText;


    public Planning() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    public String getPlanningText() {
        return planningText;
    }
    public void setPlanningText(String planningText) {
        this.planningText = planningText;
    }

}
