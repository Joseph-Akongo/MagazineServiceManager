package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Magazine;
import service.MagazineService;

public class EditMagazineController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;

    @FXML
    public void initialize() {
        Magazine mag = MagazineService.getMagazine();
        if (mag != null) {
            nameField.setText(mag.getName());
            priceField.setText(String.valueOf(mag.getPrice()));
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        try {
            float price = Float.parseFloat(priceText);
            if (name.isEmpty() || price <= 0) throw new IllegalArgumentException();

            MagazineService.setMagazine(new Magazine(name, price));
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please enter a valid name and positive price.");
            alert.showAndWait();
        }
    }
}
