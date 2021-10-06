package cs601.project2.controllers.remote;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.BrokerHandler;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.models.Review;
import cs601.project2.utils.Strings;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Waits for the connection from remote host and if received subscribe the host as a new subscriber to the broker.
 *
 * @author Palak Jain
 */
public class RemoteConnectionServer {
    private volatile boolean running;
    private List<String> clients;
    private BrokerHandler<Review> reviewManager;
    private Socket remoteConnectionListener;
    private ServerSocket serverSocket = null;

    public RemoteConnectionServer(BrokerHandler<Review> reviewManager) {
        running = true;
        clients = new ArrayList<>();
        this.reviewManager = reviewManager;
    }

    /**
     * Add remote subscribers to the broker.
     */
    public void addRemoteSubscribers() {
        try {
            serverSocket = new ServerSocket(Constants.CONNECTION_PORT);
        }
        catch (IOException ioException) {
            System.out.println("Fail to create server socket object. " + ioException.getMessage());

            return;
        }

        while (running) {
            //Will continue to look for all the clients who wish to receive the messages until close connection is not instantiated.

            try {
                remoteConnectionListener = serverSocket.accept();
            }
            catch (IOException ioException) {
                System.out.printf("Interruption happen while waiting for connections from remote host. %s.\n", ioException);
                break;
            }

            try (
                BufferedReader inStream = new BufferedReader(new InputStreamReader(remoteConnectionListener.getInputStream()));
                PrintWriter outStream = new PrintWriter(remoteConnectionListener.getOutputStream(), true)
                ) {
                String ipAddress;
                String port;
                String outputMessage;

                String line = inStream.readLine();

                boolean isValid = verifyRequest(line);

                if (isValid) {
                    String[] reqParts = line.trim().split(" ");
                    ipAddress = reqParts[1];
                    port = reqParts[2];

                    //Add all clients IPAddress which will be useful when closing the connections
                    clients.add(ipAddress);

                    //Creating a local subscriber object
                    SubscribeHandler<Review> subscriberProxy = new RemoteSubscriberProxy(ipAddress, Integer.parseInt(port));

                    //Subscribe the subscriber to the broker
                    reviewManager.subscribe(subscriberProxy);

                    System.out.printf("Subscribed host with IPAddress %s and port %s. \n", ipAddress, port);
                    outputMessage = Constants.MESSAGES.SUBSCRIBED;
                } else {
                    outputMessage = Constants.MESSAGES.INVALID_REQUEST;
                }

                //Send a message to the server.
                outStream.println(outputMessage);
            }
            catch (IOException ioException) {
                if(ioException instanceof SocketException) {
                    System.out.printf("Interruption happen while waiting for new connection with a client. %s. \n", ioException);
                }
                else {
                    System.out.printf("Failure happen while processing request from a client. %s. \n", ioException);
                }
            }
        }
    }

    /**
     * Verify if the received request is correct. Request should
     *  1) not be null or empty
     *  2) have three words starting with "Subscribe"
     * @param request request received from client
     * @return true if valid else false
     */
    public boolean verifyRequest(String request) {
        boolean isValid = false;

        if(Strings.isNullOrEmpty(request)) {
            System.out.println("Received request is empty.");
        }
        else {
            String[] reqParts = request.trim().split(" ");
            if(reqParts.length != 3 || !reqParts[0].equalsIgnoreCase(Constants.MESSAGES.SUBSCRIBE_REQUEST)) {
                System.out.printf("Invalid request. %s.\n", request);
            }
            else if(clients.contains(reqParts[1]))
            {
                System.out.printf("Ip address: %s already linked to the server. Try on another host.", reqParts[1]);
            }
            else {
                isValid = true;
            }
        }

        return isValid;
    }

    /**
     * Close all the connections with all the clients.
     * Clients will not receive the messages from the server.
     */
    public void close() {
        running = false;

        try {
            //After closing the socket, thread will wake up from the socket.accept() call and then will exit the loop
            serverSocket.close();
            if(remoteConnectionListener != null) {
                //When there is no remote host, then socket will be null.
                remoteConnectionListener.close();
            }
        } catch (IOException ioException) {
            System.out.printf("Error while closing the remote connection. %s.\n", ioException);
        }

        for (String client : clients) {

            try (
                Socket socket = new Socket(client, Constants.DISCONNECTION_PORT);
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
            ) {
                outStream.println(Constants.MESSAGES.CLOSE_REQUEST);

                String line = inStream.readLine();

                if(!Strings.isNullOrEmpty(line) && line.equalsIgnoreCase(Constants.MESSAGES.CLOSE_RESPONSE)) {
                    System.out.printf("Closed the connection with the host with IpAddress %s. \n", client);
                }
                else {
                    System.out.println("Fail to close the connection with the host.");
                }
            }
            catch(IOException ioException) {
                System.out.printf("Fail to close the connection with the host. %s:%s. %s.\n", client, Constants.DISCONNECTION_PORT, ioException);
            }
        }
    }
}
