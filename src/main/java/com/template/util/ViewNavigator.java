package com.template.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewNavigator {

    public static void switchTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(ViewNavigator.class.getResource(fxmlPath));

            // Get current scene from the clicked button
            Scene scene = ((Node) event.getSource()).getScene();

            // Switch root (same window, same scene)
            scene.setRoot(root);

            // Update window title
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle(title);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}