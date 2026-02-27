package com.template.ui.controllers;

import com.template.service.AuthService;
import com.template.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberCheck;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        errorLabel.setText("");

        try {
            authService.login(emailField.getText(), passwordField.getText());
            ViewNavigator.switchTo(event, "/dashboard.fxml", "Dashboard");
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        ViewNavigator.switchTo(event, "/register.fxml", "Create Account");
    }

    @FXML
    private void forgotPassword(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Forgot password is not implemented yet.\nPlease create a new account for now.");
        a.setHeaderText("Info");
        a.showAndWait();
    }

    @FXML
    private void googleSignIn(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Google Sign-In is UI-only for now.\nWe can implement it later if needed.");
        a.setHeaderText("Info");
        a.showAndWait();
    }
}