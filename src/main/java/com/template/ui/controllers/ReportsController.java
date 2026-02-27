package com.template.ui.controllers;

import com.template.Domain.ProgrammeSummaryRow;
import com.template.Domain.Student;
import com.template.Domain.TopPerformerRow;
import com.template.service.ReportService;
import com.template.service.SettingsService;
import com.template.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;

public class ReportsController {

    // Top performers filters
    @FXML private ComboBox<String> tpProgrammeFilter;
    @FXML private ComboBox<String> tpLevelFilter;

    // Top performers table
    @FXML private TableView<TopPerformerRow> topTable;
    @FXML private TableColumn<TopPerformerRow, Integer> rankCol;
    @FXML private TableColumn<TopPerformerRow, String> tpIdCol;
    @FXML private TableColumn<TopPerformerRow, String> tpNameCol;
    @FXML private TableColumn<TopPerformerRow, String> tpProgrammeCol;
    @FXML private TableColumn<TopPerformerRow, Integer> tpLevelCol;
    @FXML private TableColumn<TopPerformerRow, Double> tpGpaCol;

    // Cards
    @FXML private TextField thresholdField;
    @FXML private Label atRiskCountLabel;
    @FXML private Label totalStudentsValue;
    @FXML private Label avgGpaValue;

    @FXML private BarChart<String, Number> gpaChart;

    // Programme summary card table
    @FXML private TableView<ProgrammeSummaryRow> programmeTable;
    @FXML private TableColumn<ProgrammeSummaryRow, String> psProgrammeCol;
    @FXML private TableColumn<ProgrammeSummaryRow, Integer> psTotalCol;
    @FXML private TableColumn<ProgrammeSummaryRow, Double> psAvgCol;

    // At risk tab
    @FXML private TextField atRiskThresholdField;
    @FXML private TableView<Student> atRiskTable;
    @FXML private TableColumn<Student, String> arIdCol;
    @FXML private TableColumn<Student, String> arNameCol;
    @FXML private TableColumn<Student, String> arProgrammeCol;
    @FXML private TableColumn<Student, Integer> arLevelCol;
    @FXML private TableColumn<Student, Double> arGpaCol;

    // Full charts/tables tabs
    @FXML private BarChart<String, Number> gpaChartFull;

    @FXML private TableView<ProgrammeSummaryRow> programmeTableFull;
    @FXML private TableColumn<ProgrammeSummaryRow, String> psfProgrammeCol;
    @FXML private TableColumn<ProgrammeSummaryRow, Integer> psfTotalCol;
    @FXML private TableColumn<ProgrammeSummaryRow, Double> psfAvgCol;

    private final ReportService reportService = new ReportService();

    private double threshold = 2.0;

    private final ObservableList<TopPerformerRow> topData = FXCollections.observableArrayList();
    private final ObservableList<Student> atRiskData = FXCollections.observableArrayList();
    private final ObservableList<ProgrammeSummaryRow> programmeData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        SettingsService settingsService = new SettingsService();

        tpProgrammeFilter.getItems().clear();
        tpProgrammeFilter.getItems().add("All Programmes");
        tpProgrammeFilter.getItems().addAll(settingsService.getProgrammes());
        tpProgrammeFilter.setValue("All Programmes");

// Load threshold from settings
        threshold = settingsService.getAtRiskThreshold();
        thresholdField.setText(String.valueOf(threshold));
        atRiskThresholdField.setText(String.valueOf(threshold));


        tpLevelFilter.getItems().addAll("All Levels", "100", "200", "300", "400");
        tpLevelFilter.setValue("All Levels");

        // Allow threshold fields to accept digits and dot only
        setupThresholdField(thresholdField);
        setupThresholdField(atRiskThresholdField);

        thresholdField.setText("2.0");
        atRiskThresholdField.setText("2.0");

