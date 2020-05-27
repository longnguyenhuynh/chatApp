package Controller;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    final static int ServerPort = 1234;

    public JFXButton signup;
    @FXML
    private JFXTextField username;

    @FXML
    private JFXTextField password;

    @FXML
    private ImageView progress;

    @FXML
    private JFXButton login;

    @FXML
    private JFXTextField alert;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
        progress.setVisible(false);
    }

    @FXML
    public void loginAction() throws IOException {
        progress.setVisible(true);

        InetAddress ip = null;
        ip = InetAddress.getByName("localhost");

        Socket s = null;
        s = new Socket(ip, ServerPort);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        dos.writeUTF("LOGIN#" + username.getText() + "#" + password.getText() );
        if (dis.readUTF().equals("CORRECT")){
            chatUIDisplay();
        } else {
            alert.setVisible(true);
            progress.setVisible(false);
        }
        s.close();
    }

    @FXML
    public void signUpDisplay() throws IOException {
        login.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignUpUI.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
    }

    public void chatUIDisplay() throws IOException {
        login.getScene().getWindow().hide();

        Stage chatUI = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/ChatUI.fxml"));
        Scene scene = new Scene(root);
        chatUI.setScene(scene);
        chatUI.show();
        chatUI.setResizable(false);
    }
}
