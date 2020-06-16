package Controller;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

import Client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    public JFXButton signup;
    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

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

        InetAddress ip = InetAddress.getByName(Client.ServerIP);

        Socket s = new Socket(ip, Client.ServerPort);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        try {
            dos.writeUTF("LOGIN#" + username.getText() + "#" + password.getText());
            if (dis.readUTF().equals("CORRECT")) {
                s.close();
                chatUIDisplay();
            } else {
                alert.setVisible(true);
                progress.setVisible(false);
            }
        } catch (IOException e) {
            s.close();
        }
    }

    public void signUpDisplay() throws IOException {
        login.getScene().getWindow().hide();
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignUpUI.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.setTitle("Seen");
        signup.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        signup.show();
        signup.setResizable(false);
    }

    public void chatUIDisplay() throws IOException {
        login.getScene().getWindow().hide();
        Stage  chatUI = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChatUI.fxml"));
        Scene  scene  = new Scene(loader.load());
        ChatController controller = loader.getController();
        controller.setUsername(username.getText());
        chatUI.setScene(scene);
        chatUI.setTitle("Seen");
        chatUI.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        chatUI.show();
        chatUI.setResizable(false);
        chatUI.setOnCloseRequest(e -> System.exit(0));
    }
}
