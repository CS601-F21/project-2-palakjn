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

/**
 * Receives items from publishers in remote host for local subscribers.
 *
 * @author Palak Jain
 */
public class RemoteBroker {
    private Subscribers<Review> subscribers;
    private volatile boolean isConnected;
    private ServerSocket serverSocket;

    public RemoteBroker() {
        subscribers = new Subscribers<>();
        isConnected = false;
    }

    /**
     * Subscribe local Subscribers for future events.
     * @param subscriber Subscriber who wants to enroll
     */
    public void subscribe(SubscribeHandler<Review> subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Send a request to the remote server to subscribe for future events
     * @return true if successfully connected else false
     */
    public boolean connectToServer() {
        if(!isConnected) {
            //Making a connection with server listening if not done before

            try (
                    Socket socket = new Socket(Constants.IPADDRESS, Constants.CONNECTION_PORT);
                    PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                //Sending request like "Subscribe localhost 3032"
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
        }

        return isConnected;
    }

    /**
     * Receives event from remote server and publish it to the local subscribers.
     */
    public void publish() {
        try {
            serverSocket = new ServerSocket(Constants.MESSAGE_PORT);
        } catch (IOException ioException) {
            System.out.printf("Exception while creating server socket for accepting events from remote. %s", ioException);
            return;
        }

        try (
            Socket remoteListener = serverSocket.accept();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(remoteListener.getInputStream()));
            PrintWriter outStream = new PrintWriter(remoteListener.getOutputStream(), true)
            ){

            //Until the connection is stable, waiting for events
            while (isConnected) {
                try {
                    //Client expect to receive json format. If the received string is not in json format then, Subscriber will handle the issue.
                    String json = inStream.readLine();

                    if (!Strings.isNullOrEmpty(json)) {
                        publish(json);

                        outStream.println(Constants.MESSAGES.RECEIVED);
                    } else {
                        outStream.println(Constants.MESSAGES.INVALID_REQUEST);
                    }
                } catch (IOException ioException) {
                    System.out.printf("Interruption happen while waiting for events from remote host. %s", ioException);
                    break;
                }
            }
        }
        catch (IOException ioException) {
            System.out.printf("Failure while accepting requests from server. %s", ioException);
        }
    }

    /**
     * Publish review to all the subscribers.
     * @param json String in JSON format
     */
    public void publish(String json)  {
        int numOfSubscribers = subscribers.size();

        Review review = null;
        if(numOfSubscribers != 0) {
            review = JsonManager.fromJsonToReview(json);
        }

        if(review != null) {
            review.setJson(json);

            for (int i = 0; i < numOfSubscribers; i++) {
                SubscribeHandler<Review> subscribeHandler = subscribers.get(i);

                if (subscribeHandler != null) {
                    subscribeHandler.onEvent(review);
                }
            }
        }
    }

    /**
     * Stop publishing items to local subscribers if remote server send a request to close the connection.
     */
    public void close() {
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(Constants.DISCONNECTION_PORT);
        } catch (IOException ioException) {
            System.out.printf("Exception while creating server socket for accepting events from remote. %s", ioException);
            return;
        }

        try (
            //Listening for requests to close the connection.
            Socket socket = serverSocket.accept();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line = inStream.readLine();

            if(!Strings.isNullOrEmpty(line) && line.trim().equalsIgnoreCase(Constants.MESSAGES.CLOSE_REQUEST)) {
                outStream.println(Constants.MESSAGES.CLOSE_RESPONSE);
                isConnected = false;
                if(this.serverSocket != null) {
                    this.serverSocket.close();
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
