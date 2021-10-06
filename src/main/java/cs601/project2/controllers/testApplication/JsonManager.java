package cs601.project2.controllers.testApplication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cs601.project2.configuration.Config;
import cs601.project2.models.Review;

import java.io.Reader;
import java.util.Map;

/**
 * Convert objects to JSON and parse JSON to objects.
 *
 * @author Palak Jain
 */
public class JsonManager {

    /**
     * Parse JSON string into Review object
     * @param json Document written in JSON format
     * @return Review object
     */
    public static Review fromJsonToReview(String json) {
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

    /**
     * Parse JSON string into Configuration object
     * @param reader Reader object
     * @return Config object
     */
    public static Config fromJsonToConfig(Reader reader) {
        Gson gson = new Gson();

        Config config = null;

        try {
            config = gson.fromJson(reader, Config.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json");
        }

        return config;
    }

    /**
     * Parse JSON string into Map<String,String> object
     * @param reader Reader object
     * @return Map
     */
    public static Map<String,String> fromJsonToMap(Reader reader) {
        Gson gson = new Gson();

        Map<String,String> map = null;

        try {
            map = gson.fromJson(reader, Map.class);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json.");
        }

        return map;
    }
}
