package cs601.project2;

import com.google.gson.Gson;
import cs601.project2.configuration.Config;
import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.*;
import cs601.project2.controllers.testApplication.ReviewListener;
import cs601.project2.controllers.testApplication.Reviewer;
import cs601.project2.models.Review;
import cs601.project2.utils.Strings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

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

    public void processReviews() {
        Broker<Review> reviewManager = getBroker();

        if(reviewManager != null) {

            try (
                 //Creating two subscribers
                 Subscriber<Review> oldReviewListener = new ReviewListener(configuration.getOldReviewsPath(), Constants.REVIEW_OPTION.OLD);
                 Subscriber<Review> newReviewListener = new ReviewListener(configuration.getNewReviewsPath(), Constants.REVIEW_OPTION.NEW);
                 ) {

                //Subscribes both subscribers
                reviewManager.subscribe(oldReviewListener);
                reviewManager.subscribe(newReviewListener);

                long startTime = System.currentTimeMillis();

                filterReviewsByUnix(reviewManager);

                long endTime = System.currentTimeMillis();

                System.out.printf("Time took to publish all reviews: %d seconds.\n", TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
            }
            catch (Exception exception) {
                //Auto-closable block in ReviewListener requires catching java.lang.Exception
                StringWriter writer = new StringWriter();
                exception.printStackTrace(new PrintWriter(writer));

                System.out.printf("An issue occurred while creating subscribers. %s. \n", writer);
            }
        }
    }

    public void filterReviewsByUnix(Broker<Review> reviewManager) {
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

    public String getConfig(String[] args) {
        String configFileLocation = null;

        if(args.length == 2 &&
                args[0].equalsIgnoreCase("-config") &&
                !args[1].isEmpty() &&
                !args[1].isBlank()) {
            configFileLocation = args[1];
        }
        else {
            System.out.println("Invalid Arguments");
        }

        return configFileLocation;
    }

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

    public Broker<Review> getBroker() {
        if(configuration.getBroker() == Constants.BROKER_OPTION.SYNCHRONIZED_ORDERED) {
            return new SynchronousOrderedBroker<>();
        }
        else if(configuration.getBroker() == Constants.BROKER_OPTION.ASYNCHRONIZED_ORDERED) {
            return new AsynchronousOrderedBroker<>();
        }
        else if(configuration.getBroker() == Constants.BROKER_OPTION.ASYNCHRONIZED_UNORDERED) {
            return new AsynchronousUnorderedBroker<>();
        }

        return null;
    }
}
