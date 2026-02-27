package com.template.ui.controllers;

import com.template.service.ImportExportService;
import com.template.service.ImportResult;
import com.template.util.ViewNavigator;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

public class ImportExportController {

    @FXML private Button exportFullBtn;
    @FXML private Button exportTopBtn;
    @FXML private Button exportRiskBtn;
    @FXML private Label exportStatusLabel;
    @FXML private ProgressIndicator exportProgress;

    @FXML private StackPane dropZone;
    @FXML private Label selectedFileLabel;
    @FXML private Button importBtn;
    @FXML private Label importStatusLabel;
    @FXML private ProgressIndicator importProgress;
    @FXML private Hyperlink viewErrorReportLink;

    private final ImportExportService service = new ImportExportService();

    private File selectedCsvFile;
    private Path lastErrorReport;

    @FXML
    public void initialize() {
        exportStatusLabel.setText("");
        importStatusLabel.setText("");

        exportProgress.setVisible(false);
        importProgress.setVisible(false);

        selectedFileLabel.setText("No file selected");
        importBtn.setDisable(true);

        viewErrorReportLink.setDisable(true);

        setupDragAndDrop();
    }

    private void setupDragAndDrop() {

        dropZone.setOnDragEntered(e -> dropZone.getStyleClass().add("drop-zone-active"));
        dropZone.setOnDragExited(e -> dropZone.getStyleClass().remove("drop-zone-active"));

        dropZone.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                if (!files.isEmpty() && files.get(0).getName().toLowerCase().endsWith(".csv")) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles() && !db.getFiles().isEmpty()) {
                File f = db.getFiles().get(0);
                if (f.getName().toLowerCase().endsWith(".csv")) {
                    setSelectedFile(f);
                    success = true;
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    // Click drop zone to pick file
    @FXML
    private void chooseCsvFile(MouseEvent event) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Select CSV File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File file = fc.showOpenDialog(dropZone.getScene().getWindow());
        if (file != null) {
            setSelectedFile(file);
        }
    }

    private void setSelectedFile(File file) {
        selectedCsvFile = file;
        selectedFileLabel.setText("Selected: " + file.getName());
        importBtn.setDisable(false);

        importStatusLabel.setText("");
        lastErrorReport = null;
        viewErrorReportLink.setDisable(true);
    }

    // =========================
    // EXPORT BUTTONS
    // =========================
    @FXML
    private void exportFullStudentList(ActionEvent event) {
        runExportTask("Exporting full student list...",
                service::exportFullStudentListCsv);
    }

    @FXML
    private void exportTopPerformers(ActionEvent event) {
        runExportTask("Exporting top performers report...",
                service::exportTopPerformersCsv);
    }

    @FXML
    private void exportAtRiskReport(ActionEvent event) {
        runExportTask("Exporting at risk report...",
                service::exportAtRiskReportCsv);
    }

    private void runExportTask(String runningMessage, Callable<Path> job) {

        setExportBusy(true);
        exportStatusLabel.setText(runningMessage);

        Task<Path> task = new Task<>() {
            @Override
            protected Path call() throws Exception {
                return job.call();
            }
        };

        task.setOnSucceeded(e -> {
            setExportBusy(false);
            Path out = task.getValue();
            exportStatusLabel.setText("Saved to: " + out.toString());
            showInfo("Export Complete", "File saved to:\n" + out.toString());
        });

        task.setOnFailed(e -> {
            setExportBusy(false);
            exportStatusLabel.setText("");
            showError("Export failed: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void setExportBusy(boolean busy) {
        exportFullBtn.setDisable(busy);
        exportTopBtn.setDisable(busy);
        exportRiskBtn.setDisable(busy);
        exportProgress.setVisible(busy);
    }

    // =========================
    // IMPORT BUTTON
    // =========================
    @FXML
    private void importStudentRecords(ActionEvent event) {

        if (selectedCsvFile == null) {
            showError("Please select a CSV file first.");
            return;
        }

        setImportBusy(true);
        importStatusLabel.setText("Importing...");

        Task<ImportResult> task = new Task<>() {
            @Override
            protected ImportResult call() {
                return service.importStudentsFromCsv(selectedCsvFile.toPath());
            }
        };

        task.setOnSucceeded(e -> {
            setImportBusy(false);
            ImportResult r = task.getValue();

            importStatusLabel.setText(
                    "Imported: " + r.getImportedCount()
                            + " | Skipped: " + r.getSkippedCount()
                            + " | Duplicates: " + r.getDuplicateCount()
            );

            if (r.getErrorReportPath() != null) {
                lastErrorReport = r.getErrorReportPath();
                viewErrorReportLink.setDisable(false);
            } else {
                lastErrorReport = null;
                viewErrorReportLink.setDisable(true);
            }

            showInfo("Import Finished",
                    "Imported: " + r.getImportedCount()
                            + "\nSkipped: " + r.getSkippedCount()
                            + "\nDuplicates: " + r.getDuplicateCount()
                            + (r.getErrorReportPath() != null ? ("\n\nError report saved to:\n" + r.getErrorReportPath()) : "")
            );
        });

        task.setOnFailed(e -> {
            setImportBusy(false);
            importStatusLabel.setText("");
            showError("Import failed: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    private void setImportBusy(boolean busy) {
        importBtn.setDisable(busy);
        importProgress.setVisible(busy);
    }

    // =========================
    // VIEW ERROR REPORT
    // =========================
    @FXML
    private void openErrorReport(ActionEvent event) {

        if (lastErrorReport == null) {
            showError("No error report available yet.");
            return;
        }

        try {
            File f = lastErrorReport.toFile();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(f);
            } else {
                showInfo("Error report saved", "Saved at:\n" + f.getAbsolutePath());
            }

        } catch (Exception ex) {
            showInfo("Error report saved", "Could not auto-open.\nSaved at:\n" + lastErrorReport.toAbsolutePath());
        }
    }

    // =========================
    // BACK TO DASHBOARD
    // =========================
    @FXML
    private void goDashboard(ActionEvent event) {
        ViewNavigator.switchTo(event, "/dashboard.fxml", "Dashboard");
    }

    // =========================
    // Alerts
    // =========================
    private void showInfo(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(header);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}