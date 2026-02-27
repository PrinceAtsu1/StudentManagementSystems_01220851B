package com.template.util;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        try (Connection conn = DatabaseUtil.connect();
             Statement stmt = conn.createStatement()) {

            // STUDENTS table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students(
                    student_id TEXT PRIMARY KEY,
                    full_name TEXT NOT NULL,
                    programme TEXT NOT NULL,
                    level INTEGER NOT NULL CHECK(level IN (100,200,300,400,500,600,700)),
                    gpa REAL CHECK(gpa >= 0.0 AND gpa <= 4.0),
                    email TEXT,
                    phone TEXT,
                    date_added TEXT,
                    status TEXT NOT NULL CHECK(status IN ('Active','Inactive'))
                );
            """);

            // USERS table (LOGIN/REGISTER)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    email TEXT PRIMARY KEY,
                    full_name TEXT NOT NULL,
                    password_hash TEXT NOT NULL,
                    date_created TEXT NOT NULL
                );
            """);

            // SETTINGS table
            stmt.execute("""
    CREATE TABLE IF NOT EXISTS settings(
        key TEXT PRIMARY KEY,
        value TEXT NOT NULL
    );
""");

// PROGRAMMES table
            stmt.execute("""
    CREATE TABLE IF NOT EXISTS programmes(
        name TEXT PRIMARY KEY NOT NULL
    );
""");

// Default threshold (2.0)
            stmt.execute("INSERT OR IGNORE INTO settings(key, value) VALUES('at_risk_threshold','2.0');");


            System.out.println("✅ students table is ready.");
            System.out.println("✅ users table is ready.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}