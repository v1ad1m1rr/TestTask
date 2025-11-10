package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager {
    private Map<String, FileInfo> files = new ConcurrentHashMap<>();

    public FileInfo addFile(String originalName, String fileName) {
        FileInfo fileInfo = new FileInfo(originalName, fileName);
        files.put(fileName, fileInfo);
        return fileInfo;
    }

    public FileInfo getFile(String fileName) {
        return files.get(fileName);
    }

    public void recordDownload(String fileName) {
        FileInfo fileInfo = files.get(fileName);
        if (fileInfo != null) {
            fileInfo.incrementDownload();
        }
    }

    public List<FileInfo> getAllFiles() {
        return new ArrayList<>(files.values());
    }

    public boolean removeFile(String fileName) {
        return files.remove(fileName) != null;
    }

    public List<FileInfo> getOldFiles(int maxAgeDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(maxAgeDays);
        List<FileInfo> oldFiles = new ArrayList<>();

        for (FileInfo file : files.values()) {
            LocalDateTime lastActivity = file.getLastDownload() != null ?
                    file.getLastDownload() : file.getUploadDate();

            if (lastActivity.isBefore(cutoffDate)) {
                oldFiles.add(file);
            }
        }

        return oldFiles;
    }
}
