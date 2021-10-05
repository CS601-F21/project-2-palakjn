package cs601.project2;

import com.google.gson.Gson;
import cs601.project2.configuration.Config;
import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.*;
import cs601.project2.controllers.remote.RemoteServer;
import cs601.project2.controllers.testApplication.ReviewListener;
import cs601.project2.controllers.testApplication.Reviewer;
import cs601.project2.utils.Strings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Reads Home_And_Kitchen_5 and Apps_for_Android_5 reviews and filter both reviews by date to find out
 * which reviews are old or new.
 *
 * @author Palak Jain
 */
public class AmazonReviews {

    private Config configuration;

    public AmazonReviews() {
    }

    public static void main(String[] args) {
        AmazonReviews amazonReviews = new AmazonReviews();

        //Getting configuration file
        String configFileLocation = amazonReviews.getConfig(args);

        if(!Strings.isNullOrEmpty(configFileLocation)) {
            //Read configuration file
            amazonReviews.readConfig(configFileLocation);

            //validate if values are non-empty and files exist at a given location
            boolean isValid = amazonReviews.verifyConfig();

            if(isValid) {
                amazonReviews.processReviews();
            }
        }
    }

    /**
     * Creates subscribers and filter reviews by Unix Timestamp
     */
    public void processReviews() {
        BrokerHandler<String> reviewManager = getBroker();

        if(reviewManager != null) {
            SubscribeHandler<String> oldReviewListener = null;
            SubscribeHandler<String> newReviewListener = null;

            try {
                //Creating two subscribers
                oldReviewListener = new ReviewListener(configuration.getOldReviewsPath(), Constants.REVIEW_OPTION.OLD);
                newReviewListener = new ReviewListener(configuration.getNewReviewsPath(), Constants.REVIEW_OPTION.NEW);

                //Subscribes both subscribers
                reviewManager.subscribe(oldReviewListener);
                reviewManager.subscribe(newReviewListener);

                //Creating one thread which will look for remote subscribers
                RemoteServer remoteServer = new RemoteServer(reviewManager);
                Thread thread = new Thread(remoteServer::addRemoteSubscribers);
                thread.start();

                long startTime = System.currentTimeMillis();
                filterReviewsByUnix(reviewManager);

                //Closing the connection with remote server
                System.out.println("Closing connection");
                remoteServer.close();

                try {
                    //Waiting for threads to complete a task
                    System.out.println("Waiting for thread to finish");
                    thread.join();
                }
                catch (InterruptedException exception) {
                    StringWriter writer = new StringWriter();
                    exception.printStackTrace(new PrintWriter(writer));

                    System.out.printf("Interruption happen while waiting for threads to finish all the tasks. %s. \n", writer);
                }

                long endTime = System.currentTimeMillis();

                System.out.printf("Time took to publish all reviews: %d milliseconds.\n", endTime - startTime);
            }
            catch (IOException exception) {
                StringWriter writer = new StringWriter();
                exception.printStackTrace(new PrintWriter(writer));

                System.out.printf("An issue occurred while creating subscribers. %s. \n", writer);
            }
            finally {
                if(oldReviewListener != null) {
                    oldReviewListener.close();
                }

                if(newReviewListener != null) {
                    newReviewListener.close();
                }
            }
        }
    }

    /**
     * Spawns two threads to read reviews from file and publish to all subscribers.
     * @param reviewManager Broker object to use for publishing reviews.
     */
    public void filterReviewsByUnix(BrokerHandler<String> reviewManager) {
        //Creating two publishers
        Reviewer appliancesReviewer = new Reviewer(configuration.getAppliancesDatasetPath(), reviewManager);
        Reviewer appsReviewer = new Reviewer(configuration.getAppsDatasetPath(), reviewManager);

        //Creating two threads. Each reading reviews from two different datasets and publish review to a listener.
        Thread thread1 = new Thread(appliancesReviewer::read);
        Thread thread2 = new Thread(appsReviewer::read);

        //Starts both threads
        thread1.start();
        thread2.start();

        try {
            //Waiting for threads to complete a task
            thread1.join();
            thread2.join();
            reviewManager.shutdown();
        }
        catch (InterruptedException exception) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));

            System.out.printf("Interruption happen while waiting for threads to finish all the tasks. %s. \n", writer);
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

            configuration = gson.fromJson(reader, Config.class);
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
        else if(Strings.isNullOrEmpty(configuration.getAppliancesDatasetPath())) {
            System.out.println("No file location provided for appliances dataset.");
        }
        else if(Strings.isNullOrEmpty(configuration.getAppsDatasetPath())) {
            System.out.println("No file location provided for android apps dataset.");
        }
        else if(Strings.isNullOrEmpty(configuration.getNewReviewsPath())) {
            System.out.println("No file location provided for writing new reviews.");
        }
        else if(Strings.isNullOrEmpty(configuration.getOldReviewsPath())) {
            System.out.println("No file location provided for writing old reviews.");
        }
        else if(configuration.getBroker() == Constants.BROKER_OPTION.INVALID) {
            System.out.println("Invalid broker.");
        }
        else if(!Files.exists(Paths.get(configuration.getAppliancesDatasetPath()))) {
            System.out.printf("No file found at a location %s.\n", configuration.getAppliancesDatasetPath());
        }
        else if(!Files.exists(Paths.get(configuration.getAppsDatasetPath()))) {
            System.out.printf("No file found at a location %s.\n", configuration.getAppsDatasetPath());
        }
        else {
            flag = true;
        }

        return flag;
    }

    /**
     * Get specific broker object based on the configuration file
     * @return Broker object
     */
    public BrokerHandler<String> getBroker() {
        if(configuration.getBroker() == Constants.BROKER_OPTION.SYNCHRONIZED_ORDERED) {
            return new SynchronousOrderedBrokerHandler<>();
        }
        else if(configuration.getBroker() == Constants.BROKER_OPTION.ASYNCHRONIZED_ORDERED) {
            return new AsyncOrderedDispatchBroker<>();
        }
        else if(configuration.getBroker() == Constants.BROKER_OPTION.ASYNCHRONIZED_UNORDERED) {
            return new AsyncUnorderedDispatchBroker<>();
        }

        return null;
    }
}
