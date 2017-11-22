package simplemessageserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * The server that can be run as a console application
 */
public class SimpleMessageServer {
    private int port;
    private static int uniqueId;
    private ArrayList<ClientThread> clientThreadList;
    private SimpleDateFormat sdf;
    private boolean stopServer;


    /**
     *
     * @param port port number to listen on
     */
    public SimpleMessageServer(int port) {
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        clientThreadList = new ArrayList<ClientThread>();
    }

    /**
     * Start Server
     * - create ServerSocket and wait for new connections
     */
    public void start() {
        stopServer = false;
        try {
            ServerSocket serverSocket = new ServerSocket(port);

        // loop waiting for connections
            while(!stopServer)
            {
                // wait for new client connection
                display("Server waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept();  	// accept connection
                if(stopServer)
                    break;
                // create new ClientThread and start it
                ClientThread clientThread = new ClientThread(socket,uniqueId++);  // make a thread of it
                clientThreadList.add(clientThread);					   // save it in the ArrayList
                clientThread.start();
            }
            // After stopServer
            try {
                serverSocket.close();
                for(int i = 0; i < clientThreadList.size(); ++i) {
                    ClientThread clientThread = clientThreadList.get(i);
                    clientThread.sInput.close();
                    clientThread.sOutput.close();
                    clientThread.socket.close();
                }
            }
            catch(IOException e) {
                display("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            display("Exception on new ServerSocket: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Display an event (not a message) to the console
     * @param msg
     */
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }

    /**
     * Broadcast a message to all clients
     * @param msg
     */
    private synchronized void broadcast(ServerMessage msg) {
        display("Broadcast: " + msg);

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for(int i = clientThreadList.size(); --i >= 0;) {
            ClientThread ct = clientThreadList.get(i);
            // try to write to the Client if it fails remove it from the list
            if(!ct.writeMsg(msg)) {
                clientThreadList.remove(i);
                display("Disconnected Client " + ct.name + " removed from list.");
            }
        }
    }

    /**
     * Remove a client
     * @param id
     */
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < clientThreadList.size(); ++i) {
            ClientThread ct = clientThreadList.get(i);
            // found it
            if(ct.id == id) {
                clientThreadList.remove(i);
                return;
            }
        }
    }

    /*
     *
     */
    public static void main(String[] args) {
        int portNumber = 1500;
        switch(args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: SimpleMessageServer [portNumber]");
                return;

        }
        // create a server object and start it
        SimpleMessageServer server = new SimpleMessageServer(portNumber);
        server.start();
    }


    /**
     * Thread runs for each client
     */
    class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;

        int id;
        String name;
        ServerMessage sMsg;

        /**
         *  Constructor
         * @param socket
         */
        ClientThread(Socket socket, int id, String name) {
            this.socket = socket;
            this.id = id;
            this.name = name;

            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
                // output stream must be created first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                display(name + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
        }
        ClientThread(Socket socket, int id) {this(socket,id,"player" + id);};
        ClientThread(Socket socket) {this(socket,uniqueId++);};


        /**
         * Thread run() will loop
         */
        public void run() {
            boolean stopThread = false;
            while(!stopThread) {
                // read a message
                try {
                    sMsg = (ServerMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(name + " Exception reading Streams: " + e);
                    e.getStackTrace();
                    break;
                }
                catch(ClassNotFoundException e) {
                    e.getStackTrace();
                    break;
                }

                // Switch on the type of message receive
                switch(sMsg.getType()) {
                    default:
                        broadcast(sMsg);
                        break;
                }
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if(sOutput != null)
                    sOutput.close();
            }
            catch(Exception e) {}
            try {
                if(sInput != null)
                    sInput.close();
            }
            catch(Exception e) {};
            try {
                if(socket != null)
                    socket.close();
            }
            catch (Exception e) {}
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(ServerMessage msg) {
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {
                display("Error sending message to " + name);
                display(e.toString());
            }
            return true;
        }
    }
}


