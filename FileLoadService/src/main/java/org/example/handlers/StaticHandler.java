package org.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.file.*;

public class StaticHandler implements HttpHandler {
    private String frontendDir = "src/main/resources";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/templates/index.html";
        }

        try {
            Path filePath = Paths.get(frontendDir + path);

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                sendError(exchange, 404, "Файл не найден");
                return;
            }

            String contentType = getContentType(filePath);
            byte[] fileBytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }

        } catch (Exception e) {
            sendError(exchange, 500, "Ошибка загрузки файла");
        }
    }

    private String getContentType(Path filePath) {
        String fileName = filePath.getFileName().toString();
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".json")) return "application/json";
        return "text/plain";
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        byte[] responseBytes = response.getBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
}
