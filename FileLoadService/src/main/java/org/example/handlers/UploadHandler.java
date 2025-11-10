package org.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.models.FileInfo;
import org.example.models.FileManager;
import org.example.models.UserManager;
import org.example.utils.JsonUtils;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

public class UploadHandler implements HttpHandler {
    private FileManager fileManager;
    private UserManager userManager;
    private String uploadDir = "uploads";

    public UploadHandler(FileManager fileManager, UserManager userManager) {
        this.fileManager = fileManager;
        this.userManager = userManager;
        createUploadDir();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Метод не поддерживается");
            return;
        }

        try {
            String username = getAuthenticatedUser(exchange);
            if (username == null) {
                sendError(exchange, 401, "Требуется авторизация");
                return;
            }

            System.out.println("Пользователь " + username + " загружает файл");
            InputStream is = exchange.getRequestBody();

            String originalName = "file_" + System.currentTimeMillis();
            String uniqueName = UUID.randomUUID() + "_" + originalName;

            Path filePath = Paths.get(uploadDir, uniqueName);
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
            long fileSize = Files.size(filePath);
            System.out.println("Файл сохранен: " + uniqueName + " (" + fileSize + " bytes)");

            FileInfo fileInfo = fileManager.addFile(originalName, uniqueName, username);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "fileInfo", Map.of(
                            "id", fileInfo.getId(),
                            "originalName", fileInfo.getOriginalName(),
                            "fileName", fileInfo.getFileName(),
                            "downloadUrl", "/download/" + fileInfo.getFileName(),
                            "uploadDate", fileInfo.getUploadDate().toString(),
                            "uploadedBy", fileInfo.getUploadedBy()
                    )
            );

            sendResponse(exchange, 200, response);
            System.out.println("Файл загружен: " + originalName);

        } catch (Exception e) {
            sendError(exchange, 500, "Ошибка загрузки файла");
        }
    }
    private String getAuthenticatedUser(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return userManager.getUsernameFromToken(token);
        }
        return null;
    }

    private void createUploadDir() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку uploads", e);
        }
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