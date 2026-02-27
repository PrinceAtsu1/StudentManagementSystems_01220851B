package com.template.service;

import com.template.Domain.Student;
import com.template.repository.StudentRepository;
import com.template.repository.sqlite.SQLiteStudentRepository;

import java.util.List;

public class StudentService {

    private final StudentRepository repository = new SQLiteStudentRepository();

    public void addStudent(Student s) {
        validate(s);

        if (repository.existsById(s.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists. Use a different ID.");
        }

        repository.save(s);
    }

    public void updateStudent(Student s) {
        validate(s);
        repository.update(s);
    }

    public void deleteStudent(String studentId) {
        repository.delete(studentId);
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public List<Student> searchStudents(String keyword, String programme, Integer level, String status) {
        return repository.search(keyword, programme, level, status);
    }

    // Validation rules (exactly from your message)
    private void validate(Student s) {

        // Student ID: required, 4–20, letters/digits only
        if (s.getStudentId() == null || s.getStudentId().isBlank()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (!s.getStudentId().matches("[A-Za-z0-9]{4,20}")) {
            throw new IllegalArgumentException("Student ID must be 4–20 characters (letters and digits only).");
        }

        // Full name: required, 2–60, must not contain digits
        if (s.getFullName() == null || s.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (s.getFullName().length() < 2 || s.getFullName().length() > 60) {
            throw new IllegalArgumentException("Full name must be 2–60 characters.");
        }
        if (s.getFullName().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Full name must not contain digits.");
        }

        // Programme required
        if (s.getProgramme() == null || s.getProgramme().isBlank()) {
            throw new IllegalArgumentException("Programme is required.");
        }

        // Level must be 100/200/300/400
        if (!(s.getLevel() == 100 || s.getLevel() == 200 || s.getLevel() == 300 || s.getLevel() == 400)) {
            throw new IllegalArgumentException("Level must be one of: 100, 200, 300, 400.");
        }

        // GPA range
        if (s.getGpa() < 0.0 || s.getGpa() > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0.");
        }

        // Email must contain @ and .
        if (s.getEmail() == null || s.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!s.getEmail().contains("@") || !s.getEmail().contains(".")) {
            throw new IllegalArgumentException("Email must contain an @ sign and a dot.");
        }

        // Phone must be 10 digits only
        if (s.getPhone() == null || s.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (!s.getPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits (digits only).");
        }
    }
}