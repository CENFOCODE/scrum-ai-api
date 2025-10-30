package com.project.demo.logic.entity.achievements;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
