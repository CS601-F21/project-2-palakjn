package cs601.project2.controllers.testApplication;

import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.Subscriber;
import cs601.project2.models.Review;
import cs601.project2.view.JsonManager;
import cs601.project2.view.OutputManager;

public class ReviewListener extends Subscriber<Review> {
    private String fileLocation;
    private Constants.REVIEW_OPTION reviewOption;

    public ReviewListener(String fileLocation, Constants.REVIEW_OPTION reviewOption) {
        this.fileLocation = fileLocation;
        this.reviewOption = reviewOption;
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
                OutputManager.writeToFile(fileLocation, json);
            }
        }
    }
}
