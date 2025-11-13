package com.project.demo.logic.dto.ceremony;

public class CeremonyInviteRequest {

    private String senderName;     // nombre del usuario que invita
    private String recipientEmail; // correo del invitado
    private String ceremonyType;   // tipo de ceremonia
    private String difficulty;     // dificultad seleccionada
    private String roomLink;       // enlace generado (local o túnel)

    public CeremonyInviteRequest() {}

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getCeremonyType() {
        return ceremonyType;
    }

    public void setCeremonyType(String ceremonyType) {
        this.ceremonyType = ceremonyType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getRoomLink() {
        return roomLink;
    }

    public void setRoomLink(String roomLink) {
        this.roomLink = roomLink;
    }
}
