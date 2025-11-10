package org.example.utils;

import org.example.models.FileInfo;
import org.example.models.FileManager;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FileCleanup {
    private FileManager fileManager;
    private String uploadDir;
    private int maxAgeDays;
    private Timer timer;

    public FileCleanup(FileManager fileManager, String uploadDir, int maxAgeDays) {
        this.fileManager = fileManager;
        this.uploadDir = uploadDir;
        this.maxAgeDays = maxAgeDays;
    }

    public void startCleanup() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanupOldFiles();
            }
        }, 0, 24 * 60 * 60 * 1000);

        System.out.println("Очистка старых файлов запущена");
    }

    public void stopCleanup() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void cleanupOldFiles() {
        try {
            List<FileInfo> oldFiles = fileManager.getOldFiles(maxAgeDays);
            System.out.println("Найдено старых файлов: " + oldFiles.size());

            for (FileInfo file : oldFiles) {
                try {
                    File fileToDelete = Paths.get(uploadDir, file.getFileName()).toFile();
                    if (fileToDelete.exists() && fileToDelete.delete()) {
                        fileManager.removeFile(file.getFileName());
                        System.out.println("Удален старый файл: " + file.getOriginalName());
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка удаления " + file.getFileName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка очистки файлов: " + e.getMessage());
        }
    }
}