package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import DBConnection.DBHandler;

public class SignUpController implements Initializable {
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

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
        progress.setVisible(false);
        handler = new DBHandler();
    }

    @FXML
    public void signUpAction(ActionEvent e) throws SQLException, ClassNotFoundException {
        progress.setVisible(true);
        if (password.getText().equals(repassword.getText())) {
            // Saving Data
            String insert = "INSERT INTO chatDB(username,password)" + "VALUES (?,?)";
            connection = handler.getConnection();
            pst = connection.prepareStatement(insert);

            pst.setString(1, username.getText());
            pst.setString(2, password.getText());

            pst.executeUpdate();
            //
            PauseTransition pt = new PauseTransition();
            pt.setDuration(Duration.seconds(1));
            pt.setOnFinished(ev -> {
                try {
                    displayLogin();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            pt.play();
        }
        else {
            alert.setVisible(true);
            progress.setVisible(false);
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
