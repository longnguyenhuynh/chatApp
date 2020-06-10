package Server;

import java.util.Vector;

class GroupHandler {
    public Vector<ClientHandler> clientHandlerVector = new Vector<>();
    public String groupName;

    public
    GroupHandler(String groupName) {
        this.groupName = groupName;
    }
}
