/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: EditSupplementController.java
 * Purpose: Provides functionality to edit or delete an existing Supplement object.
 *          Supports updating the supplement's name and cost, and handles confirmation
 *          before deletion from the system.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Supplement;
import service.MagazineService;

public class EditSupplementController {

    @FXML private TextField nameField; // Input field for supplement name
    @FXML private TextField costField; // Input field for supplement cost

    private Supplement supplement; // Reference to the supplement being edited

    /**
     * Sets the supplement to be edited and populates the form fields with its current data.
     * 
     * @param s the supplement object to edit
     */
    public void setSupplement(Supplement s) {
        this.supplement = s;
        nameField.setText(s.getName());
        costField.setText(String.valueOf(s.getWeeklyCost()));
    }

    /**
     * Handles saving the updated supplement information.
     * Updates the name and weekly cost, then closes the window.
     */
    @FXML
    private void handleSave() {
        supplement.setName(nameField.getText().trim());
        supplement.setWeeklyCost(Double.parseDouble(costField.getText().trim()));
        ((Stage) nameField.getScene().getWindow()).close();
    }

    /**
     * Handles deleting the supplement from the system.
     * Prompts the user for confirmation before removal.
     */
    @FXML
    private void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Supplement");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This will remove the supplement from all customers.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                MagazineService.removeSupplement(supplement);
                ((Stage) nameField.getScene().getWindow()).close();
            }
        });
    }
}
