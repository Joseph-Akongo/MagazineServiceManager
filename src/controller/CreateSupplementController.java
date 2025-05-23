/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: CreateSupplementController.java
 * Purpose: Manages the creation of new Supplement entries for the Magazine Service system.
 *          Handles user input validation, object creation, and updates the service with new data.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Supplement;
import service.MagazineService;

public class CreateSupplementController {

    // FXML UI elements for input fields
    @FXML private TextField nameField; // Input for supplement name
    @FXML private TextField costField; // Input for supplement weekly cost

    /**
     * Triggered when the user clicks the "Save" button.
     * Validates inputs, creates a new Supplement object, and adds it to the MagazineService.
     */
    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String costText = costField.getText().trim();

        // Check that both fields are filled
        if (name.isEmpty() || costText.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        try {
            // Parse cost and check that it is non-negative
            double cost = Double.parseDouble(costText);
            if (cost < 0) throw new NumberFormatException();

            // Add the new supplement to the system
            MagazineService.getAvailableSupplements().add(new Supplement(name, cost));

            // Show success and close the window
            showAlert("Success", "Supplement created.");
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert("Error", "Cost must be a positive number.");
        }
    }

    /**
     * Triggered when the user clicks the "Cancel" button.
     * Simply closes the current window without saving.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Closes the window that this controller is managing.
     */
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    // Alert
    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.INFORMATION, content).showAndWait();
    }
}
