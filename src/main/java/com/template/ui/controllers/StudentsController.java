package com.template.ui.controllers;

import com.template.Domain.Student;
import com.template.service.SettingsService;
import com.template.service.StudentService;
import com.template.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentsController {

    // Filters/Search
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> levelFilter;
    @FXML private ComboBox<String> programmeFilter;
    @FXML private ComboBox<String> statusFilter;

    // Table
    @FXML private TableView<Student> studentTable;

    @FXML private TableColumn<Student, String> idCol;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> programmeCol;
    @FXML private TableColumn<Student, Integer> levelCol;
    @FXML private TableColumn<Student, Double> gpaCol;
    @FXML private TableColumn<Student, String> emailCol;
    @FXML private TableColumn<Student, String> phoneCol;
    @FXML private TableColumn<Student, LocalDate> dateAddedCol;
    @FXML private TableColumn<Student, String> statusCol;
    @FXML private TableColumn<Student, Void> actionsCol;

    private final StudentService service = new StudentService();
    private final SettingsService settingsService = new SettingsService();

    private final ObservableList<Student> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Filters
        levelFilter.getItems().setAll(100, 200, 300, 400);
        statusFilter.getItems().setAll("Active", "Inactive");

        // Programme list from Settings (dynamic)
        programmeFilter.getItems().setAll(settingsService.getProgrammes());

        // Columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        programmeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        dateAddedCol.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // GPA formatting
        gpaCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.1f", item));
            }
        });

        // Date formatting
        dateAddedCol.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : fmt.format(item));
            }
        });

        // Status badge
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                badge.setText(item);
                badge.getStyleClass().setAll("status-badge");
                if ("Active".equalsIgnoreCase(item)) {
                    badge.getStyleClass().add("status-active");
                } else {
                    badge.getStyleClass().add("status-inactive");
                }
                setGraphic(badge);
            }
        });

        // Actions (edit/delete)
        actionsCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("✎");
            private final Button delBtn = new Button("🗑");
            private final HBox box = new HBox(8, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("icon-btn");
                delBtn.getStyleClass().add("icon-btn");
                box.setStyle("-fx-alignment: CENTER;");

                editBtn.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    openEditStudent(s);
                });

                delBtn.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    deleteStudent(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Table behavior
        studentTable.setItems(data);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        studentTable.setPlaceholder(new Label("No student records yet. Click “Add New Student” to begin."));

        // Search enter key
        searchField.setOnAction(e -> applyFilters());

        // Newest first
        dateAddedCol.setSortType(TableColumn.SortType.DESCENDING);
        studentTable.getSortOrder().setAll(dateAddedCol);

        loadAll();
    }

    private void loadAll() {
        data.setAll(service.getAllStudents());
        studentTable.sort();
    }

    @FXML
    private void applyFilters() {

        String keyword = searchField.getText();
        String programme = programmeFilter.getValue();
        Integer level = levelFilter.getValue();
        String status = statusFilter.getValue();

        List<Student> result = service.searchStudents(keyword, programme, level, status);
        data.setAll(result);
        studentTable.sort();
    }

    @FXML
    private void clearFilters() {
        searchField.clear();
        programmeFilter.setValue(null);
        levelFilter.setValue(null);
        statusFilter.setValue(null);
        loadAll();
    }

    @FXML
    private void openAddStudent(ActionEvent event) {
        Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
        openStudentForm(null, owner);
    }

    private void openEditStudent(Student student) {
        Stage owner = (Stage) studentTable.getScene().getWindow();
        openStudentForm(student, owner);
    }

    private void openStudentForm(Student studentToEdit, Stage owner) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student_form.fxml"));
            Parent root = loader.load();

            StudentFormController controller = loader.getController();
            controller.setOnSaved(this::applyFilters);

            if (studentToEdit != null) {
                controller.setStudentToEdit(studentToEdit);
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.setScene(scene);

            // Blur background
            Node ownerRoot = owner.getScene().getRoot();
            ownerRoot.setEffect(new GaussianBlur(10));
            dialog.setOnHidden(e -> ownerRoot.setEffect(null));

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open Add Student form.");
        }
    }

    private void deleteStudent(Student s) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student with ID " + s.getStudentId() + "?",
                ButtonType.CANCEL, ButtonType.OK);

        confirm.setHeaderText("Confirm Delete");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.deleteStudent(s.getStudentId());
                applyFilters();
            }
        });
    }

    // Back button (beside Add New Student)
    @FXML
    private void goDashboard(ActionEvent event) {
        ViewNavigator.switchTo(event, "/dashboard.fxml", "Dashboard");
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}