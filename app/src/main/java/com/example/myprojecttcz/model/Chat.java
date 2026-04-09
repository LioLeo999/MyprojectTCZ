package com.example.myprojecttcz.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat {
    private String id;
    private List<String> members;
    private HashMap<String, Message> messages; // שינוי ל-HashMap
    private boolean isForum;
    private String title;
    private long lastMessageTime;

    // בנאי ריק חובה עבור Firebase
    public Chat() {
        this.members = new ArrayList<>();
        this.messages = new HashMap<>();
    }

    public Chat(String id, List<String> members, boolean isForum, String title) {
        this.id = id;
        this.members = members;
        this.isForum = isForum;
        this.title = title;
        this.messages = new HashMap<>();
    }


    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public HashMap<String, Message> getMessages() { return messages; }
    public void setMessages(HashMap<String, Message> messages) { this.messages = messages; }

    public boolean isForum() { return isForum; }
    public void setForum(boolean forum) { isForum = forum; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}