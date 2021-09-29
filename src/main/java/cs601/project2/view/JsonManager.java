package cs601.project2.view;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cs601.project2.models.Review;

/**
 * Convert objects to JSON and parse JSON to objects.
 *
 * @author Palak Jain
 */
public class JsonManager {

    /**
     * Converts Review object into a Json format string.
     * @param review Review object
     * @return String in JSON format
     */
    public static String toJson(Review review) {
        Gson gson = new Gson();

        String json = null;

        try {
            json = gson.toJson(review);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to get JSON string from Review object.");
        }

        return json;
    }

    /**
     * Parse JSON string into Review object
     * @param json Document written in JSON format
     * @return Review object
     */
    public static Review fromJson(String json) {
        Gson gson = new Gson();

        Review review = null;

        try {
            review = gson.fromJson(json, Review.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json: " + json);
        }

        return review;
    }
}
