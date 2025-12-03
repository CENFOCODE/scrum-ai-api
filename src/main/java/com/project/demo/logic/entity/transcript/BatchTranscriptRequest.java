package com.project.demo.logic.entity.transcript;

import java.time.LocalDateTime;
import java.util.List;

public class BatchTranscriptRequest {
    private Long ceremonySessionId;
    private String roomId;
    private List<TranscriptEntry> transcripts;

    public Long getCeremonySessionId() {
        return ceremonySessionId;
    }

    public void setCeremonySessionId(Long ceremonySessionId) {
        this.ceremonySessionId = ceremonySessionId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<TranscriptEntry> getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(List<TranscriptEntry> transcripts) {
        this.transcripts = transcripts;
    }

    public static class TranscriptEntry {
        private String username;
        private String text;
        private LocalDateTime timestamp;
        private Long userId;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}