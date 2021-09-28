package cs601.project2.models;

import java.util.ArrayList;
import java.util.List;

public class Reviews {

    private String reviewerID;
    private String asin;
    private String reviewerName;
    private List<Integer> helpful;
    private String reviewText;
    private float overall;
    private String summary;
    private long unixReviewTime;
    private String reviewTime;

    public Reviews() {
        helpful = new ArrayList<>();
    }

    public boolean isOld() {
        return unixReviewTime <= 1362268800;
    }

    public boolean isNew() {
        return unixReviewTime > 1362268800;
    }
}
