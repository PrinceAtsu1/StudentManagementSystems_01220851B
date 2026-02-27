package com.template.ui.controllers;

import com.template.Domain.Student;
import com.template.service.StudentService;
import com.template.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class DashboardController {

    @FXML private Label totalStudentsLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private Label inactiveStudentsLabel;
    @FXML private Label avgGpaLabel;

    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        refreshDashboardStats();
    }

    private void refreshDashboardStats() {
        try {
            List<Student> students = studentService.getAllStudents();

            int total = students.size();

            long active = students.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus().equalsIgnoreCase("Active"))
                    .count();

            long inactive = students.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus().equalsIgnoreCase("Inactive"))
                    .count();

            double avg = students.stream()
                    .mapToDouble(Student::getGpa)
                    .average()
                    .orElse(0.0);

            totalStudentsLabel.setText(String.valueOf(total));
            activeStudentsLabel.setText(String.valueOf(active));
            inactiveStudentsLabel.setText(String.valueOf(inactive));
            avgGpaLabel.setText(String.format("%.2f", avg));

        } catch (Exception e) {
            // If anything fails, keep safe default values
            totalStudentsLabel.setText("0");
            activeStudentsLabel.setText("0");
            inactiveStudentsLabel.setText("0");
            avgGpaLabel.setText("0.00");
        }
    }

    // ===== Navigation buttons (cards) =====
    @FXML
    private void openStudents(ActionEvent event) {
        ViewNavigator.switchTo(event, "/students.fxml", "Students");
    }

    @FXML
    private void openReports(ActionEvent event) {
        ViewNavigator.switchTo(event, "/reports.fxml", "Reports");
    }

    @FXML
    private void openImportExport(ActionEvent event) {
        ViewNavigator.switchTo(event, "/import_export.fxml", "Import & Export");
    }

    @FXML
    private void openSettings(ActionEvent event) {
        ViewNavigator.switchTo(event, "/settings.fxml", "Settings");
    }

    @FXML
    private void logout(ActionEvent event) {
        ViewNavigator.switchTo(event, "/login.fxml", "Login");
    }
}