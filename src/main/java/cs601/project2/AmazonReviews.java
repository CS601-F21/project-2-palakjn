package cs601.project2;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.*;
import cs601.project2.controllers.testApplication.ReviewListener;
import cs601.project2.controllers.testApplication.Reviewer;
import cs601.project2.models.Review;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

public class AmazonReviews {

    private String appliancesDatasetPath;
    private String appsDatasetPath;

    public AmazonReviews(String appliancesDatasetPath, String appsDatasetPath) {
        this.appliancesDatasetPath = appliancesDatasetPath;
        this.appsDatasetPath = appsDatasetPath;
    }

    public static void main(String[] args) {
        //This is temporary just for testing
        AmazonReviews amazonReviews = new AmazonReviews("Home_and_Kitchen_5.json", "Apps_for_Android_5.json");

        Broker<Review> reviewBroker = new SynchronousOrderedBroker<>();

        Subscriber<Review> oldReviewListener = new ReviewListener("OldReviews.txt", Constants.REVIEW_OPTION.OLD);
        Subscriber<Review> newReviewListener = new ReviewListener("NewReviews.txt", Constants.REVIEW_OPTION.NEW);

        reviewBroker.subscribe(oldReviewListener);
        reviewBroker.subscribe(newReviewListener);

        long startTime = System.currentTimeMillis();

        amazonReviews.filterReviewsByDate(reviewBroker);

        oldReviewListener.flush();
        newReviewListener.flush();

        long endTime = System.currentTimeMillis();

        System.out.printf("Time took to publish all reviews: %d seconds \n", TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
    }

    public void filterReviewsByDate(Broker<Review> reviewManager) {
        //Creating two publishers
        Reviewer appliancesReviewer = new Reviewer(appliancesDatasetPath, reviewManager);
        Reviewer appsReviewer = new Reviewer(appsDatasetPath, reviewManager);

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
}
