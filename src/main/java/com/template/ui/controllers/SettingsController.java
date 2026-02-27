package com.template.ui.controllers;

import com.template.service.SettingsService;
import com.template.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.List;

public class SettingsController {

    @FXML private Spinner<Double> thresholdSpinner;
    @FXML private ListView<String> programmeListView;

    private final SettingsService settingsService = new SettingsService();
    private final ObservableList<String> programmes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Spinner for threshold 0.0 to 4.0
        double saved = settingsService.getAtRiskThreshold();
        SpinnerValueFactory<Double> vf =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 4.0, saved, 0.1);
        thresholdSpinner.setValueFactory(vf);
        thresholdSpinner.setEditable(true);

        // Load programme list
        refreshProgrammes();

        // Custom list cell to show programme row + Edit/Delete buttons (like screenshot)
        programmeListView.setItems(programmes);
        programmeListView.setCellFactory(lv -> new ListCell<>() {

            private final Label nameLabel = new Label();
            private final Pane spacer = new Pane();

            private final Button editBtn = new Button("✎  Edit");
            private final Button deleteBtn = new Button("🗑  Delete");

            private final HBox row = new HBox(12, nameLabel, spacer, editBtn, deleteBtn);

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);

                row.getStyleClass().add("programme-row");
                nameLabel.getStyleClass().add("programme-name");

                editBtn.getStyleClass().add("prog-edit-btn");
                deleteBtn.getStyleClass().add("prog-delete-btn");

                editBtn.setOnAction(e -> {
                    String oldName = getItem();
                    if (oldName == null) return;

                    TextInputDialog dialog = new TextInputDialog(oldName);
                    dialog.setHeaderText("Edit Programme");
                    dialog.setContentText("Programme name:");

                    dialog.showAndWait().ifPresent(newName -> {
                        try {
                            settingsService.updateProgramme(oldName, newName);
                            refreshProgrammes();
                        } catch (Exception ex) {
                            showError(ex.getMessage());
                        }
                    });
                });

                deleteBtn.setOnAction(e -> {
                    String name = getItem();
                    if (name == null) return;

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete programme: " + name + " ?",
                            ButtonType.CANCEL, ButtonType.OK);

                    confirm.setHeaderText("Confirm Delete");

                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            settingsService.deleteProgramme(name);
                            refreshProgrammes();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(item);
                    setGraphic(row);
                }
            }
        });
    }

    @FXML
    private void saveThreshold(ActionEvent event) {
        try {
            double v = thresholdSpinner.getValue();
            settingsService.setAtRiskThreshold(v);

            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "At‑Risk GPA threshold saved successfully.");
            a.setHeaderText("Saved");
            a.showAndWait();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void addProgramme(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Add New Programme");
        dialog.setContentText("Programme name:");

        dialog.showAndWait().ifPresent(name -> {
            try {
                settingsService.addProgramme(name);
                refreshProgrammes();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        ViewNavigator.switchTo(event, "/dashboard.fxml", "Dashboard");
    }

    private void refreshProgrammes() {
        List<String> list = settingsService.getProgrammes();
        programmes.setAll(list);
        programmeListView.setPlaceholder(new Label("No programmes yet. Click “Add New Programme”."));
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText("Error");
        a.showAndWait();
    }
}