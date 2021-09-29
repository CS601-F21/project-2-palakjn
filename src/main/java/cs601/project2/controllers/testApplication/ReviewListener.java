package cs601.project2.controllers.testApplication;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.Subscriber;
import cs601.project2.models.Review;
import cs601.project2.view.JsonManager;

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
    private int maxValue = 1000;
    private BufferedWriter bufferedWriter;

    public ReviewListener(String fileLocation, Constants.REVIEW_OPTION reviewOption) {
        this.fileLocation = fileLocation;
        this.reviewOption = reviewOption;
        this.reviewList = new ArrayList<>();
        try {
            this.bufferedWriter = Files.newBufferedWriter(Paths.get(fileLocation), StandardCharsets.ISO_8859_1);
        }
        catch (IOException ioException) {
            //TODO
        }
    }

    @Override
    public void onEvent(Review review) {
        if(review == null) {
            //If reviews object is null then return
            return;
        }

        if((reviewOption == Constants.REVIEW_OPTION.NEW && review.isNew()) ||
                reviewOption == Constants.REVIEW_OPTION.OLD && review.isOld()) {
            String json = JsonManager.toJson(review);

            if(json != null) {
                try{
                    bufferedWriter.write(json);
                    bufferedWriter.newLine();
                }
                catch (IOException ioException) {
                    System.out.printf("Unable to write to a file %s. %s\n", fileLocation, ioException.getMessage());
                }
            }
        }
    }

    public void flush() {
        try {
            bufferedWriter.close();
        }
        catch (IOException ioException) {

        }
    }
}
