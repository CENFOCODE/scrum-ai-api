package com.project.demo.logic.entity.transcript;


import com.project.demo.logic.entity.ceremonySession.CeremonySession;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transcripts")
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ceremony_session_id", nullable = false)
    private CeremonySession ceremonySession;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(nullable = false)
    private String username;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;


    @Column(nullable = false)
    private LocalDateTime timestamp;


    @Column(name = "room_id")
    private String roomId;

    public Transcript() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CeremonySession getCeremonySession() {
        return ceremonySession;
    }

    public void setCeremonySession(CeremonySession ceremonySession) {
        this.ceremonySession = ceremonySession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}