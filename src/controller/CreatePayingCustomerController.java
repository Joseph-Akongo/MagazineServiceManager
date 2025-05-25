/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: CreatePayingCustomerController.java
 * Purpose: Handles the creation of PayingCustomer objects in the Magazine Service system.
 *          This includes capturing user input, validating details, selecting a payment method,
 *          choosing supplements, and saving the customer to the system.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;

import java.util.ArrayList;
import java.util.List;
import util.InputValidator;

public class CreatePayingCustomerController {

    // User input fields
    @FXML private TextField nameField; 
    @FXML private TextField emailField; 
    @FXML private ComboBox<String> paymentMethodBox; 

    // Credit Card fields
    @FXML private VBox creditCardFields;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField holderNameField;

    // Direct Debit fields
    @FXML private VBox debitFields;
    @FXML private TextField bsbField;
    @FXML private TextField accountNumberField;

    @FXML private VBox supplementsBox; // Container for dynamically loaded supplement checkboxes

    private List<CheckBox> supplementCheckboxes = new ArrayList<>(); // Track selected supplements

    // Sets up the payment method options and supplement checkboxes.
    @FXML
    public void initialize() {
        // Populate the payment method combo box
        paymentMethodBox.getItems().addAll("Credit Card", "Direct Debit");
        paymentMethodBox.setValue("Credit Card"); // Default selection

        // Set the handler to show/hide fields based on selection
        paymentMethodBox.setOnAction(e -> togglePaymentFields());
        togglePaymentFields(); // Initialize visibility

        // Load available supplements into the VBox with checkboxes
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.getName() + " ($" + s.getWeeklyCost() + "/week)");
            cb.setUserData(s); // Store supplement object
            supplementCheckboxes.add(cb);
            supplementsBox.getChildren().add(cb);
        }
    }

    // Shows or hides input fields based on the selected payment method.
    private void togglePaymentFields() {
        boolean isCard = "Credit Card".equals(paymentMethodBox.getValue());

        creditCardFields.setVisible(isCard);
        creditCardFields.setManaged(isCard);

        debitFields.setVisible(!isCard);
        debitFields.setManaged(!isCard);
    }

     // Validates input, builds the PayingCustomer object, and adds them to the system.
    @FXML
    private void handleCreate() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        // Validate name and email format
        InputValidator.isValidName(name);
        InputValidator.isValidEmail(email);

        PaymentMethod method;
        String selectedMethod = paymentMethodBox.getValue();

        // Handle Credit Card input
        if ("Credit Card".equals(selectedMethod)) {
            String cardNum = cardNumberField.getText().trim();
            String expiry = expiryField.getText().trim();
            String holder = holderNameField.getText().trim();

            if (!InputValidator.isValidCreditCard(cardNum, expiry, holder)) {
                showAlert("Invalid Credit Card", "Ensure card number is 16 digits, expiry is MM/YY, and name is filled.");
                return;
            }

            CreditCard card = new CreditCard(cardNum, expiry, holder);
            method = new PaymentMethod(card);

        // Handle Direct Debit input
        } else if ("Direct Debit".equals(selectedMethod)) {
            String accStr = accountNumberField.getText().trim();
            String bsbStr = bsbField.getText().trim();

            // Debug output
            System.out.println("accStr: '" + accStr + "', bsbStr: '" + bsbStr + "'");
            System.out.println("accStr valid? " + accStr.matches("\\d{8}"));
            System.out.println("bsbStr valid? " + bsbStr.matches("\\d{6}"));

            // Validate account and BSB formats
            if (!InputValidator.isValidDirectDebit(accStr, bsbStr)) {
                showAlert("Invalid Direct Debit", "Account number must be exactly 8 digits, and BSB must be exactly 6 digits.");
                return;
            }

            int acc = Integer.parseInt(accStr);
            int bsb = Integer.parseInt(bsbStr);
            method = new PaymentMethod(new DirectDebit(acc, bsb));

        } else {
            showAlert("Missing Payment Method", "Please select either Credit Card or Direct Debit.");
            return;
        }

        // Create the PayingCustomer
        PayingCustomer customer = new PayingCustomer(name, email, method);

        // Add selected supplements
        for (CheckBox cb : supplementCheckboxes) {
            if (cb.isSelected()) {
                customer.addSupplement((Supplement) cb.getUserData());
            }
        }

        // Add customer to the service
        MagazineService.addCustomer(customer);

        // Close the creation window
        closeWindow();
    }

   // Close window
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    // Alert
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
