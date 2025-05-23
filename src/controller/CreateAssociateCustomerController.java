/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: CreateAssociateCustomerController.java
 * Purpose: Controls the interface for creating an Associate Customer in the Magazine Service system.
 *          It collects name, email, payer, and selected supplements, performs validation, and adds the
 *          new associate to the system.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;
import util.InputValidator;
import java.util.ArrayList;
import java.util.List;

public class CreateAssociateCustomerController {

    @FXML private TextField nameField; 
    @FXML private TextField emailField; 
    @FXML private ComboBox<String> payerComboBox; 
    @FXML private VBox supplementsBox; 

    private final List<CheckBox> supplementCheckboxes = new ArrayList<>(); // List to track dynamically created checkboxes

    // Called automatically when the FXML view is loaded. Initializes the list of payers and supplement checkboxes.
    @FXML
    public void initialize() {
        // Populate combo box with names of paying customers
        for (Customer c : MagazineService.getCustomers()) {
            if (c instanceof PayingCustomer) {
                payerComboBox.getItems().add(c.getName());
            }
        }

        // Select the first payer by default, if available
        if (!payerComboBox.getItems().isEmpty()) {
            payerComboBox.setValue(payerComboBox.getItems().get(0));
        }

        // Dynamically create a checkbox for each available supplement
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.toString());
            cb.setUserData(s); // Store supplement object for later retrieval
            supplementCheckboxes.add(cb);
            supplementsBox.getChildren().add(cb); // Add checkbox to the VBox
        }
    }

    // Creation of a new AssociateCustomer, validates input, links associate to a payer, adds supplements, and saves the customer.
    @FXML
    private void handleCreate() {
        String name = nameField.getText();
        String email = emailField.getText();
        String payerName = payerComboBox.getValue();

        // Validate user input
        InputValidator.isValidName(name);
        InputValidator.isValidEmail(email);

        // Find the selected paying customer
        PayingCustomer payer = (PayingCustomer) MagazineService.findCustomerByName(payerName);
        if (payer == null) {
            showAlert("Not Found", "Selected payer could not be found.");
            return;
        }

        // Create new associate customer and link to payer
        AssociateCustomer customer = new AssociateCustomer(name, email, payer);
        payer.addAssociate(customer);

        // Add selected supplements to the new associate
        for (CheckBox cb : supplementCheckboxes) {
            if (cb.isSelected()) {
                customer.addSupplement((Supplement) cb.getUserData());
            }
        }

        // Add associate customer to system
        MagazineService.addCustomer(customer);

        // Update the detail view to show the new associate under the payer
        MagazineController.getInstance().updateDetailView(payer);

        // Close the creation window
        closeWindow();
    }

    // Alert
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    // Closes the current window after creation is complete.
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
