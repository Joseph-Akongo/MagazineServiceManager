/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: LaunchScreenController.java
 * Purpose: Controls the launch screen UI for the Magazine Service system.
 *          Provides actions to start a new magazine, load an existing file,
 *          load preset data, show credits, or exit the application.
 */

package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Magazine;
import service.HardCodedData;
import service.MagazineService;
import util.FileHelper;
import util.myInfo;

import java.io.File;

public class LaunchScreenController {

    @FXML private Button startButton; // Button to start creating a new magazine

    
    // Opens the "Create Magazine" view and closes the launch screen.
    @FXML
    public void handleStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateMagazineView.fxml"));
            Parent root = loader.load();
            Stage magStage = new Stage();
            magStage.setScene(new Scene(root, 400, 250));
            magStage.setTitle("Create Magazine");
            magStage.show();
            ((Stage) startButton.getScene().getWindow()).close(); // Close the current launch window
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     // load a previously saved magazine file (.dat) opens the main Magazine Service UI.
    @FXML
    public void handleLoad() {
        FileChooser fileChooser = FileHelper.getDatFileChooser("Open Magazine File", false);
        File file = fileChooser.showOpenDialog(startButton.getScene().getWindow());

        if (FileHelper.loadMagazineFromFile(file)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Magazine Service Manager");
                stage.show();
                ((Stage) startButton.getScene().getWindow()).close(); // Close launch screen
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Load hardcoded data into the system, shows confirmation, and opens the main UI.
    @FXML
    public void handleLoadPreset() {
        // Load predefined data
        HardCodedData.loadData();
        MagazineService.setMagazine(new Magazine("Preset Weekly", 7.99f));

        // Inform the user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Preset Data Loaded");
        alert.setHeaderText(null);
        alert.setContentText("Hardcoded magazine data has been successfully loaded.");
        alert.showAndWait();

        // Open main UI
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MagazineServiceView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Magazine Service Manager");
            stage.show();
            ((Stage) startButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    // Displays the developer credits using information from myInfo.java.
    public void handleShowCredits() {
        myInfo info = new myInfo();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText("Developer Information");
        alert.setContentText(info.getInfo());
        alert.showAndWait();
    }

    // Alert
    @FXML
    public void handleExit() {
        System.exit(0);
    }
}
