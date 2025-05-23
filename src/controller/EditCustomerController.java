/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: EditCustomerController.java
 * Purpose: Allows editing of existing customer information including name, email,
 *          supplements, payment method, payer (for associates), and contact person
 *          (for enterprise customers). Also supports deleting customers from the system.
 */

package controller;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import model.*;
import service.MagazineService;
import util.InputValidator;

public class EditCustomerController {

    // Common fields
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ListView<CheckBox> supplementList;

    // Payment method fields
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cardNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField bsbField;
    @FXML private VBox debitFields;
    @FXML private VBox creditCardFields;

    // Customer type specific sections
    @FXML private VBox associateBox;
    @FXML private VBox payingBox;
    @FXML private ComboBox<String> payerCombo;
    @FXML private VBox enterpriseBox;
    @FXML private TextField contactNameField;
    @FXML private TextField contactEmailField;

    private Customer customer;
    private TreeItem<String> customerItem;

    /**
     * Initializes the payment method options and toggles fields on load.
     */
    @FXML
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Credit Card", "Direct Debit"));
        paymentMethodCombo.setOnAction(e -> togglePaymentFields());
    }

    /**
     * Configures the UI with the current customer data.
     */
    public void setCustomer(Customer customer, TreeItem<String> customerItem) {
        this.customer = customer;
        this.customerItem = customerItem;

        // Set common fields
        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());

        // Populate supplements and mark selected
        supplementList.getItems().clear();
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.getName());
            cb.setSelected(customer.getSupplements().stream()
                .anyMatch(existing -> existing.getName().equals(s.getName())));
            supplementList.getItems().add(cb);
        }

        // Hide all customer-type specific sections by default
        associateBox.setVisible(false); associateBox.setManaged(false);
        payingBox.setVisible(false); payingBox.setManaged(false);
        enterpriseBox.setVisible(false); enterpriseBox.setManaged(false);

        // AssociateCustomer: load payer list
        if (customer instanceof AssociateCustomer ac) {
            associateBox.setVisible(true); associateBox.setManaged(true);
            List<String> payers = MagazineService.getCustomers().stream()
                    .filter(c -> c instanceof PayingCustomer)
                    .map(Customer::getName)
                    .toList();
            payerCombo.setItems(FXCollections.observableArrayList(payers));
            if (ac.getPayer() != null) {
                payerCombo.getSelectionModel().select(ac.getPayer().getName());
            }
        }

        // PayingCustomer: load payment details
        if (customer instanceof PayingCustomer pc) {
            payingBox.setVisible(true); payingBox.setManaged(true);
            PaymentMethod method = pc.getPaymentMethod();
            if (method == null) {
                paymentMethodCombo.setValue(null);
                showAlert("Missing Payment Method", "This customer has no payment method set.");
                togglePaymentFields();
                return;
            }

            CreditCard creditCard = method.getCard();
            DirectDebit directDebit = method.getDebit();

            if (creditCard != null) {
                paymentMethodCombo.setValue("Credit Card");
                cardNumberField.setText(creditCard.getCardNumber());
                expiryField.setText(creditCard.getExpiryDate());
                cardNameField.setText(creditCard.getCardHolderName());
            } else if (directDebit != null) {
                paymentMethodCombo.setValue("Direct Debit");
                accountNumberField.setText(String.valueOf(directDebit.getAccountNumber()));
                bsbField.setText(String.valueOf(directDebit.getBsb()));
            } else {
                paymentMethodCombo.setValue(null);
                showAlert("Unknown Payment Method", "The payment method exists but is unrecognized.");
            }

            togglePaymentFields();
        }

        // EnterpriseCustomer: load contact person details
        if (customer instanceof EnterpriseCustomer ec) {
            enterpriseBox.setVisible(true); enterpriseBox.setManaged(true);
            EnterpriseCustomer.ContactPerson contact = ec.getContact();
            if (contact != null) {
                contactNameField.setText(contact.getContactName());
                contactEmailField.setText(contact.getContactEmail());
            } else {
                contactNameField.clear();
                contactEmailField.clear();
            }
        }
    }

    /**
     * Shows or hides payment input fields based on selected method.
     */
    private void togglePaymentFields() {
        boolean isCard = "Credit Card".equals(paymentMethodCombo.getValue());
        creditCardFields.setVisible(isCard); creditCardFields.setManaged(isCard);
        debitFields.setVisible(!isCard); debitFields.setManaged(!isCard);
    }

    /**
     * Handles save button logic: updates the customer object and UI.
     */
    @FXML
    private void handleSave() {
        try {
            // Set name and email
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());

            // Update supplements
            List<Supplement> selected = supplementList.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(cb -> MagazineService.findSupplementByName(cb.getText()))
                .filter(s -> s != null).toList();
            customer.setSupplements(selected);

            // Handle AssociateCustomer payer update
            if (customer instanceof AssociateCustomer ac) {
                String payerName = payerCombo.getValue();
                Customer payer = MagazineService.findCustomerByName(payerName);
                if (payer instanceof PayingCustomer pc) {
                    PayingCustomer oldPayer = ac.getPayer();
                    if (oldPayer != null) oldPayer.removeAssociate(ac);
                    ac.setPayer(pc);
                    pc.addAssociate(ac);
                    MagazineController.getInstance().updateDetailView(pc);
                } else {
                    showAlert("Invalid Payer", "Selected payer is not a paying customer.");
                    return;
                }
            }

            // Update payment method for PayingCustomer
            if (customer instanceof PayingCustomer pc) {
                String selectedMethod = paymentMethodCombo.getValue();
                if ("Credit Card".equals(selectedMethod)) {
                    String cardNum = cardNumberField.getText().trim();
                    String expiry = expiryField.getText().trim();
                    String holder = cardNameField.getText().trim();

                    if (!InputValidator.isValidCreditCard(cardNum, expiry, holder)) {
                        showAlert("Invalid Credit Card", "Card number must be 16 digits, expiry in MM/YY format, and name must not be empty.");
                        return;
                    }

                    pc.setPaymentMethod(new PaymentMethod(new CreditCard(cardNum, expiry, holder)));

                } else if ("Direct Debit".equals(selectedMethod)) {
                    String accStr = accountNumberField.getText().trim();
                    String bsbStr = bsbField.getText().trim();

                    if (!InputValidator.isValidDirectDebit(accStr, bsbStr)) {
                        showAlert("Invalid Direct Debit", "Account number must be exactly 8 digits, and BSB must be exactly 6 digits.");
                        return;
                    }

                    int acc = Integer.parseInt(accStr);
                    int bsb = Integer.parseInt(bsbStr);
                    pc.setPaymentMethod(new PaymentMethod(new DirectDebit(acc, bsb)));
                } else {
                    showAlert("Missing Payment Method", "Please select Credit Card or Direct Debit.");
                    return;
                }
            }

            // Update contact info for EnterpriseCustomer
            if (customer instanceof EnterpriseCustomer ec) {
                ec.setContact(new EnterpriseCustomer.ContactPerson(
                        contactNameField.getText(),
                        contactEmailField.getText()
                ));
            }

            // Update the TreeItem label in TreeView
            if (customerItem != null) {
                customerItem.setValue(customer.getName());
            }

            // Refresh the UI tree and close the window
            MagazineController.getInstance().refreshTreeView();
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Account number and BSB must be numeric.");
        } catch (Exception e) {
            showAlert("Unexpected Error", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the delete button action, removing the customer from the system after confirmation.
     */
    public void handleDelete() {
        String name = customer.toString();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete (this cannot be undone)?",
            ButtonType.YES, ButtonType.CANCEL);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                MagazineService.removeCustomer(name);
            }
            ((Stage) nameField.getScene().getWindow()).close();
        });
    }

    /**
     * Displays a warning alert to the user.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Alert
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
