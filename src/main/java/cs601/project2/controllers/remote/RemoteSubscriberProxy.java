package cs601.project2.controllers.remote;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.models.Review;
import cs601.project2.utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Subscriber receives event from publisher and send to the remote Subscriber.
 *
 * @author Palak Jain
 */
public class RemoteSubscriberProxy extends SubscribeHandler<Review> {
    private String ipAddress;
    private int port;
    private Socket socket;
    private BufferedReader inStream;
    private PrintWriter outStream;

    public RemoteSubscriberProxy(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Send json file to remote subscriber
     * @param review Review Object
     */
    @Override
    public synchronized void onEvent(Review review) {
        if(socket == null) {
            //Only making connection one time to the client
            connect();
        }

        if(socket != null && socket.isConnected()) {
            try  {
                outStream.println(review.getJson());

                String line = inStream.readLine();
                if (Strings.isNullOrEmpty(line) && line.equalsIgnoreCase(Constants.MESSAGES.RECEIVED)) {
                    System.out.println("Client didn't receive the message.");
                }
            } catch (IOException ioException) {
                System.out.printf("Failure while sending message to client: %s:%s. %s \n", ipAddress, port, ioException);
            }
        }
    }

    /**
     * Closing the connection with the client.
     */
    @Override
    public void close() {
        try {
            socket.close();
            inStream.close();
            outStream.close();
        }
        catch (IOException ioException) {
            System.out.printf("Error occurred while closing the socket and input/output streams. %s", ioException);
        }
    }

    /**
     * Tries to connect to a server one time.
     * Will re-try the connection until the server is not up.
     */
    private void connect() {
        do {
            try {
                this.socket = new Socket(ipAddress, port);
            }
            catch (IOException ioException) {
                System.out.println("Sleeping for one second");
                sleep(1000);
            }
        } while (socket == null);

        try {
            this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outStream = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException exception) {
            System.out.printf("Exception occurred while connecting to a server %s:%s. %s.\n", ipAddress, port, exception);
        }
    }

    /**
     * Causes the currently executing thread to sleep for the specified number of milliseconds
     * @param milliseconds the length of time to sleep in milliseconds
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException exception) {
            System.out.printf("Fail to sleep for %d time. %s.\n", milliseconds, exception);
        }
    }
}
