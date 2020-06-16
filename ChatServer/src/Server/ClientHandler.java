package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

class ClientHandler implements Runnable {
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    public
    ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.s = s;
    }

    @Override
    public
    void run() {
        boolean isFile = false;
        String  received;
        while (true) {
            try {
                isFile = false;
                received = dis.readUTF();
                // break the string into message and recipient part
                String[] msgSplit = received.split("#", 2);
                String[] tmpSplit = msgSplit[1].split("#");
                switch (msgSplit[0]) {
                    case "CHAT_DISPLAY":
                        // tmpSplit[0]: username, tmpSplit[1]: username
                        String msg = DBconnection.GetChatData(tmpSplit[0], tmpSplit[1]);
//                        String[] fileSplit = msg.split("#");
//                        for (String file : fileSplit) {
//                            if (file.contains(".")) {
//                                for (String i : Server.fileHandler.keySet()) {
//                                    if (file.equals(i)) {
//                                        this.dos.writeUTF("FILE_DATA#" + i + "#" + Server.fileHandler.get(i).length());
//                                        this.dos.write(Server.fileHandler.get(i));
//                                        break;
//                                    }
//                                }
//                            }
//                        }
                        if (msg != null)
                            this.dos.writeUTF("CHAT_DISPLAY#" + msg);
                        break;
                    case "GROUP_CHAT_DISPLAY":
                        // msgSplit[1]: groupName
                        String temp = DBconnection.GetChatData(msgSplit[1], "null");
                        if (temp != null)
                            this.dos.writeUTF("CHAT_DISPLAY#" + temp);
                        break;
                    case "GROUP_CHAT":
                        // tmpSplit[0]: groupName, tmpSplit[1]: message
                        DBconnection.SaveChatData(tmpSplit[0], "null", tmpSplit[1]);
                        for (GroupHandler groupHandler : Server.groupHandlerVector) {
                            String[] clientInGroupSplit = groupHandler.groupName.split(",");
                            for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                                for (String clientInGroup : clientInGroupSplit) {
                                    if (clientHandler.name.equals(clientInGroup)) {
                                        clientHandler.dos.writeUTF("GROUP_CHAT#" + tmpSplit[0] + "#" + tmpSplit[1]);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "CHAT":
                        // tmpSplit[0]: fromClient, tmpSplit[1]: toClient, tmpSplit[2]: message
                        if (tmpSplit[0].compareTo(tmpSplit[1]) > 0)
                            DBconnection.SaveChatData(tmpSplit[0], tmpSplit[1], tmpSplit[2]);
                        else
                            DBconnection.SaveChatData(tmpSplit[1], tmpSplit[0], tmpSplit[2]);
                        for (ClientHandler clientHandler : Server.clientHandlerVector) {
                            if (clientHandler.name.equals(tmpSplit[1])) {
                                clientHandler.dos.writeUTF(tmpSplit[0] + "#" + tmpSplit[2]);
                                break;
                            }
                        }
                        break;
                    case "FILE": // fromCLient + toClient + fileName + fileLength
                        isFile = true;
                        int fileLength = Integer.parseInt(tmpSplit[3]);
                        byte[] byteArray = new byte[fileLength];
                        dis.read(byteArray, 0, fileLength);
                        if (tmpSplit[1].contains(",")) {
                            for (GroupHandler groupHandler : Server.groupHandlerVector) {
                                String[] clientInGroupSplit = groupHandler.groupName.split(",");
                                for (ClientHandler clientHandler : groupHandler.clientHandlerVector) {
                                    for (String clientInGroup : clientInGroupSplit) {
                                        if (clientHandler.name.equals(clientInGroup)) {
                                            clientHandler.dos.writeUTF("FILE#" + tmpSplit[0] + "#" + tmpSplit[1] + "#" + tmpSplit[2]);
                                            clientHandler.dos.writeUTF("FILE_DATA#" + tmpSplit[2] + "#" + fileLength);
                                            clientHandler.dos.write(byteArray, 0, fileLength);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            for (ClientHandler clientHandler : Server.clientHandlerVector) {
                                if (clientHandler.name.equals(tmpSplit[1])) {
//                                DBconnection.SaveFileData(tmpSplit[2], byteArray, tmpSplit[0], tmpSplit[1]);
//                                DBconnection.SaveChatData(tmpSplit[0], tmpSplit[1], tmpSplit[0] + ": " + tmpSplit[2]);
                                    clientHandler.dos.writeUTF("FILE#" + tmpSplit[0] + "#" + tmpSplit[2]);
                                    clientHandler.dos.writeUTF("FILE_DATA#" + tmpSplit[2] + "#" + fileLength);
                                    clientHandler.dos.write(byteArray, 0, fileLength);
                                    break;
                                }
                            }
                        }
                        break;
                }
            } catch(IOException e) {
                try {
                    if (! isFile) {
                        this.s.close();
                        Server.clientHandlerVector.remove(this);
                        RemoveClient(this.name);
                        break;
                    }
                } catch(IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch(SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    void RemoveClient(String name) throws IOException {
        for (ClientHandler clientHandler : Server.clientHandlerVector) {
            clientHandler.dos.writeUTF("REMOVE_USER#" + name);
        }
    }

    public
    void AddOnlineClient(String name) throws IOException {
        this.dos.writeUTF("NEW_USER#" + name);
    }

    public
    void AddAllOnlineClient() throws IOException {
        StringBuilder msg = new StringBuilder("ALL_USER#");

        for (ClientHandler clientHandler : Server.clientHandlerVector) {
            msg.append(clientHandler.name).append("#");
        }

        if (! msg.toString().equals("ALL_USER#")) { //first user
            this.dos.writeUTF(msg.toString());
        }
    }

    public
    void AddGroup(String groupName) throws IOException {
        this.dos.writeUTF("NEW_GROUP#" + groupName);
    }

    public
    void RemoveGroup(String groupName) throws IOException {
        this.dos.writeUTF("REMOVE_GROUP#" + groupName);
    }

    public
    void AddJoinedGroup() throws SQLException, ClassNotFoundException, IOException {
        String[] tmpSplit = DBconnection.GetGroupData().split("#");
        for (String tmp : tmpSplit) {
            String[] strSplit = tmp.split(",");
            for (String str : strSplit) {
                if (this.name.equals(str)) {
                    AddGroup(tmp);
                    for (GroupHandler groupHandler : Server.groupHandlerVector) {
                        if (groupHandler.groupName.equals(tmp)) {
                            groupHandler.clientHandlerVector.add(this);
                        }
                    }
                    break;
                }
            }
        }
    }


    public
    void ChangeGroupName(String newGroupName, String oldGroupName) throws IOException {
        this.dos.writeUTF("GROUP_NAME_CHANGE#" + newGroupName + "#" + oldGroupName);
    }
}
