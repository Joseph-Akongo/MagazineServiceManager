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
import model.CreditCard;
import model.DirectDebit;
import util.InputValidator;

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
    private TreeItem<String> customerItem;
    
    @FXML
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Credit Card", "Direct Debit"));
        paymentMethodCombo.setOnAction(e -> togglePaymentFields());
    }
    
    
    public void setCustomer(Customer customer, TreeItem<String> customerItem) {
        this.customer = customer;
        this.customerItem = customerItem;

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
                .filter(s -> s != null).toList();
            customer.setSupplements(selected);

            // AssociateCustomer payer
            if (customer instanceof AssociateCustomer ac) {
                String payerName = payerCombo.getValue();
                Customer payer = MagazineService.findCustomerByName(payerName);
                if (payer instanceof PayingCustomer pc) {
                    PayingCustomer oldPayer = ac.getPayer();
                if (oldPayer != null) {
                    oldPayer.removeAssociate(ac);
                }

                ac.setPayer(pc);
                pc.addAssociate(ac);

                    MagazineController.getInstance().updateDetailView(pc);
                } else {
                    showAlert("Invalid Payer", "Selected payer is not a paying customer.");
                    return;
                }
            }

            if (customer instanceof PayingCustomer pc) {
                System.out.println("Befor save: " + pc.getPaymentMethod());
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

                System.out.println("After save: " + pc.getPaymentMethod());
                System.out.println("Method object type: " + pc.getPaymentMethod().getMethod().getClass().getName());
            }

            // EnterpriseCustomer contact
            if (customer instanceof EnterpriseCustomer ec) {
                ec.setContact(new EnterpriseCustomer.ContactPerson(
                    contactNameField.getText(),
                    contactEmailField.getText()
                ));
            }

            // Update TreeView label
            if (customerItem != null) {
                customerItem.setValue(customer.getName());  // triggers UI label update
            }

            // Refresh tree and right panel
            MagazineController.getInstance().refreshTreeView();

            // Close
            ((Stage) nameField.getScene().getWindow()).close();



            // Close
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Account number and BSB must be numeric.");
        } catch (Exception e) {
            showAlert("Unexpected Error", e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void handleDelete(){
        String name = customer.toString();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete (this cannot be undone)?",
        ButtonType.YES, ButtonType.CANCEL);
        
        
        alert.showAndWait().ifPresent(buttonType ->{
            if(buttonType == ButtonType.YES){
                MagazineService.removeCustomer(name);
            }
            
            // Close Stage
            ((Stage) nameField.getScene().getWindow()).close();            
        });
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
