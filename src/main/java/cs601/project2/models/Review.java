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
    private String json;

    public Review() {
        helpful = new ArrayList<>();
    }

    /**
     * Setting JSON of the review object.
     * @param line String in JSON format
     */
    public void setJson(String line) {
        json = line;
    }

    /**
     * Gets JSON representation of the object
     * @return A string in JSON format
     */
    public String getJson() {
        return json;
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
