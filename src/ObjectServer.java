import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ObjectServer extends Thread {
    private ServerSocket serverSocket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public ObjectServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60000);
    }

    public void run() {
        System.out.println("Server Waiting for client on port " + serverSocket.getLocalPort() + "...");
        Socket server = null;
        try {
            server = serverSocket.accept();
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server Just connected to " + server.getRemoteSocketAddress());

        boolean stop = false;
        while(!stop) {
            try {
                //out = new ObjectOutputStream(server.getOutputStream());
                //in = new ObjectInputStream(server.getInputStream());
                ServerMessage msg = (ServerMessage) in.readObject();
                System.out.println("Server received: " + msg);

                ServerMessage msgBack = new ServerMessage(ServerMessageType.StatusPrint,"Back at you!");
                out.writeObject(msgBack);
                out.flush();
                System.out.println("Sent obj:" + msgBack);
            } catch (IOException e) {
                e.printStackTrace();
                stop = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                stop = true;
            }
        }
    }

    public static void main(String [] args) {
        //int port = Integer.parseInt(args[0]);
        int port = 6066;
        try {
            Thread t = new ObjectServer(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
