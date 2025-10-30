package com.project.demo.logic.entity.ceremonySession;

import jakarta.persistence.*;

@Entity
@Table(name = "ceremony_session")
public class CeremonySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}