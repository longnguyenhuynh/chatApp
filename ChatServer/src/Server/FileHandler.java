package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public
class FileHandler implements Runnable {
    private String toClient;
    private String fromClient;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    public
    FileHandler(Socket s, DataInputStream dis, DataOutputStream dos, String fromClient, String toClient) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.toClient = toClient;
        this.fromClient = fromClient;
    }

    @Override
    public
    void run() {
        try {
            String received = dis.readUTF();
            String[] strSplit = received.split("#");
            int fileLength = Integer.parseInt(strSplit[1]);
            byte[] byteArray = new byte[fileLength];
            dis.read(byteArray, 0, fileLength);
//            for (ClientHandler clientHandler : Server.clientHandlerVector) {
//                if (clientHandler.name.equals(toClient)) {
//                    clientHandler.dos.writeUTF("FILE#" + fromClient + "#" + strSplit[0] + "#" + fileLength);
//                    clientHandler.dos.write(byteArray, 0, fileLength);
//                    clientHandler.dos.flush();
//                    System.out.write(byteArray, 0, fileLength);
//                    break;
//                }
//            }
        }
        catch(IOException e) {
            try {
                this.s.close();
            } catch(IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
