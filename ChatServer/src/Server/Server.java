package Server;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.net.*;

public
class Server {

    static Vector<ClientHandler> clientHandlerVector = new Vector<>();
    static Vector<GroupHandler> groupHandlerVector = new Vector<>();
    static HashMap<String, String> fileHandler = new HashMap<String, String>();

    public static
    void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        getGroup();
//        getFile();
//        ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
        ServerSocket ss = new ServerSocket(8080);
        Socket       s;

        while (true) {
            s = ss.accept();
            DataInputStream  dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            try {
                String   received      = dis.readUTF();
                String[] receivedSplit = received.split("#", 3);
                switch (receivedSplit[0]) {
                    case "LOGIN":
                        dos.writeUTF(DBconnection.GetUserData(receivedSplit[1], receivedSplit[2])); // username, password
                        break;
                    case "SIGNUP":
                        dos.writeUTF(DBconnection.SaveUserData(receivedSplit[1], receivedSplit[2]));// username, password
                        break;
                    case "GROUP_ADD_CLIENT":
                        // receivedSplit[1]: group name, receivedSplit[2]: group name + new clients (separate by ,)
                        for (GroupHandler groupHandler : groupHandlerVector) {
                            if (groupHandler.groupName.equals(receivedSplit[1])) {
                                String   newClient      = receivedSplit[2].replace(receivedSplit[1] + ',', "");
                                String[] newClientSplit = newClient.split(",");
                                for (ClientHandler clientHandler : clientHandlerVector) {
                                    for (String client : newClientSplit) {
                                        if (clientHandler.name.equals(client)) {
                                            clientHandler.AddGroup(receivedSplit[2]);
                                            break;
                                        }
                                    }
                                }
                                groupHandler.groupName = receivedSplit[2];
                                DBconnection.ChangeGroupName(receivedSplit[2], receivedSplit[1]);
                                for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                                    clientHandler.ChangeGroupName(receivedSplit[2], receivedSplit[1]);
                                }
                                break;
                            }
                        }
                        break;
                    case "GROUP_REMOVE_CLIENT":
                        // receivedSplit[1]: group name, receivedSplit[2]: groupClient + removedGroupClient (separate by #)
                        String[] groupClientSplit = receivedSplit[2].split("#");
                        for (GroupHandler groupHandler : groupHandlerVector) {
                            if (groupHandler.groupName.equals(receivedSplit[1])) {
                                groupHandler.groupName = receivedSplit[1].replace(groupClientSplit[1], "");
                                DBconnection.ChangeGroupName(groupHandler.groupName, receivedSplit[1]);
                                String[] removedGroupClient = groupClientSplit[1].split(",");
                                for (ClientHandler clientHandler : clientHandlerVector) {
                                    for (String removeClientName : removedGroupClient) {
                                        if (clientHandler.name.equals(removeClientName)) {
                                            clientHandler.RemoveGroup(receivedSplit[1]);
                                            break;
                                        } else if (clientHandler.name.equals(groupClientSplit[0])) {
                                            clientHandler.ChangeGroupName(groupHandler.groupName, receivedSplit[1]);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "GROUP_CREATE":
                        GroupHandler groupHandler = new GroupHandler(receivedSplit[2]);
                        groupHandlerVector.add(groupHandler);
                        String[] clientSplit = receivedSplit[2].split(",");
                        for (ClientHandler clientHandler : clientHandlerVector) {
                            for (String client : clientSplit) {
                                if (clientHandler.name.equals(client)) {
                                    groupHandler.clientHandlerVector.add(clientHandler);
                                    break;
                                }
                            }
                        }
                        for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                            clientHandler.AddGroup(groupHandler.groupName);
                        }
                        break;
                    case "NEW_CLIENT":
                        ClientHandler client = new ClientHandler(s, receivedSplit[1], dis, dos);
                        for (ClientHandler clientHandler : clientHandlerVector) {
                            clientHandler.AddOnlineClient(client.name);
                        }
                        client.AddJoinedGroup();
                        client.AddAllOnlineClient();
                        clientHandlerVector.add(client);
                        Thread t = new Thread(client);
                        t.start();
                        break;
                }
            } catch(IOException e) {
                s.close();
            }

        }
    }

//    private static
//    void getFile() throws SQLException, ClassNotFoundException {
//        String[] groupArray = DBconnection.GetFileData().split("!");
////        for (String group : groupArray) {
////            String[] file = group.split("#");
////            fileHandler.put(file[0], file[1]);
////        }
//    }

    private static
    void getGroup() throws SQLException, ClassNotFoundException {
        String[] groupArray = DBconnection.GetGroupData().split("#");
        for (String group : groupArray) {
            GroupHandler groupHandler = new GroupHandler(group);
            groupHandlerVector.add(groupHandler);
        }
    }
}
