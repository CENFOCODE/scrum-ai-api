package com.project.demo.logic.entity.scenario_template;

import jakarta.persistence.*;

@Entity
@Table(name = "scenarios_templates")
public class ScenarioTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
