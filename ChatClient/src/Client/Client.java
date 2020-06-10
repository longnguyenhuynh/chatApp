package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public
class Client extends Application {
    @Override
    public
    void start(Stage primaryStage) {
        try {
            Parent root  = FXMLLoader.load(getClass().getResource("/FXML/LoginUI.fxml"));
            Scene  scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Seen");
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static
    void main(String[] args) {
        launch(args);
    }
}