package com.project.demo.logic.entity.ceremonySession;

import com.project.demo.logic.entity.simulation.Simulation;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "ceremony_session")
public class CeremonySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;

    @Column(name = "ceremony_type", nullable = false, length = 50)
    private String ceremonyType;

    @CreationTimestamp
    @Column(name = "start_time", updatable = false)
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(columnDefinition = "TEXT")
    private String summary;

    public CeremonySession() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public String getCeremonyType() {
        return ceremonyType;
    }

    public void setCeremonyType(String ceremonyType) {
        this.ceremonyType = ceremonyType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}