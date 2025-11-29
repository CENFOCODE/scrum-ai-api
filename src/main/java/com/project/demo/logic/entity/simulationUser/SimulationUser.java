package com.project.demo.logic.entity.simulationUser;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.demo.logic.entity.simulation.Simulation;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "simulation_users")
public class SimulationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "simulation_id",nullable = false)
    @JsonBackReference
    private Simulation simulation;


    @Column(name = "scrum_role", nullable = false)
    private String scrumRole;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column(nullable = false, name = "assigned_at", updatable = false)
    private Date assignedAt;

    public SimulationUser(Long id, Date assignedAt, String scrumRole, Simulation simulation, User user) {
        this.id = id;
        this.assignedAt = assignedAt;
        this.scrumRole = scrumRole;
        this.simulation = simulation;
        this.user = user;
    }

    public SimulationUser() {
    }

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

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getScrumRole() {
        return scrumRole;
    }

    public void setScrumRole(String scrumRole) {
        this.scrumRole = scrumRole;
    }

    
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}


