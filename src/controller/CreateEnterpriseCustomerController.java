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

    @FXML
    public void initialize() {
        paymentMethodBox.getItems().addAll("Credit Card", "Direct Debit");
        paymentMethodBox.setValue("Credit Card");
        paymentMethodBox.setOnAction(e -> togglePaymentFields());

        togglePaymentFields();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        copiesSpinner.setValueFactory(valueFactory);

        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.toString());
            cb.setUserData(s);
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
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String contactName = contactNameField.getText();
            String contactEmail = contactEmailField.getText();
            int copies = copiesSpinner.getValue();

            PaymentMethod method;
            if ("Credit Card".equals(paymentMethodBox.getValue())) {
                CreditCard card = new CreditCard(
                        cardNumberField.getText(),
                        expiryField.getText(),
                        holderNameField.getText()
                );
                if (card.checkCardValidity() == 0) {
                    showAlert("Invalid Credit Card", "Please provide valid card details.");
                    return;
                }
                method = new PaymentMethod(card);
            } else {
                int bsb = Integer.parseInt(bsbField.getText());
                int acc = Integer.parseInt(accountNumberField.getText());
                method = new PaymentMethod(new DirectDebit(acc, bsb));
            }
            
            InputValidator.isValidName(name);
            InputValidator.isValidEmail(email);

            EnterpriseCustomer.ContactPerson cp = new EnterpriseCustomer.ContactPerson(contactName, contactEmail);
            EnterpriseCustomer ec = new EnterpriseCustomer(name, email, method, cp, copies);

            for (CheckBox cb : supplementCheckboxes) {
                if (cb.isSelected()) {
                    ec.addSupplement((Supplement) cb.getUserData());
                }
            }

            MagazineService.addCustomer(ec);
            closeWindow();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
