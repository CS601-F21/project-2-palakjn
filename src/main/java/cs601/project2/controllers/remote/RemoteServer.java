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

public class RemoteServer {
    private volatile boolean running;
    private List<String> clients;
    private BrokerHandler<String> reviewManager;
    private Socket remoteConnectionListener;
    private ServerSocket serverSocket = null;

    public RemoteServer(BrokerHandler<String> reviewManager) {
        running = true;
        clients = new ArrayList<>();
        this.reviewManager = reviewManager;
    }

    public void addRemoteSubscribers() {
        try {
            serverSocket = new ServerSocket(Constants.CONNECTION_PORT);
        }
        catch (IOException ioException) {
            System.out.println("Fail to create server socket object. "+ ioException.getMessage());

            return;
        }

        while (running) {
            //Will continue to look for all the clients who wish to receive the messages until close connection is not instantiated.

            try {
                System.out.println("Waiting for connections");
                remoteConnectionListener = serverSocket.accept();
                System.out.println("Received!");
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

                System.out.printf("Request body: %s. \n", line);

                boolean isValid = verifyRequest(line);

                if (isValid) {
                    System.out.println("Received request is valid");
                    String[] reqParts = line.trim().split(" ");
                    ipAddress = reqParts[1];
                    port = reqParts[2];

                    clients.add(ipAddress);

                    SubscribeHandler<String> subscriberProxy = new RemoteSubscriberProxy(ipAddress, Integer.parseInt(port));
                    reviewManager.subscribe(subscriberProxy);

                    System.out.printf("Subscribed host with IPAddress %s and port %s. \n", ipAddress, port);
                    outputMessage = Constants.MESSAGES.SUBSCRIBED;
                } else {
                    outputMessage = Constants.MESSAGES.INVALID_REQUEST;
                }

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
            else {
                isValid = true;
            }
        }

        return isValid;
    }

    public void close() {
        running = false;

        try {
            serverSocket.close();
            remoteConnectionListener.close();
        }
        catch (IOException ioException) {
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
                System.out.printf("Fail to close the connection with the host. %s:%s. %s.\n", client, Constants.CONNECTION_PORT, ioException);
            }
        }
    }
}
