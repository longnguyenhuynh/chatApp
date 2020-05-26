package Controller;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public
class LoginController {

    @FXML
    private JFXTextField username;

    @FXML
    private JFXButton login;

    @FXML
    public
    void loginAction(ActionEvent e) throws IOException {
        chatUIDisplay();
    }

    @FXML
    public
    void login(KeyEvent e) throws IOException {
        if (e.getCode() == KeyCode.ENTER)
            chatUIDisplay();
    }

    public
    void chatUIDisplay() throws IOException {
        login.getScene().getWindow().hide();

        Stage  chatUI = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChatUI.fxml"));

        Scene  scene  = new Scene(loader.load());

        ChatUIController controller = loader.getController();
        controller.setUsername(username.getText());

        chatUI.setScene(scene);
        chatUI.show();
        chatUI.setResizable(false);

    }
}
