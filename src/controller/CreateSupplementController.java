package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Supplement;
import service.MagazineService;

public class CreateSupplementController {

    @FXML private TextField nameField;
    @FXML private TextField costField;

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String costText = costField.getText().trim();

        if (name.isEmpty() || costText.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        try {
            double cost = Double.parseDouble(costText);
            if (cost < 0) throw new NumberFormatException();
            MagazineService.getAvailableSupplements().add(new Supplement(name, cost));

            showAlert("Success", "Supplement created.");
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert("Error", "Cost must be a positive number.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.INFORMATION, content).showAndWait();
    }
}
