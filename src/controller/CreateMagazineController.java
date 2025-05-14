/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.MagazineService;
import model.*;
import util.InputValidator;

/**
 *
 * @author Joseph
 */
public class CreateMagazineController {
    @FXML private TextField magazineName;
    @FXML private TextField magazinePrice;
    @FXML private TextField nameField;

    @FXML
    private void handleCreate() {
        try {
            String name = magazineName.getText().trim();
            String price = magazinePrice.getText().trim();

            if (name.isEmpty() || price.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields.");
                return;
            }

            float fPrice;
            try {
                fPrice = Float.parseFloat(price);
                if (fPrice < 0) {
                    showAlert("Input Error", "Price must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Invalid price format.");
                return;
            }

            if (!InputValidator.isValidName(name)) {
                showAlert("Input Error", "Invalid magazine name.");
                return;
            }

            // Create Magazine
            Magazine magazine = new Magazine(name, fPrice);
            MagazineService.addMagazine(magazine);
            
            File file = handleSave(name);

            if (file == null) {
                showAlert("Cancelled", "Save operation was cancelled. Magazine not saved.");
                return; // Don’t proceed if user cancelled
            }

            // OPTIONAL: Save the magazine object to file (you can replace this with your own format)
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(magazine); // Ensure Magazine implements Serializable
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Save Error", "Failed to save magazine to file.");
                return;
            }

            // Proceed to main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Magazine Service Manager");
            stage.show();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error occurred while creating the magazine.");
        }
    }
    
    private File handleSave(String name) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        fileChooser.setInitialFileName(name.replaceAll("\\s+", "_") + ".dat");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        // Show dialog and return file
        return fileChooser.showSaveDialog(magazineName.getScene().getWindow());
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) magazineName.getScene().getWindow(); // or any node from your scene
        stage.close();
    }

}
