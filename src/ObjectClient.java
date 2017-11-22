import java.io.*;
import java.net.Socket;

public class ObjectClient {

    private Socket clientSocket;
    private String serverName;
    private int port;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;

    public ObjectClient(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }
    public ObjectClient() { this("localhost",6066); }

    public boolean connect(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        boolean successful = false;
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            clientSocket = new Socket(serverName, port);
            System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
            in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            successful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return successful;
    }
    public boolean connect() {
        return connect(serverName,port);
    }

    public void sendMessage(ServerMessage message) {
        try {
            out.writeObject(message);
            out.flush();
            System.out.println("Sent obj:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerMessage receiveMessage() {
        ServerMessage msg = null;
        try {
            //ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            //ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            msg = (ServerMessage) in.readObject();
            System.out.println("Server received: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String [] args) {
        //String serverName = args[0];
        //int port = Integer.parseInt(args[1]);
        //String serverName = "192.168.0.110";
        String serverName = "localhost";
        int port = 6066;

        ObjectClient client = new ObjectClient(serverName,port);
        boolean connected = client.connect(serverName, port);

        if (connected) {
            ServerMessage msg = new ServerMessage(ServerMessageType.StatusPrint,"Hello from " + client.getClientSocket().getLocalSocketAddress());
            ServerMessage rcvd;
            System.out.println("Client sending: " + msg);
            client.sendMessage(msg);
            rcvd = client.receiveMessage();
            System.out.println("Server says: " + rcvd);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
            msg = new ServerMessage(ServerMessageType.StatusPrint,"Hello Again");
            System.out.println("Client sending: " + msg);
            client.sendMessage(msg);
            rcvd = client.receiveMessage();
            System.out.println("Server says: " + rcvd);
            */

            client.disconnect();
        }
    }
}
