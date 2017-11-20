import java.io.*;
import java.net.Socket;

public class ObjectClient {

    private Socket clientSocket;
    private String serverName;
    private int port;

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
            successful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return successful;
    }
    public boolean connect() {
        return connect(serverName,port);
    }

    public void sendMessage(String message) {
        try {
            OutputStream outToServer = clientSocket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(message);
            //out.writeUTF("Hello from " + clientSocket.getLocalSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        InputStream isFromServer;
        DataInputStream disFromServer = null;
        String message = "";
        try {
            isFromServer = clientSocket.getInputStream();
            disFromServer = new DataInputStream(isFromServer);
            message = disFromServer.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
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
            String msg;
            String rcvd;
            msg = "Hello from " + client.getClientSocket().getLocalSocketAddress();
            System.out.println("Client sending: " + msg);
            client.sendMessage(msg);
            rcvd = client.receiveMessage();
            System.out.println("Server says: " + rcvd);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            msg = "Hello again ";
            System.out.println("Client sending: " + msg);
            client.sendMessage(msg);
            rcvd = client.receiveMessage();
            System.out.println("Server says: " + rcvd);
            client.disconnect();
        }
    }
}
