package cs601.project2.controllers.remote;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.BrokerHandler;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.models.Review;
import cs601.project2.utils.Strings;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class RemoteServer {
    private volatile boolean running;
    private Map<String, Integer> clients;

    public RemoteServer() {
        running = true;
        clients = new HashMap<>();
    }

    public void addRemoteSubscribers(BrokerHandler<Review> reviewManager) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constants.CONNECTION_PORT);
        }
        catch (IOException ioException) {
            System.out.println("Fail to create server socket object. "+ ioException.getMessage());

            return;
        }

        while (running) {
            //Will continue to look for all the clients who wish to receive the messages until close connection is not instantiated.

            try (
                Socket socket = serverSocket.accept();
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true)
                ) {
                System.out.println("One request for making connection received!");
                String ipAddress;
                String port;
                String outputMessage;

                String line = inStream.readLine();
                boolean isValid = verifyRequest(line);

                if (isValid) {
                    System.out.println("Received request is valid");
                    String[] reqParts = line.trim().split(" ");
                    ipAddress = reqParts[1];
                    port = reqParts[2];

                    clients.put(ipAddress, ((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());

                    SubscribeHandler<Review> subscriberProxy = new RemoteSubscriberProxy(ipAddress, Integer.parseInt(port));
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

        if(!Strings.isNullOrEmpty(request)) {
            System.out.println("Received request is empty.");
        }
        else {
            String[] reqParts = request.trim().split(" ");
            if(reqParts.length != 3 || reqParts[0].equalsIgnoreCase(Constants.MESSAGES.SUBSCRIBE_REQUEST)) {
                System.out.printf("Invalid request. %s", request);
            }
            else {
                isValid = true;
            }
        }

        return isValid;
    }

    public void close() {
        running = false;

        for (Map.Entry<String, Integer> entry : clients.entrySet()) {

        }
    }
}
