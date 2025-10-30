package com.project.demo.logic.entity.improvementPlan;

import jakarta.persistence.*;

@Entity
@Table(name = "improvement_plans")
public class ImprovementPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
