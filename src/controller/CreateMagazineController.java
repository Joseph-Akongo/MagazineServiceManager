package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Magazine;
import service.MagazineService;
import util.FileHelper;

import java.io.File;

public class CreateMagazineController {

    @FXML private TextField magazineName;
    @FXML private TextField magazinePrice;

    @FXML
    private void handleCreate() {
        try {
            String name = magazineName.getText().trim();
            String price = magazinePrice.getText().trim();

            if (name.isEmpty() || price.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields.");
                return;
            }

            float fPrice;
            try {
                fPrice = Float.parseFloat(price);
                if (fPrice < 0) {
                    showAlert("Input Error", "Price must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Invalid price format.");
                return;
            }

            Magazine magazine = new Magazine(name, fPrice);
            MagazineService.setMagazine(magazine);

            FileChooser fileChooser = FileHelper.getDatFileChooser("Save Magazine File", true);
            fileChooser.setInitialFileName(name.replaceAll("\s+", "_") + ".dat");
            File file = fileChooser.showSaveDialog(magazineName.getScene().getWindow());

            if (file == null) {
                showAlert("Cancelled", "Save operation was cancelled.");
                return;
            }

            if (!FileHelper.saveMagazineToFile(file)) {
                showAlert("Save Error", "Failed to save magazine to file.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Magazine Service Manager");
            stage.show();

            ((Stage) magazineName.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected error occurred while creating the magazine.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}