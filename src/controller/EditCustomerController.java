    package controller;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;
import model.CreditCard;
import model.DirectDebit;

public class EditCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ListView<CheckBox> supplementList;

    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cardNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField bsbField;
    @FXML private VBox debitFields;
    @FXML private VBox creditCardFields;
    @FXML private VBox associateBox;
    @FXML private VBox payingBox;
    @FXML private ComboBox<String> payerCombo;
    @FXML private VBox enterpriseBox;
    @FXML private TextField contactNameField;
    @FXML private TextField contactEmailField;

    private Customer customer;
    
    
    @FXML
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Credit Card", "Direct Debit"));
        paymentMethodCombo.setOnAction(e -> togglePaymentFields());
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;

        // Set basic fields
        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());

        // Populate supplements with selection
        supplementList.getItems().clear();
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.getName());
            cb.setSelected(customer.getSupplements().stream()
                    .anyMatch(existing -> existing.getName().equals(s.getName())));
            supplementList.getItems().add(cb);
        }

        // Hide all optional sections initially
        associateBox.setVisible(false);
        associateBox.setManaged(false);
        payingBox.setVisible(false);
        payingBox.setManaged(false);
        enterpriseBox.setVisible(false);
        enterpriseBox.setManaged(false);

        // AssociateCustomer 
        if (customer instanceof AssociateCustomer ac) {
            associateBox.setVisible(true);
            associateBox.setManaged(true);

            List<String> payers = MagazineService.getCustomers().stream()
                    .filter(c -> c instanceof PayingCustomer)
                    .map(Customer::getName)
                    .toList();

            payerCombo.setItems(FXCollections.observableArrayList(payers));
            if (ac.getPayer() != null) {
                payerCombo.getSelectionModel().select(ac.getPayer().getName());
            }
        }

        // PayingCustomer (incl. EnterpriseCustomer) 
        if (customer instanceof PayingCustomer pc) {
            payingBox.setVisible(true);
            payingBox.setManaged(true);

            // Downcast methods 
            PaymentMethod method = pc.getPaymentMethod();
            Object actualMethod = method.getMethod();
            CreditCard creditCard = method.getCard();
            DirectDebit directDebit = method.getDebit();
            
            if (creditCard instanceof CreditCard) {
                paymentMethodCombo.setValue("Credit Card");
                cardNumberField.setText(creditCard.getCardNumber());
                expiryField.setText(creditCard.getExpiryDate());
                cardNameField.setText(creditCard.getCardHolderName());

            } else if (directDebit instanceof DirectDebit dd) {
                paymentMethodCombo.setValue("Direct Debit");
                accountNumberField.setText(String.valueOf(dd.getAccountNumber()));
                bsbField.setText(String.valueOf(directDebit.getBsb()));
            } else if (actualMethod instanceof String str) {
                // Known legacy or invalid data fallback
                paymentMethodCombo.setValue(null);
                showAlert("Unsupported Payment Method", "Expected a CreditCard or DirectDebit, but got a raw string: " + str);
            } else {
                paymentMethodCombo.setValue(null);
                showAlert("Unknown Payment Method", "Unrecognized type: " + method.getClass().getSimpleName());
            }
            // Show the correct fields based on type
            togglePaymentFields();
        }

        // EnterpriseCustomer 
        if (customer instanceof EnterpriseCustomer ec) {
            enterpriseBox.setVisible(true);
            enterpriseBox.setManaged(true);

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
    
    private void togglePaymentFields() {
        boolean isCard = "Credit Card".equals(paymentMethodCombo.getValue());

        creditCardFields.setVisible(isCard);
        creditCardFields.setManaged(isCard);

        debitFields.setVisible(!isCard);
        debitFields.setManaged(!isCard);
    }

    @FXML
    private void handleSave() {
        try {
            // Basic fields
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());

            // Update supplements
            List<Supplement> selected = supplementList.getItems().stream()
                .filter(CheckBox::isSelected)
                .map(cb -> MagazineService.findSupplementByName(cb.getText()))
                .filter(s -> s != null)
                .toList();
            customer.setSupplements(selected);

            // Associate: Update payer from combo box
            if (customer instanceof AssociateCustomer ac) {
                String payerName = payerCombo.getValue();
                Customer payer = MagazineService.findCustomerByName(payerName);
                if (payer instanceof PayingCustomer pc) {
                    ac.setPayer(pc);
                } else {
                    showAlert("Invalid Payer", "Selected payer is not a paying customer.");
                    return;
                }
            }

            // PayingCustomer (and Enterprise): Update payment method
            if (customer instanceof PayingCustomer pc) {
                String selectedMethod = paymentMethodCombo.getValue();
                if ("Credit Card".equals(selectedMethod)) {
                    String cardNum = cardNumberField.getText();
                    String expiry = expiryField.getText();
                    String holder = cardNameField.getText();
                    pc.setPaymentMethod(new PaymentMethod(new CreditCard(cardNum, expiry, holder)));
                } else if ("Direct Debit".equals(selectedMethod)) {
                    int acc = Integer.parseInt(accountNumberField.getText());
                    int bsb = Integer.parseInt(bsbField.getText());
                    pc.setPaymentMethod(new PaymentMethod(new DirectDebit(acc, bsb)));
                }
            }

            // EnterpriseCustomer: Update contact person
            if (customer instanceof EnterpriseCustomer ec) {
                String name = contactNameField.getText();
                String email = contactEmailField.getText();
                ec.setContact(new EnterpriseCustomer.ContactPerson(name, email));
            }

            // Close the window
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Account number and BSB must be numeric.");
        } catch (IllegalArgumentException e) {
            showAlert("Input Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Unexpected Error", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleCancel() {
        // Close the current window
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
