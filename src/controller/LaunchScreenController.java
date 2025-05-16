package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Magazine;
import service.HardCodedData;
import service.MagazineService;
import util.FileHelper;

import java.io.File;

public class LaunchScreenController {

    @FXML private Button startButton;

    @FXML
    public void handleStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateMagazineView.fxml"));
            Parent root = loader.load();
            Stage magStage = new Stage();
            magStage.setScene(new Scene(root, 400, 250));
            magStage.setTitle("Create Magazine");
            magStage.show();
            ((Stage) startButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                ((Stage) startButton.getScene().getWindow()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleLoadPreset() {
        HardCodedData.loadData();
        MagazineService.setMagazine(new Magazine("Preset Weekly", 7.99f));
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

    @FXML
    public void handleExit() {
        System.exit(0);
    }
}