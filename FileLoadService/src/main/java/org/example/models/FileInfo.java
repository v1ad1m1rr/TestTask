package org.example.models;

import java.time.LocalDateTime;

public class FileInfo {
    private String id;
    private String originalName;
    private String fileName;
    private LocalDateTime uploadDate;
    private LocalDateTime lastDownload;
    private int downloadCount;
    private String uploadedBy;

    public FileInfo(String originalName, String fileName, String uploadedBy) {
        this.id = java.util.UUID.randomUUID().toString();
        this.originalName = originalName;
        this.fileName = fileName;
        this.uploadDate = LocalDateTime.now();
        this.downloadCount = 0;
        this.uploadedBy = uploadedBy;
    }

    public String getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getFileName() { return fileName; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public LocalDateTime getLastDownload() { return lastDownload; }
    public int getDownloadCount() { return downloadCount; }
    public String getUploadedBy() {
        return uploadedBy;
    }

    public void incrementDownload() {
        this.downloadCount++;
        this.lastDownload = LocalDateTime.now();
    }
}