import com.google.gson.Gson;
import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.controllers.testApplication.ReviewListener;
import cs601.project2.utils.Strings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * A remote Host subscribes to receive events from remote server and publish events to local subscribers.
 *
 * @author Palak Jain
 */
public class ReviewProcessor {
    private Map<String, String> configuration;

    public static void main(String[] args) {
        ReviewProcessor amazonReviews = new ReviewProcessor();

        //Getting configuration file
        String configFileLocation = amazonReviews.getConfig(args);

        if(!Strings.isNullOrEmpty(configFileLocation)) {
            //Read configuration file
            amazonReviews.readConfig(configFileLocation);

            //validate if values are non-empty and files exist at a given location
            boolean isValid = amazonReviews.verifyConfig();

            if(isValid) {
                amazonReviews.filterReviews();
            }
        }
    }

    /**
     * Creates local subscribers, connect to a remote server for all reviews
     */
    public void filterReviews() {
        RemoteBroker remoteBroker = new RemoteBroker();

        SubscribeHandler<String> oldReviewListener = null;
        SubscribeHandler<String> newReviewListener = null;

        try {
            oldReviewListener = new ReviewListener(configuration.get("oldReviewsPath"), Constants.REVIEW_OPTION.OLD);
            newReviewListener = new ReviewListener(configuration.get("newReviewsPath"), Constants.REVIEW_OPTION.NEW);

            //Subscribe local subscribers for receiving reviews from remote host.
            remoteBroker.subscribe(oldReviewListener);
            remoteBroker.subscribe(newReviewListener);

            long startTime = System.currentTimeMillis();

            //Connect to the server.
            boolean isConnected = remoteBroker.connectToServer();

            if(isConnected) {
                //Creates a thread which will wait for publishers to publish an item.
                Thread thread1 = new Thread(remoteBroker::publish);
                //Creates a thread which will check if a close connection request received from server.
                Thread thread2 = new Thread(remoteBroker::close);
                thread1.start();
                thread2.start();

                try {
                    //Waiting for threads to complete a task
                    thread1.join();
                    thread2.join();
                }
                catch (InterruptedException exception) {
                    StringWriter writer = new StringWriter();
                    exception.printStackTrace(new PrintWriter(writer));

                    System.out.printf("Interruption happen while waiting for threads to finish all the tasks. %s. \n", writer);
                }
            }

            long endTime = System.currentTimeMillis();

            System.out.printf("Time took to write down all reviews in remote host: %d milliseconds.\n", endTime - startTime);
        }
        catch (IOException exception) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));

            System.out.printf("An issue occurred while creating subscribers. %s. \n", writer);
        }
        finally {
            //Closing the subscribers
            if(oldReviewListener != null) {
                oldReviewListener.close();
            }

            if(newReviewListener != null) {
                newReviewListener.close();
            }
        }
    }

    /**
     * Read comment line arguments and return the location of the configuration file.
     * @param args Command line arguments being passed when running a program
     * @return location of configuration file if passed arguments are valid else null
     */
    public String getConfig(String[] args) {
        String configFileLocation = null;

        if(args.length == 2 &&
                args[0].equalsIgnoreCase("-config") &&
                !Strings.isNullOrEmpty(args[1])) {
            configFileLocation = args[1];
        }
        else {
            System.out.println("Invalid Arguments");
        }

        return configFileLocation;
    }

    /**
     * Parse configuration file.
     * @param configFileLocation location of configuration file
     */
    public void readConfig(String configFileLocation) {
        try (Reader reader = Files.newBufferedReader(Paths.get(configFileLocation))){
            Gson gson = new Gson();

            configuration = gson.fromJson(reader, Map.class);
        }
        catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));

            System.out.printf("Unable to open configuration file at location %s. %s. \n", configFileLocation, writer);
        }
    }

    /**
     * Verifies if the config has all the values which is needed and files exist at a given locations.
     * @return true if valid else false
     */
    public boolean verifyConfig() {
        boolean flag = false;

        if(configuration == null) {
            System.out.println("No configuration found.");
        }
        else if(!configuration.containsKey("newReviewsPath") ||
                (Strings.isNullOrEmpty(configuration.get("newReviewsPath"))))
        {
            System.out.println("No file location provided for appliances dataset.");
        }
        else if(!configuration.containsKey("oldReviewsPath") ||
                (Strings.isNullOrEmpty(configuration.get("oldReviewsPath")))){
            System.out.println("No file location provided for writing old reviews.");
        }
        else {
            flag = true;
        }

        return flag;
    }
}
