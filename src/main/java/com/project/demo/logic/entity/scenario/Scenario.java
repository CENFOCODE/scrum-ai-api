package com.project.demo.logic.entity.scenario;

import jakarta.persistence.*;

@Entity
@Table(name = "Scenarios")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    private String description;

    @Column(name = "ceremony_type", nullable = false)
    private String ceremonyType;

}
