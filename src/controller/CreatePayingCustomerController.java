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

        InputValidator.isValidName(name);
        InputValidator.isValidEmail(email);

        PaymentMethod method;
        if ("Credit Card".equals(paymentMethodBox.getValue())) {
            String cardNum = cardNumberField.getText().trim();
            String expiry = expiryField.getText().trim();
            String holder = holderNameField.getText().trim();

            CreditCard card = new CreditCard(cardNum, expiry, holder);
            if (card.checkCardValidity() == 0) {
                showAlert("Invalid card", "Please check credit card details.");
                return;
            }

            method = new PaymentMethod(card);
        } else {
            try {
                int bsb = Integer.parseInt(bsbField.getText().trim());
                int acc = Integer.parseInt(accountNumberField.getText().trim());
                DirectDebit debit = new DirectDebit(acc, bsb);
                method = new PaymentMethod(debit);
            } catch (Exception e) {
                showAlert("Invalid direct debit", "BSB and Account must be valid numbers.");
                return;
            }
        }

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
