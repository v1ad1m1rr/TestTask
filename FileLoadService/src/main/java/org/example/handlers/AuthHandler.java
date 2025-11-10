package org.example.handlers;

import org.example.models.UserManager;
import org.example.models.User;
import org.example.utils.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    private UserManager userManager;

    public AuthHandler(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Метод не поддерживается");
            return;
        }

        try {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes());
            Map<String, String> bodyMap = JsonUtils.parseJson(new java.io.ByteArrayInputStream(body.getBytes()), Map.class);

            String action = bodyMap.get("action");
            String username = bodyMap.get("username");
            String password = bodyMap.get("password");

            if (username == null || password == null || action == null) {
                sendError(exchange, 400, "Все поля обязательны");
                return;
            }

            if ("register".equals(action)) {
                handleRegister(exchange, username, password);
            } else if ("login".equals(action)) {
                handleLogin(exchange, username, password);
            } else if ("logout".equals(action)) {
                handleLogout(exchange, bodyMap.get("token"));
            } else {
                sendError(exchange, 400, "Неизвестное действие");
            }

        } catch (RuntimeException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Внутренняя ошибка сервера");
        }
    }

    private void handleRegister(HttpExchange exchange, String username, String password) throws IOException {
        try {
            userManager.register(username, password);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Пользователь зарегистрирован"
            );

            sendResponse(exchange, 200, response);

        } catch (RuntimeException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void handleLogin(HttpExchange exchange, String username, String password) throws IOException {
        String token = userManager.login(username, password);

        if (token != null) {
            Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Вход выполнен",
                    "token", token,
                    "username", username
            );

            sendResponse(exchange, 200, response);
        } else {
            sendError(exchange, 401, "Неверное имя пользователя или пароль");
        }
    }

    private void handleLogout(HttpExchange exchange, String token) throws IOException {
        if (token != null) {
            userManager.logout(token);
        }

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Выход выполнен"
        );

        sendResponse(exchange, 200, response);
    }

    private void sendResponse(HttpExchange exchange, int code, Map<String, Object> response) throws IOException {
        String jsonResponse = JsonUtils.toJson(response);
        byte[] responseBytes = jsonResponse.getBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        Map<String, Object> response = Map.of("error", message);
        String jsonResponse = JsonUtils.toJson(response);
        byte[] responseBytes = jsonResponse.getBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
}
