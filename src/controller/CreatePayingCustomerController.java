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

    @FXML private TextField nameField;
    @FXML private TextField emailField;

    @FXML private ComboBox<String> paymentMethodBox;

    // Credit card fields
    @FXML private VBox creditCardFields;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField holderNameField;

    // Direct debit fields
    @FXML private VBox debitFields;
    @FXML private TextField bsbField;
    @FXML private TextField accountNumberField;

    @FXML private VBox supplementsBox;

    private List<CheckBox> supplementCheckboxes = new ArrayList<>();

    @FXML
    public void initialize() {
        // Payment method selection setup
        paymentMethodBox.getItems().addAll("Credit Card", "Direct Debit");
        paymentMethodBox.setValue("Credit Card");

        paymentMethodBox.setOnAction(e -> togglePaymentFields());

        togglePaymentFields(); // default

        // Load available supplements from service
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.getName() + " ($" + s.getWeeklyCost() + "/week)");
            cb.setUserData(s); // store the Supplement object
            supplementCheckboxes.add(cb);
            supplementsBox.getChildren().add(cb);
        }
    }

    private void togglePaymentFields() {
        boolean isCard = "Credit Card".equals(paymentMethodBox.getValue());

        creditCardFields.setVisible(isCard);
        creditCardFields.setManaged(isCard);

        debitFields.setVisible(!isCard);
        debitFields.setManaged(!isCard);
    }

    @FXML
    private void handleCreate() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        // Validate name & email
        InputValidator.isValidName(name);
        InputValidator.isValidEmail(email);

        PaymentMethod method;
        String selectedMethod = paymentMethodBox.getValue();

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

        } else if ("Direct Debit".equals(selectedMethod)) {
            String accStr = accountNumberField.getText().trim();
            String bsbStr = bsbField.getText().trim();
            
            System.out.println("accStr: '" + accStr + "', bsbStr: '" + bsbStr + "'");
            System.out.println("accStr valid? " + accStr.matches("\\d{8}"));
            System.out.println("bsbStr valid? " + bsbStr.matches("\\d{6}"));

            // Validate format before parsing
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

        // Create and populate customer
        PayingCustomer customer = new PayingCustomer(name, email, method);

        for (CheckBox cb : supplementCheckboxes) {
            if (cb.isSelected()) {
                customer.addSupplement((Supplement) cb.getUserData());
            }
        }

        MagazineService.addCustomer(customer);
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
