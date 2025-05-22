

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.HardCodedData;

public class MagazineServiceManager extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getResource("/view/LaunchScreenView.fxml");
        System.out.println("FXML Location = " + location);
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
