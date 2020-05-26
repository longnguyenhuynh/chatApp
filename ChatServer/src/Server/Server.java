package Server;

import java.io.*;
import java.util.*;
import java.net.*;

public
class Server {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    public static
    void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        while (true) {
            // Accept the incoming request
            s = ss.accept();

            // obtain input and output streams
            DataInputStream  dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Create a new handler object for handling this request.
            ClientHandler client = new ClientHandler(s, dis.readUTF(), dis, dos);

            for (ClientHandler clt : Server.ar) { //notify current users new user
                clt.AddOnlineClient(client.name);
            }

            client.AddAllOnlineClient();

            // add new client to active clients list
            ar.add(client);

            // Create a new Thread with this object.
            Thread t = new Thread(client);

            // start the thread.
            t.start();
        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable {
    public String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    // constructor
    public
    ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
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

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(msgSplit[0]))
                    {
                        mc.dos.writeUTF(this.name + "#" + msgSplit[1]);
                        break;
                    }
                }
            } catch (IOException e) {
                try {
                    Server.ar.remove(this);
                    this.s.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }
    }

    public
    void AddOnlineClient(String name) throws IOException {
        this.dos.writeUTF("NEW_USER#" + name);
    }

    public
    void AddAllOnlineClient() throws IOException {
        StringBuilder msg = new StringBuilder("ALL_USER#");
        for (ClientHandler clt : Server.ar) {
            msg.append(clt.name).append("#");
        }
        if(!msg.toString().equals("ALL_USER#")) { //first user
            this.dos.writeUTF(msg.toString());
        }
    }
}
