package org.example.models;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String password;
    private LocalDateTime createdAt;

    public User(String username, String password) {
        this.id = java.util.UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
