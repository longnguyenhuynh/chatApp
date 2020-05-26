package Controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;

import java.util.ResourceBundle;
import java.util.Vector;

class ChatHandler {
    public String userName;
    public StringBuilder message;
}

public
class ChatUIController implements Initializable {

    static Vector<ChatHandler> ar = new Vector<>();

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
            if (onlineList.getSelectionModel().getSelectedItem() != null) {
                selectedUser = onlineList.getSelectionModel().getSelectedItem();
                chatBox.getItems().clear();
                for (ChatHandler clt : ar) {
                    if(clt.userName.equals(selectedUser)){
                        chatBox.getItems().add(clt.message.toString());
                    }
                };
            }
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
                    Platform.runLater(() -> { // for java.lang.IllegalStateException: Not on FX application thread
                    switch (msgSplit[0]) {
                        case "NEW_USER":
                            ChatHandler client = new ChatHandler();
                            client.userName = msgSplit[1];
                            ar.add(client);
                            onlineList.getItems().add(msgSplit[1]);
                            break;
                        case "ALL_USER":
                            String[] userString = msgSplit[1].split("#");
                            for (String user : userString) {
                                ChatHandler clients = new ChatHandler();
                                clients.userName = user;
                                ar.add(clients);
                                onlineList.getItems().add(user);
                            }
                            break;
                        case "REMOVE_USER":
                            for (ChatHandler clt : ar) {
                                if(clt.userName.equals(msgSplit[1]))
                                    ar.remove(clt);
                            };
                            onlineList.getItems().remove(msgSplit[1]);
                            break;
                        default:
                            for (ChatHandler clt : ar) {
                                if(clt.userName.equals(msgSplit[0])) {
                                    clt.message.append(msgSplit[0]).append(": ").append(msgSplit[1]).append("\n");
                                }
                            };
                            break;
                    }
                    });
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
            for (ChatHandler clt : ar) {
                if(clt.userName.equals(selectedUser)) {
                    clt.message.append(clt.userName).append(": ").append(message.getText()).append("\n");
                }
            };
            String msg = selectedUser + "#" + message.getText();
            message.clear();
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
