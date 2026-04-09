package com.example.myprojecttcz.model;

public class Message {
    private String id;
    private String senderId; // ה-ID של מי ששלח
    private String receiverId; // ה-ID של המקבל (או ה-ID של הפורום)
    private String content; // תוכן ההודעה
    private long timestamp; // זמן שליחת ההודעה

    // בנאי ריק עבור Firebase
    public Message() {
    }

    public Message(String id, String senderId, String receiverId, String content, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}