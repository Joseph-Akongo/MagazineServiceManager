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

    private final List<CheckBox> supplementCheckboxes = new ArrayList<>();

    @FXML
    public void initialize() {
        for (Customer c : MagazineService.getCustomers()) {
            if (c instanceof PayingCustomer) {
                payerComboBox.getItems().add(c.getName());
            }
        }

        if (!payerComboBox.getItems().isEmpty()) {
            payerComboBox.setValue(payerComboBox.getItems().get(0));
        }

        for (Supplement s : MagazineService.getAvailableSupplements()) {
            CheckBox cb = new CheckBox(s.toString());
            cb.setUserData(s);
            supplementCheckboxes.add(cb);
            supplementsBox.getChildren().add(cb);
        }
    }

    @FXML
    private void handleCreate() {
        String name = nameField.getText();
        String email = emailField.getText();
        String payerName = payerComboBox.getValue();

        InputValidator.isValidName(name);
        InputValidator.isValidEmail(email);

        PayingCustomer payer = (PayingCustomer) MagazineService.findCustomerByName(payerName);
        if (payer == null) {
            showAlert("Not Found", "Selected payer could not be found.");
            return;
        }

        AssociateCustomer customer = new AssociateCustomer(name, email, payer);
        payer.addAssociate(customer);

        for (CheckBox cb : supplementCheckboxes) {
            if (cb.isSelected()) {
                customer.addSupplement((Supplement) cb.getUserData());
            }
        }

        MagazineService.addCustomer(customer);
        MagazineController.getInstance().updateDetailView(payer);

        closeWindow();
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
