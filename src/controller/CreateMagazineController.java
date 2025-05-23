/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: CreateMagazineController.java
 * Purpose: Handles user input for creating a new Magazine instance.
 *          Validates inputs, sets up the MagazineService, prompts the user to save to file,
 *          and opens the main Magazine Service Manager UI upon successful creation.
 */

package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Magazine;
import service.MagazineService;
import util.FileHelper;

import java.io.File;

public class CreateMagazineController {

    @FXML private TextField magazineName;  
    @FXML private TextField magazinePrice; 

    // Create Button, creates the magazine, saves to file, and launches the main app view.
    @FXML
    private void handleCreate() {
        try {
            String name = magazineName.getText().trim();
            String price = magazinePrice.getText().trim();

            // Ensure both fields are filled
            if (name.isEmpty() || price.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields.");
                return;
            }

            float fPrice;
            try {
                // Try converting price to float and check it's non-negative
                fPrice = Float.parseFloat(price);
                if (fPrice < 0) {
                    showAlert("Input Error", "Price must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Invalid price format.");
                return;
            }

            // Create Magazine object and register it with the service
            Magazine magazine = new Magazine(name, fPrice);
            MagazineService.setMagazine(magazine);

            // Prompt user to save magazine to a .dat file
            FileChooser fileChooser = FileHelper.getDatFileChooser("Save Magazine File", true);
            fileChooser.setInitialFileName(name.replaceAll("\\s+", "_") + ".dat");
            File file = fileChooser.showSaveDialog(magazineName.getScene().getWindow());

            // Handle cancellation
            if (file == null) {
                showAlert("Cancelled", "Save operation was cancelled.");
                return;
            }

            // Attempt to save the magazine to the chosen file
            if (!FileHelper.saveMagazineToFile(file)) {
                showAlert("Save Error", "Failed to save magazine to file.");
                return;
            }

            // Load the main application UI after successful creation and saving
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Magazine Service Manager");
            stage.show();

            // Close window
            ((Stage) magazineName.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error occurred while creating the magazine.");
        }
    }

    // Alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
