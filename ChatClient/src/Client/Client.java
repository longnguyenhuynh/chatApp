package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public
class Client extends Application {
    public static String ServerIP;
    public static int ServerPort;

    @Override
    public
    void start(Stage primaryStage) {
        try {
            Parent root  = FXMLLoader.load(getClass().getResource("/FXML/LoginUI.fxml"));
            Scene  scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Seen");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static
    void main(String[] args) {
//        ServerIP = args[0];
//        ServerPort = Integer.parseInt(args[1]);
// test
        ServerIP = "localhost";
        ServerPort = 8080;
        launch(args);
    }
}