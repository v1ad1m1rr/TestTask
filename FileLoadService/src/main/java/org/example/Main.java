package org.example;

import org.example.server.FileServer;

public class Main {
    public static void main(String[] args) {
        int port = 3000;
        FileServer server = new FileServer(port);
        server.start();

        System.out.println("Сервер запущен на порту " + port);
        System.out.println("Файлы сохраняются в: uploads/");
        System.out.println("Открой: http://localhost:" + port);
    }
}