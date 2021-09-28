package cs601.project2.controllers.testApplication;

import cs601.project2.controllers.framework.implementation.Subscriber;
import cs601.project2.models.Reviews;

public class OldReviewsListener extends Subscriber<Reviews> {

    private String fileLocation;

    public OldReviewsListener(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    public void onEvent(Reviews reviews) {

    }
}
