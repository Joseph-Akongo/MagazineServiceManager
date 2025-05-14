package controller;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import model.Supplement;
import service.MagazineService;

public class EditCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextArea supplementArea;
    @FXML private Button saveButton;
    @FXML private VBox supplementBox;

    private Customer customer;
    private String originalName;
    private String originalEmail;
    private List<CheckBox> supplementCheckBoxes = new ArrayList<>();

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.originalName = customer.getName();
        this.originalEmail = customer.getEmail();

        nameField.setText(originalName);
        emailField.setText(originalEmail);

        // Listen to field changes
        nameField.textProperty().addListener((obs, oldVal, newVal) -> checkForChanges());
        emailField.textProperty().addListener((obs, oldVal, newVal) -> checkForChanges());

        // Clear and repopulate checkboxes
        supplementBox.getChildren().clear();
        supplementCheckBoxes.clear();

        List<Supplement> allSupplements = MagazineService.getAvailableSupplements();
        List<Supplement> selectedSupplements = customer.getSupplements();

        for (Supplement s : allSupplements) {
            CheckBox cb = new CheckBox(s.getName());
            if (selectedSupplements.contains(s)) {
                cb.setSelected(true);
            }
            cb.selectedProperty().addListener((obs, wasSelected, isSelected) -> checkForChanges()); // ✅ track changes
            supplementCheckBoxes.add(cb);
            supplementBox.getChildren().add(cb);
        }

        checkForChanges(); // Ensure button state is set initially
    }

    @FXML
    private void handleSave() {
        try {
            // Save updated name and email
            customer.setName(nameField.getText());
            customer.setEmail(emailField.getText());

            // Collect selected supplements
            List<Supplement> selected = new ArrayList<>();
            for (CheckBox cb : supplementCheckBoxes) {
                if (cb.isSelected()) {
                    // You must get the actual Supplement object (not just name!)
                    Supplement s = MagazineService.findSupplementByName(cb.getText());
                    if (s != null) selected.add(s);
                }
            }

            // Save the supplements to the customer
            customer.setSupplements(selected);

            // Close the window
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void checkForChanges() {
        boolean nameChanged = !nameField.getText().equals(originalName);
        boolean emailChanged = !emailField.getText().equals(originalEmail);

        // Check if supplements changed
        List<Supplement> currentSupps = customer.getSupplements();
        List<String> currentNames = currentSupps.stream().map(Supplement::getName).toList();
        List<String> selectedNames = supplementCheckBoxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .toList();

        boolean supplementsChanged = !currentNames.containsAll(selectedNames) || !selectedNames.containsAll(currentNames);

        boolean changed = nameChanged || emailChanged || supplementsChanged;
        saveButton.setDisable(!changed); // ✅ enable only when something changed
    }

    private void checkForChnages(){
        boolean changed = !nameField.getText().equals(originalName)|| !emailField.getText().equals(originalEmail);
        saveButton.setDisable(!changed);
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}