        // Top performers columns
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        tpIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        tpNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tpProgrammeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));
        tpLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        tpGpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        // Format GPA like screenshot (2 decimals)
        tpGpaCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });

        topTable.setItems(topData);
        topTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        topTable.setPlaceholder(new Label("No data yet. Add students first."));

        // Programme summary columns (card + full)
        psProgrammeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));
        psTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));
        psAvgCol.setCellValueFactory(new PropertyValueFactory<>("avgGpa"));

        psfProgrammeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));
        psfTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));
        psfAvgCol.setCellValueFactory(new PropertyValueFactory<>("avgGpa"));

        // Format avg GPA
        psAvgCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });
        psfAvgCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });

        programmeTable.setItems(programmeData);
        programmeTableFull.setItems(programmeData);

        programmeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        programmeTableFull.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // At risk tab columns
        arIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        arNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        arProgrammeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));
        arLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        arGpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        arGpaCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });

        atRiskTable.setItems(atRiskData);
        atRiskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        atRiskTable.setPlaceholder(new Label("No at-risk students for the current threshold."));

        // Events
        tpProgrammeFilter.setOnAction(e -> refreshAll());
        tpLevelFilter.setOnAction(e -> refreshAll());

        thresholdField.setOnAction(e -> applyAtRiskThreshold(null));
        atRiskThresholdField.setOnAction(e -> applyAtRiskThreshold(null));

        // Initial load
        refreshAll();
    }

    private void setupThresholdField(TextField tf) {
        tf.setTextFormatter(new TextFormatter<>(change -> {
            String next = change.getControlNewText();
            if (next.matches("\\d*([.]\\d*)?")) return change;
            return null;
        }));
    }

    private void refreshAll() {

        // Summary values
        List<Student> all = reportService.getAllStudents();
        totalStudentsValue.setText(String.valueOf(all.size()));
        avgGpaValue.setText(String.format("%.2f", reportService.averageGpa(all)));

        // Top performers
        String prog = tpProgrammeFilter.getValue();
        if ("All Programmes".equals(prog)) prog = null;

        String levelStr = tpLevelFilter.getValue();
        Integer level = null;
        if (levelStr != null && !"All Levels".equals(levelStr)) {
            try { level = Integer.parseInt(levelStr); } catch (Exception ignored) {}
        }

        topData.setAll(reportService.getTopPerformers(prog, level, 10));

        // Programme summary tables (card + full)
        programmeData.setAll(reportService.getProgrammeSummary());

        // At risk + charts
        updateAtRiskAndCharts();
    }

    private void updateAtRiskAndCharts() {

        // At risk students
        List<Student> atRisk = reportService.getAtRiskStudents(threshold);
        atRiskData.setAll(atRisk);

        atRiskCountLabel.setText(atRisk.size() + " student(s) below " + String.format("%.1f", threshold));

        // Charts
        Map<String, Long> dist = reportService.getGpaDistribution();

        setChartData(gpaChart, dist);
        setChartData(gpaChartFull, dist);
    }

    private void setChartData(BarChart<String, Number> chart, Map<String, Long> dist) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (String band : dist.keySet()) {
            series.getData().add(new XYChart.Data<>(band, dist.get(band)));
        }

        chart.getData().add(series);
    }

    // Apply threshold from either thresholdField or atRiskThresholdField
    @FXML
    private void applyAtRiskThreshold(ActionEvent event) {

        String t1 = thresholdField.getText();
        String t2 = atRiskThresholdField.getText();

        String chosen = (t2 != null && !t2.isBlank()) ? t2 : t1;

        try {
            double v = Double.parseDouble(chosen);
            if (v < 0 || v > 4.0) throw new IllegalArgumentException();

            threshold = v;

            // keep both fields in sync
            thresholdField.setText(String.valueOf(v));
            atRiskThresholdField.setText(String.valueOf(v));

            updateAtRiskAndCharts();

        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Threshold must be a number between 0.0 and 4.0");
            a.setHeaderText("Invalid Threshold");
            a.showAndWait();

            thresholdField.setText(String.valueOf(threshold));
            atRiskThresholdField.setText(String.valueOf(threshold));
        }
    }



    // Back to Dashboard button
    @FXML
    private void goDashboard(ActionEvent event) {
        ViewNavigator.switchTo(event, "/dashboard.fxml", "Dashboard");
    }
}