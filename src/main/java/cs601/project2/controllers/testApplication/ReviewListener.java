package cs601.project2.controllers.testApplication;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.Subscriber;
import cs601.project2.models.Review;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReviewListener extends Subscriber<Review> {
    private String fileLocation;
    private Constants.REVIEW_OPTION reviewOption;
    private List<Review> reviewList;
    private BufferedWriter bufferedWriter;
    private int newReviewsCount;
    private int oldReviewsCount;

    public ReviewListener(String fileLocation, Constants.REVIEW_OPTION reviewOption) throws IOException{
        this.fileLocation = fileLocation;
        this.reviewOption = reviewOption;
        this.reviewList = new ArrayList<>();
        if(Files.exists(Paths.get(fileLocation))) {
            Files.delete(Paths.get(fileLocation));
        }

        this.bufferedWriter = Files.newBufferedWriter(Paths.get(fileLocation), StandardCharsets.ISO_8859_1);
    }

    @Override
    public synchronized void onEvent(Review review) {
        if(review == null) {
            //If reviews object is null then return
            return;
        }

        if((reviewOption == Constants.REVIEW_OPTION.NEW && review.isNew()) ||
                reviewOption == Constants.REVIEW_OPTION.OLD && review.isOld()) {

            try{
                bufferedWriter.write(review.getJson());
                bufferedWriter.newLine();

                if(reviewOption == Constants.REVIEW_OPTION.OLD) {
                    oldReviewsCount++;
                }
                else {
                    newReviewsCount++;
                }
            }
            catch (IOException ioException) {
                System.out.printf("Unable to write to a file %s. %s\n", fileLocation, ioException.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {
        bufferedWriter.close();
    }

    @Override
    public int getNewReviewCount() {
        return newReviewsCount;
    }

    @Override
    public int getOldReviewsCount() {
        return oldReviewsCount;
    }
}
