package com.example.myprojecttcz.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String id;
    private List<String> members; // רשימה של מספרי ID של המשתמשים בשיחה
    private ArrayList<Message> messages;
    private boolean isForum; // האם זה פורום כללי או צ'אט פרטי
    private String title; // שם הפורום (אם רלוונטי)

    // בנאי ריק חובה עבור Firebase
    public Chat() {
        this.members = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public Chat(String id, List<String> members, boolean isForum, String title) {
        this.id = id;
        this.members = members;
        this.isForum = isForum;
        this.title = title;
        this.messages = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public ArrayList<Message> getMessages() { return messages; }
    public void setMessages(ArrayList<Message> messages) { this.messages = messages; }

    public boolean isForum() { return isForum; }
    public void setForum(boolean forum) { isForum = forum; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}