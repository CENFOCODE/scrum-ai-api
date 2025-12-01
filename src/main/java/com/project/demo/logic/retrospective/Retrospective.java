package com.project.demo.logic.retrospective;

import jakarta.persistence.*;

@Entity
@Table(name = "retrospective")
public class Retrospective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long simulationId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String retrospectiveJson;


    public Retrospective() {}

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

    public String getRetrospectiveJson() {
        return retrospectiveJson;
    }

    public void setRetrospectiveJson(String retrospectiveJson) {
        this.retrospectiveJson = retrospectiveJson;
    }

}
