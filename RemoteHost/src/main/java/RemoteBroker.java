import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.models.Subscribers;
import cs601.project2.utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RemoteBroker<T> {
    private Subscribers<T> subscribers;
    private boolean isConnected;

    private RemoteBroker() {
        subscribers = new Subscribers<>();
        isConnected = false;
    }

    public boolean subscribe(SubscribeHandler<T> subscriber) {
        boolean flag = true;
        subscribers.add(subscriber);

        if(!isConnected) {
            //Making a connection with server only one time.

            try (
                Socket socket = new Socket(Constants.IPADDRESS, Constants.CONNECTION_PORT);
                BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
            ) {
                outStream.printf("%s %s %d", Constants.MESSAGES.SUBSCRIBE_REQUEST, Constants.IPADDRESS, Constants.MESSAGE_PORT);

                String line = inStream.readLine();

                if(!Strings.isNullOrEmpty(line) && line.equalsIgnoreCase(Constants.MESSAGES.SUBSCRIBED)) {
                    System.out.printf("Host with IpAddress %s subscribed to the server", Constants.IPADDRESS);
                    isConnected = true;
                }
                else {
                    flag = false;
                    System.out.println("Fail to subscribe to the server.");
                }
            }
            catch(IOException ioException) {
                System.out.printf("Fail to connect to server %s:%s. %s.\n", Constants.IPADDRESS, Constants.CONNECTION_PORT, ioException);
            }
        }

        return  flag;
    }
}
