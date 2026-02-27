package com.template.service;

import com.template.Domain.Student;
import com.template.util.CsvUtil;
import com.template.util.DataFolderUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class ImportExportService {

    private final StudentService studentService = new StudentService();
    private final SettingsService settingsService = new SettingsService();

    // =========================
    // EXPORTS
    // =========================
    public Path exportFullStudentListCsv() {
        List<Student> students = studentService.getAllStudents();
        Path out = DataFolderUtil.newCsvFile("students_full");

        writeStudentsCsv(out, students);
        return out;
    }

    public Path exportTopPerformersCsv() {
        List<Student> all = studentService.getAllStudents();

        all.sort(Comparator
                .comparingDouble(Student::getGpa).reversed()
                .thenComparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER));

        List<Student> top = all.stream().limit(10).toList();

        Path out = DataFolderUtil.newCsvFile("top_performers");

        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            CsvUtil.writeRow(bw, List.of("rank", "student_id", "full_name", "programme", "level", "gpa"));

            int rank = 1;
            for (Student s : top) {
                CsvUtil.writeRow(bw, List.of(
                        String.valueOf(rank++),
                        s.getStudentId(),
                        s.getFullName(),
                        s.getProgramme(),
                        String.valueOf(s.getLevel()),
                        String.valueOf(s.getGpa())
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to export top performers CSV.");
        }

        return out;
    }

    public Path exportAtRiskReportCsv() {
        double threshold = settingsService.getAtRiskThreshold();

        List<Student> all = studentService.getAllStudents();
        List<Student> atRisk = all.stream()
                .filter(s -> s.getGpa() < threshold)
                .sorted(Comparator.comparingDouble(Student::getGpa))
                .toList();

        Path out = DataFolderUtil.newCsvFile("at_risk_threshold_" + DataFolderUtil.safe(threshold));

        writeStudentsCsv(out, atRisk);
        return out;
    }

    private void writeStudentsCsv(Path out, List<Student> students) {
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {

            CsvUtil.writeRow(bw, List.of(
                    "student_id", "full_name", "programme", "level", "gpa",
                    "email", "phone", "date_added", "status"
            ));

            for (Student s : students) {
                CsvUtil.writeRow(bw, List.of(
                        safe(s.getStudentId()),
                        safe(s.getFullName()),
                        safe(s.getProgramme()),
                        String.valueOf(s.getLevel()),
                        String.valueOf(s.getGpa()),
                        safe(s.getEmail()),
                        safe(s.getPhone()),
                        s.getDateAdded() == null ? "" : s.getDateAdded().toString(),
                        safe(s.getStatus())
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV.");
        }
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    // =========================
    // IMPORT
    // =========================
    public ImportResult importStudentsFromCsv(Path csvPath) {

        int imported = 0;
        int skipped = 0;
        int duplicates = 0;
        int totalRows = 0;

        List<String[]> errors = new ArrayList<>();

        // existing IDs for duplicate checks
        Set<String> existingIds = new HashSet<>();
        for (Student s : studentService.getAllStudents()) {
            existingIds.add(s.getStudentId());
        }

        try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                List<String> cols = CsvUtil.parseLine(line);

                // header skip
                if (firstLine) {
                    firstLine = false;
                    if (looksLikeHeader(cols)) {
                        continue;
                    }
                }

                totalRows++;

                // Expect 8 or 9 columns
                if (cols.size() < 8) {
                    skipped++;
                    errors.add(new String[]{String.valueOf(totalRows), "", "Not enough columns", line});
                    continue;
                }

                String studentId = get(cols, 0);
                String fullName  = get(cols, 1);
                String programme = get(cols, 2);
                String levelStr  = get(cols, 3);
                String gpaStr    = get(cols, 4);
                String email     = get(cols, 5);
                String phone     = get(cols, 6);
                String dateStr   = get(cols, 7);
                String status    = cols.size() >= 9 ? get(cols, 8) : "";

                // validate
                String error = validateRow(studentId, fullName, programme, levelStr, gpaStr, email, phone, dateStr, status);

                // duplicate check
                if (error == null && existingIds.contains(studentId)) {
                    duplicates++;
                    skipped++;
                    errors.add(new String[]{String.valueOf(totalRows), studentId, "Duplicate student ID", line});
                    continue;
                }

                if (error != null) {
                    skipped++;
                    errors.add(new String[]{String.valueOf(totalRows), studentId, error, line});
                    continue;
                }

                // build Student
                Student s = new Student();
                s.setStudentId(studentId);
                s.setFullName(fullName);
                s.setProgramme(programme);
                s.setLevel(Integer.parseInt(levelStr));
                s.setGpa(Double.parseDouble(gpaStr));
                s.setEmail(email);
                s.setPhone(phone);

                LocalDate dateAdded = dateStr.isBlank() ? LocalDate.now() : LocalDate.parse(dateStr);
                s.setDateAdded(dateAdded);

                String normalizedStatus = status.isBlank() ? "Active" : normalizeStatus(status);
                s.setStatus(normalizedStatus);

                try {
                    studentService.addStudent(s);
                    imported++;
                    existingIds.add(studentId);
                } catch (Exception ex) {
                    skipped++;
                    errors.add(new String[]{String.valueOf(totalRows), studentId, ex.getMessage(), line});
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file.");
        }

        Path errorReport = null;
        if (!errors.isEmpty()) {
            errorReport = writeErrorReport(errors);
        }

        return new ImportResult(totalRows, imported, skipped, duplicates, errorReport);
    }

    private Path writeErrorReport(List<String[]> errors) {

        Path out = DataFolderUtil.newCsvFile("import_error_report");

        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            CsvUtil.writeRow(bw, List.of("row_number", "student_id", "error_message", "raw_row"));

            for (String[] e : errors) {
                CsvUtil.writeRow(bw, List.of(e[0], e[1], e[2], e[3]));
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to write import error report.");
        }

        return out;
    }

    private boolean looksLikeHeader(List<String> cols) {
        if (cols.isEmpty()) return false;
        String first = cols.get(0).toLowerCase();
        return first.contains("student") || first.contains("id");
    }

    private String get(List<String> cols, int i) {
        if (i >= cols.size()) return "";
        return cols.get(i) == null ? "" : cols.get(i).trim();
    }

    private String normalizeStatus(String s) {
        String x = s.trim().toLowerCase();
        if (x.equals("active")) return "Active";
        if (x.equals("inactive")) return "Inactive";
        return s.trim();
    }

    // Validation rules (your requirement)
    private String validateRow(String studentId, String fullName, String programme,
                               String levelStr, String gpaStr, String email,
                               String phone, String dateStr, String status) {

        // Student ID
        if (studentId == null || studentId.isBlank()) return "Student ID is required";
        if (studentId.length() < 4 || studentId.length() > 20) return "Student ID must be 4 to 20 characters";
        if (!studentId.matches("[A-Za-z0-9]+")) return "Student ID must contain letters and digits only";

        // Full name
        if (fullName == null || fullName.isBlank()) return "Full name is required";
        if (fullName.length() < 2 || fullName.length() > 60) return "Full name must be 2 to 60 characters";
        if (fullName.matches(".*\\d.*")) return "Full name must not contain digits";

        // Programme
        if (programme == null || programme.isBlank()) return "Programme is required";

        // Level (100–400)
        int level;
        try {
            level = Integer.parseInt(levelStr);
        } catch (Exception e) {
            return "Level must be a number (100, 200, 300, 400)";
        }
        if (!(level == 100 || level == 200 || level == 300 || level == 400)) {
            return "Level must be one of: 100, 200, 300, 400";
        }

        // GPA
        double gpa;
        try {
            gpa = Double.parseDouble(gpaStr);
        } catch (Exception e) {
            return "GPA must be a number between 0.0 and 4.0";
        }
        if (gpa < 0.0 || gpa > 4.0) return "GPA must be between 0.0 and 4.0";

        // Email
        if (email == null || email.isBlank()) return "Email is required";
        if (!email.contains("@") || !email.contains(".")) return "Email must contain an @ sign and a dot";

        // Phone (10 digits)
        if (phone == null || phone.isBlank()) return "Phone number is required";
        if (!phone.matches("\\d{10}")) return "Phone number must be 10 digits (digits only)";

        // Date (optional but if provided must parse yyyy-MM-dd)
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                LocalDate.parse(dateStr);
            } catch (Exception e) {
                return "Date added must be in format yyyy-MM-dd (example: 2026-02-23)";
            }
        }

        // Status (optional)
        if (status != null && !status.isBlank()) {
            String x = status.trim().toLowerCase();
            if (!x.equals("active") && !x.equals("inactive")) {
                return "Status must be Active or Inactive";
            }
        }

        return null;
    }
}