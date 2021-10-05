package cs601.project2.models;

import cs601.project2.configuration.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information of one review.
 *
 * @author Palak
 */
public class Review {

    private String reviewerID;
    private String asin;
    private String reviewerName;
    private List<Integer> helpful;
    private String reviewText;
    private float overall;
    private String summary;
    private long unixReviewTime;
    private String reviewTime;

    public Review() {
        helpful = new ArrayList<>();
    }

    /**
     * Checks if the review is old.
     * @return true if value is less than or equals to unix timestamp else false
     */
    public boolean isOld() {
        return unixReviewTime <= Constants.MAX_UNIX_TIMESTAMP;
    }

    /**
     * Checks if review is new
     * @return true if value is greater than unix timestamp else false
     */
    public boolean isNew() {
        return unixReviewTime > Constants.MAX_UNIX_TIMESTAMP;
    }
}
