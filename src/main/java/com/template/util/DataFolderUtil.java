package com.template.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataFolderUtil {

    private DataFolderUtil() {}

    public static Path dataDir() {
        try {
            Path dir = Paths.get(System.getProperty("user.dir"), "data");
            Files.createDirectories(dir);
            return dir;
        } catch (Exception e) {
            throw new RuntimeException("Could not create/access 'data' folder.");
        }
    }

    public static Path newCsvFile(String prefix) {
        String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        return dataDir().resolve(prefix + "_" + ts + ".csv");
    }

    // safe component for filename (e.g. 2.0 -> 2_0)
    public static String safe(double value) {
        return String.valueOf(value).replace(".", "_");
    }
}