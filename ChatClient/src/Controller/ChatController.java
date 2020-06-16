package Controller;

import Client.Client;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public
class ChatController implements Initializable {

    HashMap<String, byte[]> fileHandler = new HashMap<>();

    private String selectedUser = null;

    @FXML
    private JFXTextField message;

    @FXML
    private ImageView add;
    @FXML
    private ImageView remove;

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
        GroupAddRemoveOff();
        onlineList.setOnMouseClicked(event -> {
            String str = onlineList.getSelectionModel().getSelectedItem();
            if (str != null && ! str.equals(selectedUser)) {
                selectedUser = str;
                try {
                    chatBox.getItems().clear();
                    if (str.indexOf(',') != - 1) {
                        GroupAddRemoveOn();
                        dos.writeUTF("GROUP_CHAT_DISPLAY#" + str);
                    } else {
                        GroupAddRemoveOff();
                        String tmp = this.userName.getText();
                        if (tmp.compareTo(selectedUser) > 0) {
                            dos.writeUTF("CHAT_DISPLAY#" + tmp + "#" + selectedUser);
                        } else {
                            dos.writeUTF("CHAT_DISPLAY#" + selectedUser + "#" + tmp);
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
        chatBox.setOnMouseClicked(event -> {
            String str = chatBox.getSelectionModel().getSelectedItem();
            if (str != null) {
                FileChooser fileChooser     = new FileChooser();
                String[] fileSplit = str.split(": ");
                byte[]   fileData  = fileHandler.get(fileSplit[1]);
                fileChooser.setInitialFileName(fileSplit[1]);
                try {
                    FileOutputStream     fos       = new FileOutputStream(fileChooser.showSaveDialog(null));
                    fos.write(fileData);
                } catch(IOException | NullPointerException ignored){ }
            }
        });

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(Client.ServerIP);
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        Socket s = null;
        try {
            s = new Socket(ip, Client.ServerPort);
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            assert s != null;
            dis = new DataInputStream(s.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            dos = new DataOutputStream(s.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }

        Socket finalS = s;
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String   msg      = dis.readUTF();
                    if (msg.contains("FILE_DATA#")) { // fileName + fileLength
                        String[] tmpSplit = msg.split("#");
                        int fileLength = Integer.parseInt(tmpSplit[2]);
                        byte[] bytes = new byte[fileLength];
                        dis.read(bytes, 0, fileLength);
                        fileHandler.put(tmpSplit[1], bytes);
                    }
                    else {
                        String[] msgSplit = msg.split("#", 2);
                        Platform.runLater(() -> { // for java.lang.IllegalStateException: Not on FX application thread
                            switch (msgSplit[0]) {
                                case "NEW_USER":
                                    // msgSplit[1]: username
                                    onlineList.getItems().add(msgSplit[1]);
                                    break;
                                case "NEW_GROUP":
                                    // msgSplit[1]: groupName
                                    onlineList.getItems().add(msgSplit[1].replace("#", ", "));
                                    break;
                                case "ALL_USER":
                                    // msgSplit[1]: online user name
                                    String[] userName = msgSplit[1].split("#");
                                    for (String user : userName) {
                                        onlineList.getItems().add(user);
                                    }
                                    break;
                                case "REMOVE_USER":
                                case "REMOVE_GROUP":
                                    // msgSplit[1]: username or groupName
                                    onlineList.getItems().remove(msgSplit[1]);
                                    break;
                                case "CHAT_DISPLAY":
                                    // msgSplit[1]: messages
                                    chatBox.getItems().clear();
                                    String[] tmpArray = msgSplit[1].split("#");
                                    for (String str : tmpArray)
                                        chatBox.getItems().add(str);
                                    break;
                                case "GROUP_NAME_CHANGE":
                                    // msgSplit[1]: new group name + old group name
                                    String[] temp = msgSplit[1].split("#");
                                    Object[] onlineArray = onlineList.getItems().toArray();
                                    for (int i = 0; i < onlineArray.length; i++) {
                                        if (onlineArray[i].toString().equals(temp[1])) {
                                            onlineList.getItems().set(i, temp[0]);
                                            break;
                                        }
                                    }
                                    selectedUser = temp[0];
                                    break;
                                case "GROUP_CHAT":
                                    // msgSplit[1]: toClient + message
                                    String[] tmpSplit = msgSplit[1].split("#");
                                    if (selectedUser != null && selectedUser.equals(tmpSplit[0]))
                                        chatBox.getItems().add(tmpSplit[1]);
                                    break;
                                case "CHAT":
                                    if (selectedUser != null && selectedUser.equals(msgSplit[0]))
                                        chatBox.getItems().add(msgSplit[1]);
                                    break;
                                case "FILE": // fromClient + fileName
                                    String[] strSplit = msgSplit[1].split("#");
                                    if (selectedUser != null && selectedUser.equals(strSplit[0])) {
                                        chatBox.getItems().add(strSplit[0] + ": " + strSplit[1]);
                                    }
                                    break;
                            }
                        });
                    }
                } catch(IOException e) {
                    try {
                        finalS.close();
                    } catch(IOException ioException) {
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
            if (selectedUser != null) {
                if (selectedUser.indexOf(',') != - 1) {
                    String to      = selectedUser;
                    String yourMsg = userName.getText() + ": " + message.getText();
                    dos.writeUTF("GROUP_CHAT#" + to + "#" + yourMsg);
                    message.clear();
                } else {
                    String from    = userName.getText();
                    String to      = selectedUser;
                    String yourMsg = from + ": " + message.getText();

                    dos.writeUTF("CHAT#" + from + "#" + to + "#" + yourMsg);
                    chatBox.getItems().add(yourMsg);
                    message.clear();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public
    void setUsername(String username) throws IOException {
        userName.setText(username);
        dos.writeUTF("NEW_CLIENT#" + userName.getText());
    }

    public
    void createGroupChat() throws IOException {
        Stage           createGroup = new Stage();
        FXMLLoader      loader      = new FXMLLoader(getClass().getResource("/FXML/Group.fxml"));
        Scene           scene       = new Scene(loader.load());
        GroupController controller  = loader.getController();
        controller.setAction("CREATE_GROUP");
        controller.setUsername(userName.getText());
        String tmp = getOnlineUser();
        if (tmp != null && ! tmp.trim().isEmpty()) {
            String[] onlineUser = tmp.split("#");
            for (String user : onlineUser) {
                CheckBox checkBox = new CheckBox();
                checkBox.setText(user);
                controller.groupMember.getItems().add(checkBox);
            }
        }
        createGroup.setScene(scene);
        createGroup.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        createGroup.show();
        createGroup.setResizable(false);
    }

    public
    String getOnlineUser() {
        StringBuilder stringBuilder = new StringBuilder();
        Object[]      onlineArray   = onlineList.getItems().toArray();
        for (Object o : onlineArray) {
            if (o.toString().indexOf(',') == - 1 && ! o.toString().equals(userName.getText())) {
                stringBuilder.append(o.toString()).append("#");
            }
        }
        return stringBuilder.toString();
    }

    public
    void fileSend() {
        try {
            if (selectedUser != null) {
                FileInputStream     fis           = null;
                BufferedInputStream bis           = null;
                FileChooser         fileChooser   = new FileChooser();
                List<File>          selectedFiles = fileChooser.showOpenMultipleDialog(null);
                if (selectedFiles != null) {
                    InetAddress ip = InetAddress.getByName(Client.ServerIP);
                    Socket      s  = new Socket(ip, Client.ServerPort);
                    dos = new DataOutputStream(s.getOutputStream());

                    for (File file : selectedFiles) {
                        byte[] byteArray = new byte[(int) file.length()];
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        bis.read(byteArray, 0, byteArray.length);
                        dos.writeUTF("FILE#" + userName.getText() + "#" + selectedUser + "#" + file.getName() + "#" + byteArray.length);
                        dos.write(byteArray, 0, byteArray.length);
                        dos.flush();
                    }
                    assert fis != null;
                    fis.close();
                    bis.close();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public
    void groupAdd() throws IOException {
        Stage           createGroup = new Stage();
        FXMLLoader      loader      = new FXMLLoader(getClass().getResource("/FXML/Group.fxml"));
        Scene           scene       = new Scene(loader.load());
        GroupController controller  = loader.getController();
        controller.setAction("ADD_CLIENT");
        controller.setGroupName(selectedUser);
        String   tmp      = getOnlineUser();
        String[] tmpSplit = tmp.split("#"); // User online
        String[] strSplit = selectedUser.split(","); // User already in group
        for (String temp : tmpSplit) {
            if (temp.indexOf(',') == - 1) {
                for (String str : strSplit) {
                    if (temp.equals(str)) {
                        tmp = tmp.replace(temp + "#", "");
                        break;
                    }
                }
            }
        }
        tmpSplit = tmp.split("#");
        for (String user : tmpSplit) {
            if (user != null && ! user.trim().isEmpty()) {
                CheckBox checkBox = new CheckBox();
                checkBox.setText(user);
                controller.groupMember.getItems().add(checkBox);
            }
        }
        createGroup.setScene(scene);
        createGroup.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        createGroup.show();
        createGroup.setResizable(false);
    }

    public
    void groupRemove() throws IOException {
        Stage           createGroup = new Stage();
        FXMLLoader      loader      = new FXMLLoader(getClass().getResource("/FXML/Group.fxml"));
        Scene           scene       = new Scene(loader.load());
        GroupController controller  = loader.getController();
        controller.setAction("REMOVE_CLIENT");
        controller.setGroupName(selectedUser);
        controller.setUsername(userName.getText());
        String[] strSplit = selectedUser.split(",");
        for (String user : strSplit) {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(user);
            controller.groupMember.getItems().add(checkBox);
        }
        createGroup.setScene(scene);
        createGroup.getIcons().add(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        createGroup.show();
        createGroup.setResizable(false);
    }

    void GroupAddRemoveOn() {
        add.setVisible(true);
        add.setDisable(false);
        remove.setVisible(true);
        remove.setDisable(false);
    }

    void GroupAddRemoveOff() {
        add.setVisible(false);
        add.setDisable(true);
        remove.setVisible(false);
        remove.setDisable(true);
    }

}