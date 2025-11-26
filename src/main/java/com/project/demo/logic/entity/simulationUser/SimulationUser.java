package com.project.demo.logic.entity.simulationUser;

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
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "scrum_role", nullable = false)
    private String scrumRole;

    @Column(nullable = false, name = "assigned_at", updatable = false)
    private Date assignedAt;

    public SimulationUser() {
    }

    public SimulationUser(Long id, Date assignedAt, String scrumRole, Simulation simulation, User user) {
        this.id = id;
        this.assignedAt = assignedAt;
        this.scrumRole = scrumRole;
        this.simulation = simulation;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
