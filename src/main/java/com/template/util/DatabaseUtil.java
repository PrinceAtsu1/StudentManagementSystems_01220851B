package com.template.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil {

    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = "students.db";

    public static Connection connect() throws Exception {
        // Make sure the data folder exists
        Files.createDirectories(Path.of(DB_FOLDER));

        String url = "jdbc:sqlite:" + DB_FOLDER + "/" + DB_FILE;
        return DriverManager.getConnection(url);
    }
}