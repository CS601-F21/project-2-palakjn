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

public class RemoteSubscriberProxy extends SubscribeHandler<Review> {
    private String ipAddress;
    private int port;
//    private Socket socket;
//    private BufferedReader inStream;
//    private PrintWriter outStream;

    public RemoteSubscriberProxy(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
//        try {
//            this.socket = new Socket(ipAddress, port);
//            this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            this.outStream = new PrintWriter(socket.getOutputStream(), true);
//        }
//        catch(IOException exception) {
//            System.out.printf("Exception occurred while connecting to a server %s:%s. %s.\n", ipAddress, port, exception);
//        }
    }

    @Override
    public synchronized void onEvent(Review review) {
        try (
            Socket socket = new Socket(ipAddress, port);
            BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true)
            ) {
            outStream.println(review.getJson());
            outStream.println(Constants.MESSAGES.END_TOKEN);

            String line = inStream.readLine();
            if(!Strings.isNullOrEmpty(line) && line.equalsIgnoreCase(Constants.MESSAGES.RECEIVED)) {
                System.out.println("Client received the message");
            }
            else {
                System.out.println("Client didn't receive the message.");
            }
        }
        catch (IOException ioException) {
            System.out.printf("Failure while sending message to client: %s:%s. %s \n", ipAddress, port, ioException);
        }
    }

    @Override
    public void close() {
//        try {
//            socket.close();
//            inStream.close();
//            outStream.close();
//        }
//        catch (IOException ioException) {
//            System.out.printf("Error occurred while closing the socket and input/output streams. %s", ioException);
//        }
    }
}
