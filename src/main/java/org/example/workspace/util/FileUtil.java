package org.example.workspace.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class FileUtil {

    private String extractExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private Path createYearMonthDirectory(String basePath) throws IOException {
        String datePath = new SimpleDateFormat("yyyy/MM").format(new Date());
        Path directoryPath = Paths.get(basePath, datePath);
        Files.createDirectories(directoryPath);
        return directoryPath;
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = extractExtension(originalFilename);
        String datePrefix = new SimpleDateFormat("ddHHss").format(new Date());
        String uuid = UUID.randomUUID().toString();
        return datePrefix + "_" + uuid + extension;
    }

    public Path saveFile(String basePath, MultipartFile file) throws IOException {
        Path directoryPath = createYearMonthDirectory(basePath);
        String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());

        Path savePath = directoryPath.resolve(uniqueFilename);

        Files.write(savePath, file.getBytes());

        return savePath;
    }
}
