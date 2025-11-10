package org.example.server;

import com.sun.net.httpserver.HttpServer;
import org.example.handlers.AuthHandler;
import org.example.handlers.FileHandler;
import org.example.handlers.StaticHandler;
import org.example.handlers.UploadHandler;
import org.example.models.FileManager;
import org.example.models.UserManager;
import org.example.utils.FileCleanup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class FileServer {
    private HttpServer server;
    private int port;
    private FileManager fileManager;
    private UserManager userManager;
    private FileCleanup fileCleanup;

    public FileServer(int port) {
        this.port = port;
        this.fileManager = new FileManager();
        this.userManager = new UserManager();
        this.fileCleanup = new FileCleanup(fileManager, "uploads", 30);
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/", new StaticHandler());
            server.createContext("/api/auth", new AuthHandler(userManager));
            server.createContext("/api/upload", new UploadHandler(fileManager, userManager));
            server.createContext("/api/stats", new FileHandler(fileManager, userManager));
            server.createContext("/download/", new FileHandler(fileManager, userManager));

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            fileCleanup.startCleanup();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить сервер", e);
        }
    }

    public void stop() {
        server.stop(0);
        fileCleanup.stopCleanup();
    }
}
