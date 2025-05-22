/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Supplement;
import service.MagazineService;

/**
 *
 * @author Joseph
 */
public class EditSupplementController {
    @FXML private TextField nameField;
    @FXML private TextField costField;
    private Supplement supplement;

    public void setSupplement(Supplement s) {
        this.supplement = s;
        nameField.setText(s.getName());
        costField.setText(String.valueOf(s.getWeeklyCost()));
    }

    @FXML
    private void handleSave() {
        supplement.setName(nameField.getText().trim());
        supplement.setWeeklyCost(Double.parseDouble(costField.getText().trim()));
        ((Stage) nameField.getScene().getWindow()).close();
    }
    
    @FXML
    private void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Supplement");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This will remove the supplement from all customers.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                MagazineService.removeSupplement(supplement);
                ((Stage) nameField.getScene().getWindow()).close();
            }
        });
    }
}

