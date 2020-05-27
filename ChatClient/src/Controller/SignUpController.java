package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SignUpController implements Initializable {

    final static int ServerPort = 1234;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private ImageView progress;

    @FXML
    private JFXButton signup;

    @FXML
    private JFXTextField repassword;

    @FXML
    private JFXTextField alert;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
        progress.setVisible(false);
    }

    @FXML
    public void signUpAction() throws IOException {
        progress.setVisible(true);
        if (password.getText().equals(repassword.getText())) {
            if (true) {
                displayLogin();
            } else {
                alert.setVisible(true);
                progress.setVisible(false);
            }
        }
    }
    @FXML
    public void loginButtonClicked(ActionEvent e1) throws IOException {
        displayLogin();
    }

    public void displayLogin() throws IOException {
        signup.getScene().getWindow().hide();

        Stage login = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/LoginUI.fxml"));
        Scene scene = new Scene(root);
        login.setScene(scene);
        login.show();
        login.setResizable(false);
    }
}
