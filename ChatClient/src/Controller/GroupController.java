package Controller;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public
class GroupController {

    final static int ServerPort = 1234;

    private String userName;
    private String groupName;
    private String action;

    @FXML
    public JFXListView<CheckBox> groupMember;

    public
    void setUsername(String username) {
        userName = username;
    }
    public
    void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public
    void setAction(String actionToDo) {
        action = actionToDo;
    }

    public
    void createGroup() throws IOException {
        InetAddress      ip  = InetAddress.getByName("localhost");
        Socket           s   = new Socket(ip, ServerPort);
        DataOutputStream dos   = new DataOutputStream(s.getOutputStream());
        StringBuilder    group = new StringBuilder();

        switch (action) {
            case "ADD_CLIENT":
                group.append("GROUP_ADD_CLIENT#");
                group.append(groupName + "#");
                group.append(groupName);
                break;
            case "REMOVE_CLIENT":
                group.append("GROUP_REMOVE_CLIENT#");
                group.append(groupName + "#");
                group.append(userName + "#");
                break;
            case "CREATE_GROUP":
                group.append("GROUP_CREATE#");
                group.append("NULL#");
                group.append(userName);
                break;
        }
        for (CheckBox checkBox : groupMember.getItems()) {
            if (checkBox.isSelected()) {
                group.append(",").append(checkBox.getText());
            }
        }
        try {
            dos.writeUTF(group.toString());
            s.close();
            groupMember.getScene().getWindow().hide();
        } catch (IOException e) {
            s.close();
        }
    }
}
