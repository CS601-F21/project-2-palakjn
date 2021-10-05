import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.controllers.testApplication.JsonManager;
import cs601.project2.models.Review;
import cs601.project2.models.Subscribers;
import cs601.project2.utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteBroker {
    private Subscribers<String> subscribers;
    private volatile boolean isConnected;
    private Socket eventReceiver;

    public RemoteBroker() {
        subscribers = new Subscribers<>();
        isConnected = false;
    }

    public void publish() {
        ServerSocket serverSocket = null;
        BufferedReader inStream;
        PrintWriter outStream;

        try {
            serverSocket = new ServerSocket(Constants.MESSAGE_PORT);
        } catch (IOException ioException) {
            System.out.printf("Exception while creating server socket for accepting events from remote. %s", ioException);
            return;
        }

        try {
            eventReceiver = serverSocket.accept();
            inStream = new BufferedReader(new InputStreamReader(eventReceiver.getInputStream()));
            outStream = new PrintWriter(eventReceiver.getOutputStream(), true);
        }
        catch (IOException ioException) {
            System.out.printf("Interruption happen while waiting for requests from remote host. %s", ioException);
            return;
        }

        while (isConnected) {
            try {
                String json = inStream.readLine();

                if(!Strings.isNullOrEmpty(json)) {
                    publish(json);

                    outStream.println(Constants.MESSAGES.RECEIVED);
                }
                else {
                    outStream.println(Constants.MESSAGES.INVALID_REQUEST);
                }
            }
            catch (IOException ioException) {
                System.out.printf("Interruption happen while waiting for requests from remote host. %s", ioException);
                break;
            }
        }
    }

    public void publish(String review)  {
        int numOfSubscribers = subscribers.size();

        for(int i = 0; i < numOfSubscribers; i++) {
            SubscribeHandler<String> subscribeHandler = subscribers.get(i);

            if(subscribeHandler != null) {
                subscribeHandler.onEvent(review);
            }
        }
    }

    public void subscribe(SubscribeHandler<String> subscriber) {
        subscribers.add(subscriber);
    }

    public boolean connectToServer() {

        if(!isConnected) {
            //Making a connection with server if not done before

            Socket socket = null;
            do {
                try
                {
                    System.out.println("Waiting to connect to server...");
                    socket = new Socket(Constants.IPADDRESS, Constants.CONNECTION_PORT);
                    System.out.println("Connected!");
                }
                catch (IOException ioException) {
                }
            }
            while (socket == null);

            try (
                PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                outStream.println(String.format("%s %s %d", Constants.MESSAGES.SUBSCRIBE_REQUEST, Constants.IPADDRESS, Constants.MESSAGE_PORT));
                String line = inStream.readLine();

                if(!Strings.isNullOrEmpty(line) && line.equalsIgnoreCase(Constants.MESSAGES.SUBSCRIBED)) {
                    System.out.printf("Host with IpAddress %s subscribed to the server.\n", Constants.IPADDRESS);
                    isConnected = true;
                }
                else {
                    System.out.println("Fail to subscribe to the server.");
                }
            }
            catch(IOException ioException) {
                System.out.printf("Fail to connect to server %s:%s. %s.\n", Constants.IPADDRESS, Constants.CONNECTION_PORT, ioException);
            }
            finally {
                try {
                    socket.close();
                }
                catch (IOException ioException) {
                    System.out.printf("Error while closing the socket. %s", ioException);
                }
            }
        }

        return isConnected;
    }

    public void close() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(Constants.DISCONNECTION_PORT);
        } catch (IOException ioException) {
            System.out.printf("Exception while creating server socket for accepting events from remote. %s", ioException);
            return;
        }

        try (
            Socket socket = serverSocket.accept();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line = inStream.readLine();

            if(!Strings.isNullOrEmpty(line) && line.trim().equalsIgnoreCase(Constants.MESSAGES.CLOSE_REQUEST)) {

                outStream.println(Constants.MESSAGES.CLOSE_RESPONSE);
                isConnected = false;
                if(eventReceiver != null) {
                    eventReceiver.close();
                }
            }
            else {
                outStream.println(Constants.MESSAGES.INVALID_REQUEST);
            }
        }
        catch (IOException ioException) {
            System.out.printf("Fail to close the connection %s:%s. %s.\n", Constants.IPADDRESS, Constants.CONNECTION_PORT, ioException);
        }
    }
}
