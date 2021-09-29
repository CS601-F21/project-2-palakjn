package cs601.project2.controllers.testApplication;

import cs601.project2.controllers.framework.implementation.Broker;
import cs601.project2.models.Review;
import cs601.project2.view.JsonManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reviewer {

    private Broker<Review> reviewManager;
    private String fileLocation;

    public Reviewer(String fileLocation, Broker<Review> reviewManager) {
        this.fileLocation = fileLocation;
        this.reviewManager = reviewManager;
    }

    public void read() {
        try(BufferedReader br = Files.newBufferedReader(Paths.get(fileLocation), StandardCharsets.ISO_8859_1)) {
            String line = br.readLine();

            while (line != null) {
                Review review = JsonManager.fromJson(line);

                if(review != null) {
                    reviewManager.publish(review);
                }

                line = br.readLine();
            }
        }
        catch (IOException ioException) {
            StringWriter writer = new StringWriter();
            ioException.printStackTrace(new PrintWriter(writer));

            System.out.printf("An error occurred while accessing file at a location %s. %s. \n", fileLocation, writer);
        }
    }
}
