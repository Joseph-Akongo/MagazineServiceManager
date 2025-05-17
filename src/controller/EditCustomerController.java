package controller;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;

public class EditCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ListView<CheckBox> supplementList;
    @FXML private Button cancelButton;

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

    public void setCustomer(Customer customer) {
        this.customer = customer;

        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());

        // Load supplements
        supplementList.getItems().clear();
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.getName());
            cb.setSelected(customer.getSupplements().stream()
                    .anyMatch(existing -> existing.getName().equals(s.getName())));
            supplementList.getItems().add(cb);
        }

        // AssociateCustomer: show payer options
        if (customer instanceof AssociateCustomer ac) {
            associateBox.setVisible(true);
            associateBox.setManaged(true);

            List<String> payers = MagazineService.getCustomers().stream()
                    .filter(c -> c instanceof PayingCustomer)
                    .map(Customer::getName)
                    .toList();

            payerCombo.setItems(FXCollections.observableArrayList(payers));
            payerCombo.getSelectionModel().select(ac.getPayer().getName());
        }

        // PayingCustomer (includes EnterpriseCustomer)
        if (customer instanceof PayingCustomer pc) {
            payingBox.setVisible(true);
            payingBox.setManaged(true);

            PaymentMethod method = pc.getPaymentMethod();
            if (method != null && method.getMethod() != null) {
                Object actualMethod = method.getMethod();

                if (actualMethod instanceof CreditCard) {
                    CreditCard cc = (CreditCard) actualMethod;
                    paymentMethodCombo.setValue("Credit Card");
                    cardNumberField.setText(cc.getCardNumber());
                    expiryField.setText(cc.getExpiryDate());
                    cardNameField.setText(cc.getCardHolderName());

                } else if (actualMethod instanceof DirectDebit) {
                    DirectDebit dd = (DirectDebit) actualMethod;
                    paymentMethodCombo.setValue("Direct Debit");
                    accountNumberField.setText(String.valueOf(dd.getAccountNumber()));
                    bsbField.setText(String.valueOf(dd.getBsb()));
                } else {
                    // Handle unexpected method types gracefully
                    showAlert("Invalid Payment Method", "Unrecognized payment method type.");
                    paymentMethodCombo.setValue(null);
                }

                togglePaymentFields(); // Update UI based on selection
            }

        // EnterpriseCustomer: show contact details
        if (customer instanceof EnterpriseCustomer ec) {
            enterpriseBox.setVisible(true);
            enterpriseBox.setManaged(true);
                if (ec.getContact() != null) {
                    String name = contactNameField.getText();
                    String email = contactEmailField.getText();
                    ec.setContact(new EnterpriseCustomer.ContactPerson(name, email));
                } else {
                    contactNameField.clear();
                    contactEmailField.clear();
                    showAlert("No Contact Info", "Enterprise contact person not available.");
                }
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
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Credit Card", "Direct Debit"));

        paymentMethodCombo.valueProperty().addListener((obs, old, selected) -> {
            boolean isCard = "Credit Card".equals(selected);
            cardNumberField.setDisable(!isCard);
            expiryField.setDisable(!isCard);
            cardNameField.setDisable(!isCard);

            accountNumberField.setDisable(isCard);
            bsbField.setDisable(isCard);
        });
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

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String promptInput(String title, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait().orElse(null);
    }

    private String promptChoice(String title, String message, String... options) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(options[0], options);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait().orElse(null);
    }
    
    private void showCreditFields() {
        creditCardFields.setVisible(true);
        creditCardFields.setManaged(true);
        debitFields.setVisible(false);
        debitFields.setManaged(false);
    }

    private void showDebitFields() {
        creditCardFields.setVisible(false);
        creditCardFields.setManaged(false);
        debitFields.setVisible(true);
        debitFields.setManaged(true);
    }

    private void hidePaymentFields() {
        creditCardFields.setVisible(false);
        creditCardFields.setManaged(false);
        debitFields.setVisible(false);
        debitFields.setManaged(false);
    }

}
