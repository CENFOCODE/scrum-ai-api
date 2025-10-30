package com.project.demo.logic.entity.simulation_metric;

import jakarta.persistence.*;

@Entity
@Table(name = "simulation_metrics")
public class SimulationMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
