package cs601.project2.controllers.testApplication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cs601.project2.models.Reviews;

/**
 * Convert objects to JSON and parse JSON to objects.
 *
 * @author Palak Jain
 */
public class JsonManager {

    /**
     * Converts Reviews object into a Json format string.
     * @param reviews Reviews object
     * @return String in JSON format
     */
    public static String toJson(Reviews reviews) {
        Gson gson = new Gson();

        String json = null;

        try {
            json = gson.toJson(reviews);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to get JSON string from Reviews object.");
        }

        return json;
    }

    /**
     * Parse JSON string into Reviews object
     * @param json Document written in JSON format
     * @return Reviews object
     */
    public static Reviews fromJson(String json) {
        Gson gson = new Gson();

        Reviews reviews = null;

        try {
            reviews = gson.fromJson(json, Reviews.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json: " + json);
        }

        return reviews;
    }
}
