package simplemessageserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Simple SimpleMessageServer Test Client
 */
public class SimpleMessageClient {

    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;
    private SimpleDateFormat sdf;

    private int port;
    private String server;
    private String name;


    /**
     *
     * @param server
     * @param port
     * @param name
     */
    SimpleMessageClient(String server, int port, String name) {
        this.server = server;
        this.port = port;
        this.name = name;
        sdf = new SimpleDateFormat("HH:mm:ss");
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }

    public boolean start() {
        // try to connect to the server
        try {
            display("Trying to connect to " + server + " : " + port);
            socket = new Socket(server, port);
        } catch (UnknownHostException e) {
            display("Error connectiong to server:" + e);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            display("Error connectiong to server:" + e);
            e.printStackTrace();
            return false;
        }
        display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

		/* Creating both Data Stream */
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ServerMessage objects
        /*
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        */
        return true;
    }


    /*
     * To send a message to the server
     */
    void sendMessage(ServerMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {} // not much else I can do
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {} // not much else I can do
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {} // not much else I can do

    }
    /*
     * To start the Client in console mode use one of the following command optons
     * > Client
     * > Client username
     * > Client username portNumber
     * > Client username portNumber serverAddress
     * If the portNumber is not specified 1500 is used
     * If the serverAddress is not specified "localHost" is used
     * If the username is not specified "Anonymous" is used
     * > Client
     * is equivalent to
     * > Client Anonymous 1500 localhost
     * if an error occurs the Client simply stops
     */
    public static void main(String[] args) {
        boolean useCmdArgs = true;

        // default values
        String serverAddress = "localhost";
        int portNumber = 1500;
        String userName = "Anonymous";

        Scanner scan = new Scanner(System.in);
        String input;

        if (!useCmdArgs) {
            System.out.print("Server: [" + serverAddress + "] :");
            input = scan.nextLine();
            if (!input.isEmpty()) {
                serverAddress = input;
            }
            while (true) {
                System.out.print("Port: [" + portNumber + "] :");
                input = scan.nextLine();
                if (input.isEmpty()) {
                    break;
                } else {
                    try {
                        portNumber = Integer.parseInt(input);
                        break;
                    } catch (Exception e) {
                        System.out.println("Invalid port number.");
                    }
                }
            }
            System.out.print("Name: [" + userName + "] :");
            input = scan.nextLine();
            if (!input.isEmpty()) {
                userName = input;
            }
        } else {
            // depending of the number of arguments provided we fall through
            switch(args.length) {
                // > Client username portNumber serverAddr
                case 3:
                    serverAddress = args[2];
                case 2:
                    try {
                        portNumber = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        System.out.println("Invalid port number.");
                        System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
                        return;
                    }
                case 1:
                    userName = args[0];
                case 0:
                    break;
                // invalid number of arguments
                default:
                    System.out.println("Usage is: > Client [username] [portNumber] {serverAddress]");
                    return;
            }
        }
        // create the Client object
        System.out.println("Starting Client: Name: " + userName + " Server: " + serverAddress + " : " + portNumber);
        SimpleMessageClient client = new SimpleMessageClient(serverAddress, portNumber, userName);
        if(!client.start()) {
            System.out.println("Unable to start Client: Name: " + userName + " Server: " + serverAddress + " : " + portNumber);
            return;
        }

        // loop forever for message from the user
        while(true) {
            System.out.print("> ");
            input = scan.nextLine();
            String[] inArray = input.split("\\s+");
            ServerMessage msg;
            switch (inArray[0]) {
                default:
                    msg = new ServerMessage(ServerMessageType.Other, input);
                    client.sendMessage(msg);
                    break;
            }
        }
        // done disconnect
        //client.disconnect();
    }

    /**
     * Listens for messages from Server and prints them
     */
    class ListenFromServer extends Thread {

        public void run() {
            while(true) {
                try {
                    ServerMessage msg = (ServerMessage) sInput.readObject();
                    display(msg.toString());
                    System.out.print("> ");
                }
                catch(IOException e) {
                    display("Server has close the connection: " + e);
                    break;
                }
                catch(ClassNotFoundException e) {
                }
            }
        }
    }
}



