package com.template.ui.controllers;

import com.template.service.AuthService;
import com.template.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister(ActionEvent event) {
        errorLabel.setText("");

        try {
            authService.register(
                    fullNameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    confirmPasswordField.getText()
            );
            // After register go straight to dashboard
            ViewNavigator.switchTo(event, "/dashboard.fxml", "Dashboard");
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        ViewNavigator.switchTo(event, "/login.fxml", "Login");
    }
}