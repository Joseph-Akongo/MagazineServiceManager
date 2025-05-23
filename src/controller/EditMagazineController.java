/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: EditMagazineController.java
 * Purpose: Manages the editing of existing magazine details (name and price) in the Magazine Service system.
 *          Loads current magazine data into the UI and updates the MagazineService when saved.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Magazine;
import service.MagazineService;

public class EditMagazineController {

    @FXML private TextField nameField;  // Text field for editing the magazine's name
    @FXML private TextField priceField; // Text field for editing the magazine's price

    /**
     * Initializes the form by preloading existing magazine data into the input fields.
     */
    @FXML
    public void initialize() {
        Magazine mag = MagazineService.getMagazine();
        if (mag != null) {
            nameField.setText(mag.getName());
            priceField.setText(String.valueOf(mag.getPrice()));
        }
    }

    /**
     * Handles the save button click.
     * Validates and updates the magazine's name and price, and closes the window if successful.
     */
    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        try {
            float price = Float.parseFloat(priceText);

            // Validate input: name must not be empty and price must be positive
            if (name.isEmpty() || price <= 0) throw new IllegalArgumentException();

            // Update the magazine in the service
            MagazineService.setMagazine(new Magazine(name, price));

            // Close the edit window
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            // Show alert if input is invalid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please enter a valid name and positive price.");
            alert.showAndWait();
        }
    }
}
