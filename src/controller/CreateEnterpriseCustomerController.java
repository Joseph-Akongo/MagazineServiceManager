/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: CreateEnterpriseCustomerController.java
 * Purpose: Manages the creation of Enterprise Customers in the Magazine Service system.
 *          Handles input validation, contact person details, payment method selection,
 *          supplement choices, and customer registration logic.
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

public class CreateEnterpriseCustomerController {

    // FXML fields for user input
    @FXML private TextField nameField;                  
    @FXML private TextField emailField;                 
    @FXML private ComboBox<String> paymentMethodBox;    
    @FXML private VBox creditCardFields;                
    @FXML private TextField cardNumberField;           
    @FXML private TextField expiryField;                
    @FXML private TextField holderNameField;           
    @FXML private VBox debitFields;                     
    @FXML private TextField bsbField;                   
    @FXML private TextField accountNumberField;         
    @FXML private TextField contactNameField;           
    @FXML private TextField contactEmailField;          
    @FXML private Spinner<Integer> copiesSpinner;       
    @FXML private VBox supplementsBox;                  

    private final List<CheckBox> supplementCheckboxes = new ArrayList<>(); 

    // Initializes the controller Populates payment method options, supplement list.
    @FXML
    public void initialize() {
        // Populate payment method options
        paymentMethodBox.getItems().addAll("Credit Card", "Direct Debit");
        paymentMethodBox.setValue("Credit Card");
        paymentMethodBox.setOnAction(e -> togglePaymentFields()); // React to selection change

        togglePaymentFields(); // Set initial visibility of payment fields

        // Configure spinner for number of copies (1–100, default 1)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        copiesSpinner.setValueFactory(valueFactory);

        // Populate supplements with checkboxes
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.toString());
            cb.setUserData(s); // Store Supplement object
            supplementCheckboxes.add(cb);
            supplementsBox.getChildren().add(cb);
        }
    }

    // Toggle visibility based on selected payment method.    
    private void togglePaymentFields() {
        boolean isCard = "Credit Card".equals(paymentMethodBox.getValue());

        creditCardFields.setVisible(isCard);
        creditCardFields.setManaged(isCard);

        debitFields.setVisible(!isCard);
        debitFields.setManaged(!isCard);
    }

    // Button click event. Validates input, creates a new EnterpriseCustomer, and adds it to the system.
    @FXML
    private void handleCreate() {
        try {
            // Collect and trim basic info
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String contactName = contactNameField.getText().trim();
            String contactEmail = contactEmailField.getText().trim();
            int copies = copiesSpinner.getValue();

            // Validate required fields
            InputValidator.isValidName(name);
            InputValidator.isValidEmail(email);

            // Determine payment method and validate accordingly
            PaymentMethod method;
            String methodType = paymentMethodBox.getValue();

            if ("Credit Card".equals(methodType)) {
                String cardNum = cardNumberField.getText().trim();
                String expiry = expiryField.getText().trim();
                String holder = holderNameField.getText().trim();

                if (!InputValidator.isValidCreditCard(cardNum, expiry, holder)) {
                    showAlert("Invalid Credit Card", "Ensure card number is 16 digits, expiry is MM/YY, and name is filled.");
                    return;
                }

                CreditCard card = new CreditCard(cardNum, expiry, holder);
                method = new PaymentMethod(card);

            } else if ("Direct Debit".equals(methodType)) {
                String accStr = accountNumberField.getText().trim();
                String bsbStr = bsbField.getText().trim();

                if (!InputValidator.isValidDirectDebit(accStr, bsbStr)) {
                    showAlert("Invalid Direct Debit", "Account must be 6–9 digits. BSB must be 6 digits.");
                    return;
                }

                int acc = Integer.parseInt(accStr);
                int bsb = Integer.parseInt(bsbStr);
                method = new PaymentMethod(new DirectDebit(acc, bsb));

            } else {
                showAlert("Missing Payment Method", "Please select Credit Card or Direct Debit.");
                return;
            }

            // Create contact person object
            EnterpriseCustomer.ContactPerson cp = new EnterpriseCustomer.ContactPerson(contactName, contactEmail);

            // Create the new enterprise customer
            EnterpriseCustomer ec = new EnterpriseCustomer(name, email, method, cp, copies);

            // Attach selected supplements
            for (CheckBox cb : supplementCheckboxes) {
                if (cb.isSelected()) {
                    ec.addSupplement((Supplement) cb.getUserData());
                }
            }

            // Debugging output
            System.out.println("Selected method: " + paymentMethodBox.getValue());
            System.out.println("Method before customer creation: " + method);

            // Add customer to system and close the window
            MagazineService.addCustomer(ec);
            closeWindow();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    // Alert
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Close
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
