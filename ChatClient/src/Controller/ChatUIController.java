package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;

import java.util.ResourceBundle;

public
class ChatUIController implements Initializable {

    final static int ServerPort = 1234;

    private String selectedUser = null;

    @FXML
    private JFXTextField message;

    @FXML
    private JFXTextField userName;

    @FXML
    private JFXListView<String> chatBox;

    @FXML
    private JFXListView<String> onlineList;

    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public
    void initialize(URL arg0, ResourceBundle arg1) {

        onlineList.setOnMouseClicked(event -> { // có thê bằng null
            System.out.println(selectedUser);
            selectedUser = onlineList.getSelectionModel().getSelectedItem();
        });

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Socket s = null;
        try {
            s = new Socket(ip, ServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert s != null;
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket finalS = s;
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String msg = dis.readUTF();

                    // break the string into message and recipient part
                    String[] msgSplit     = msg.split("#", 2);
                    switch (msgSplit[0]) {
                        case "NEW_USER":
                            onlineList.getItems().add(msgSplit[1]);
                            break;
                        case "ALL_USER":
                            String[] userString = msgSplit[1].split("#");
                            for (String user : userString) {
                                onlineList.getItems().add(user);
                            }
                            break;
                        default:
                            chatBox.getItems().add(msgSplit[0] + ": " + msgSplit[1]);
                            break;
                    }
                } catch (IOException e) {
                    try {
                        finalS.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        readMessage.start();
    }

    @FXML
    void buttonSend() {
        sendMessage();
    }

    @FXML
    public
    void textBoxSend(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER)
            sendMessage();
    }

    private
    void sendMessage() {
        try {
            String msg = selectedUser + "#" + message.getText();
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public
    void setUsername(String username) throws IOException {
        userName.setText(username);
        dos.writeUTF(userName.getText());
    }

}
