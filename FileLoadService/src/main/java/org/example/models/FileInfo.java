package org.example.models;

import java.time.LocalDateTime;

public class FileInfo {
    private String id;
    private String originalName;
    private String fileName;
    private LocalDateTime uploadDate;
    private LocalDateTime lastDownload;
    private int downloadCount;

    public FileInfo(String originalName, String fileName) {
        this.id = java.util.UUID.randomUUID().toString();
        this.originalName = originalName;
        this.fileName = fileName;
        this.uploadDate = LocalDateTime.now();
        this.downloadCount = 0;
    }

    public String getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getFileName() { return fileName; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public LocalDateTime getLastDownload() { return lastDownload; }
    public int getDownloadCount() { return downloadCount; }

    public void incrementDownload() {
        this.downloadCount++;
        this.lastDownload = LocalDateTime.now();
    }
}