package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public
class SignUpController implements Initializable {

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
    public
    void initialize(URL arg0, ResourceBundle arg1) {
        alert.setVisible(false);
        progress.setVisible(false);
    }

    @FXML
    public
    void signUpAction() throws IOException {
        if (password.getText().equals(repassword.getText())) {
            Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
            Matcher match   = pattern.matcher(username.getText());
            boolean val     = match.find();

            if (val) {
                alert.setText("Username must not contain special characters");
                alert.setVisible(true);
                progress.setVisible(false);
            } else {
                progress.setVisible(true);
                InetAddress      ip  = InetAddress.getByName(Client.ServerIP);
                Socket           s   = new Socket(ip, Client.ServerPort);
                DataInputStream  dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                try {
                    dos.writeUTF("SIGNUP#" + username.getText() + "#" + password.getText());
                    if (dis.readUTF().equals("CORRECT")) {
                        s.close();
                        displayLogin();
                    } else {
                        alert.setText("Username already exists");
                        alert.setVisible(true);
                        progress.setVisible(false);
                    }
                } catch(IOException e) {
                    s.close();
                }
            }
        } else {
            alert.setText("Password does not match");
            alert.setVisible(true);
            progress.setVisible(false);
        }
    }

    @FXML
    public
    void loginButtonClicked(ActionEvent e1) throws IOException {
        displayLogin();
    }

    public
    void displayLogin() throws IOException {
        signup.getScene().getWindow().hide();

        Stage  login = new Stage();
        Parent root  = FXMLLoader.load(getClass().getResource("/FXML/LoginUI.fxml"));
        Scene  scene = new Scene(root);
        login.setScene(scene);
        login.setTitle("Seen");
        login.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        login.show();
        login.setResizable(false);
    }
}
