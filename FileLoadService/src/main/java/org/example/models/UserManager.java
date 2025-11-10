package org.example.models;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private Map<String, User> users = new ConcurrentHashMap<>(); // username -> User
    private Map<String, String> tokens = new ConcurrentHashMap<>(); // token -> username

    public User register(String username, String password) {
        if (users.containsKey(username)) {
            throw new RuntimeException("Пользователь уже существует");
        }

        User user = new User(username, password);
        users.put(username, user);
        return user;
    }

    public String login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
//            String token = UUID.randomUUID().toString();
            String token = user.getId();
            tokens.put(token, username);
            return token;
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        return tokens.get(token);
    }

    public void logout(String token) {
        tokens.remove(token);
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
