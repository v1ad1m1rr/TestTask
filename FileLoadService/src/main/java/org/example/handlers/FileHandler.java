package org.example.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.models.FileInfo;
import org.example.models.FileManager;
import org.example.utils.JsonUtils;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileHandler implements HttpHandler {
    private FileManager fileManager;
    private String uploadDir = "uploads";

    public FileHandler(FileManager fileManager) {
        this.fileManager = fileManager;
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

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/api/stats")) {
            handleStats(exchange);
        } else if (path.startsWith("/download/")) {
            handleDownload(exchange);
        } else {
            sendError(exchange, 404, "Не найдено");
        }
    }

    public void handleStats(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Метод не поддерживается");
            return;
        }

        try {
            List<FileInfo> files = fileManager.getAllFiles();

            Map<String, Object> stats = Map.of(
                    "totalFiles", files.size(),
                    "totalDownloads", files.stream().mapToInt(FileInfo::getDownloadCount).sum(),
                    "files", files.stream().map(file -> Map.of(
                            "originalName", file.getOriginalName(),
                            "uploadDate", file.getUploadDate().toString(),
                            "lastDownload", file.getLastDownload() != null ?
                                    file.getLastDownload().toString() : "никогда",
                            "downloadCount", file.getDownloadCount()
                    )).collect(Collectors.toList())
            );

            sendResponse(exchange, 200, stats);

        } catch (Exception e) {
            sendError(exchange, 500, "Ошибка получения статистики");
        }
    }

    private void handleDownload(HttpExchange exchange) throws IOException {
        try {
            String fileName = exchange.getRequestURI().getPath().substring("/download/".length());
            FileInfo fileInfo = fileManager.getFile(fileName);

            if (fileInfo == null) {
                sendError(exchange, 404, "Файл не найден");
                return;
            }

            Path filePath = Paths.get(uploadDir, fileName);
            if (!Files.exists(filePath)) {
                sendError(exchange, 404, "Файл не найден на сервере");
                return;
            }

            fileManager.recordDownload(fileName);
            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            exchange.getResponseHeaders().set("Content-Disposition",
                    "attachment; filename=\"" + fileInfo.getOriginalName() + "\"");
            exchange.sendResponseHeaders(200, Files.size(filePath));

            try (OutputStream os = exchange.getResponseBody();
                 InputStream is = Files.newInputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Файл скачан: " + fileInfo.getOriginalName());

        } catch (Exception e) {
            sendError(exchange, 500, "Ошибка скачивания файла");
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