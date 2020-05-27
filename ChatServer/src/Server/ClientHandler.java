package Server;

import javax.management.MBeanTrustPermission;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

// ClientHandler class
class ClientHandler implements Runnable {
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    // constructor
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
        String received;
        while (true) {
            try {
                // receive the string
                received = dis.readUTF();

                // break the string into message and recipient part
                String[] msgSplit     = received.split("#", 2);

                for (ClientHandler clientHandler : Server.clientHandlerVector) {
                    if (clientHandler.name.equals(msgSplit[0])) {
                        clientHandler.dos.writeUTF(this.name + "#" + msgSplit[1]);
                        break;
                    }
                }
            } catch (IOException e) {
                try {
                    for (Iterator<ClientHandler> iterator = Server.clientHandlerVector.iterator(); iterator.hasNext();) {
                        ClientHandler clientHandler = iterator.next();
                        if (clientHandler.equals(this)) {
                            RemoveClient(clientHandler.name);
                            iterator.remove();
                            break;
                        }
                    }
                    this.s.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
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

        if(!msg.toString().equals("ALL_USER#")) { //first user
            this.dos.writeUTF(msg.toString());
        }
    }
}
