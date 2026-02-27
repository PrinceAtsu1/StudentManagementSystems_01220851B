package com.template.ui.controllers;

import com.template.Domain.Student;
import com.template.service.SettingsService;
import com.template.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class StudentFormController {

    @FXML private Label titleLabel;

    @FXML private TextField studentIdField;
    @FXML private TextField fullNameField;

    @FXML private ComboBox<String> programmeCombo;
    @FXML private ComboBox<Integer> levelCombo;
    @FXML private Spinner<Double> gpaSpinner;

    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private DatePicker dateAddedPicker;

    @FXML private RadioButton activeRadio;
    @FXML private RadioButton inactiveRadio;

    @FXML private Label errorLabel;

    private final StudentService service = new StudentService();
    private final SettingsService settingsService = new SettingsService();

    private Student editingStudent = null;
    private Runnable onSaved;

    @FXML
    public void initialize() {

        // Load programmes dynamically
        programmeCombo.getItems().setAll(settingsService.getProgrammes());

        // Level options
        levelCombo.getItems().setAll(100, 200, 300, 400);

        // GPA spinner
        SpinnerValueFactory<Double> vf =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 4.0, 0.0, 0.1);
        gpaSpinner.setValueFactory(vf);
        gpaSpinner.setEditable(true);

        // Status toggle
        ToggleGroup tg = new ToggleGroup();
        activeRadio.setToggleGroup(tg);
        inactiveRadio.setToggleGroup(tg);
        activeRadio.setSelected(true);

        dateAddedPicker.setValue(LocalDate.now());

        // Phone digits only
        phoneField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) return change;
            return null;
        }));
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setStudentToEdit(Student s) {
        this.editingStudent = s;

        titleLabel.setText("Edit Student");

        studentIdField.setText(s.getStudentId());
        studentIdField.setDisable(true);

        fullNameField.setText(s.getFullName());
        programmeCombo.setValue(s.getProgramme());
        levelCombo.setValue(s.getLevel());
        gpaSpinner.getValueFactory().setValue(s.getGpa());

        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone());
        dateAddedPicker.setValue(s.getDateAdded());

        if ("Inactive".equalsIgnoreCase(s.getStatus()))
            inactiveRadio.setSelected(true);
        else
            activeRadio.setSelected(true);
    }

    @FXML
    private void saveStudent() {

        errorLabel.setText("");

        try {
            Student s = new Student();

            s.setStudentId(studentIdField.getText());
            s.setFullName(fullNameField.getText());
            s.setProgramme(programmeCombo.getValue());
            s.setLevel(levelCombo.getValue());
            s.setGpa(gpaSpinner.getValue());
            s.setEmail(emailField.getText());
            s.setPhone(phoneField.getText());
            s.setDateAdded(dateAddedPicker.getValue());
            s.setStatus(activeRadio.isSelected() ? "Active" : "Inactive");

            if (editingStudent == null)
                service.addStudent(s);
            else
                service.updateStudent(s);

            if (onSaved != null) onSaved.run();
            close();

        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}