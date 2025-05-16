package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import model.Customer;
import model.Magazine;
import model.MagazineData;
import service.HardCodedData;
import service.MagazineService;

public class LaunchScreenController {

    @FXML private Button startButton;
    
    @FXML
    public void handleStart() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/view/CreateMagazineView.fxml"));
            CreateMagazineController controller = loader.getController();
            
            Stage magStage = new Stage();
           
            magStage.setScene(new Scene(root, 400, 250));
            magStage.setTitle("Create Magazine");
            magStage.show();

            // Close the current launch screen
            Stage currentStage = (Stage) startButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        File file = fileChooser.showOpenDialog(startButton.getScene().getWindow());

        if (file != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                // Read full data object (not just Magazine anymore)
                MagazineData data = (MagazineData) in.readObject();

                // Clear and set service data
                MagazineService.clearCustomers();
                MagazineService.setAvailableSupplements(data.getSupplements());
                MagazineService.setMagazine(new Magazine(data.getMagazineName(), data.getPrice()));
                for (Customer c : data.getCustomers()) {
                    MagazineService.addCustomer(c);
                }
                
                System.out.println("Loaded: " + data.getMagazineName());
                System.out.println("Supplements: " + data.getSupplements().size());
                System.out.println("Customers: " + data.getCustomers().size());

                // Load the main view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Magazine Service Manager");
                stage.show();

                // Close the launch screen
                Stage currentStage = (Stage) startButton.getScene().getWindow();
                currentStage.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Load Failed", "Could not load magazine from file.");
            }
        } else {
            showAlert("Cancelled", "No file selected.");
        }
    }
    
    @FXML
    public void handleLoadPreset() {
        try {
            // Load hardcoded data into MagazineService
            HardCodedData.loadData();

            // Manually set magazine (assumes consistent preset values)
            MagazineService.setMagazine(new Magazine("Tech World Weekly", 9.99f));

            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Magazine Service Manager");
            stage.show();

            // Close the launch screen
            Stage currentStage = (Stage) startButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load preset data.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void handleExit() {
        System.exit(0);
    }

}
