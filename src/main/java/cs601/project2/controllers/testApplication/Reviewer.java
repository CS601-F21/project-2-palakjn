package cs601.project2.controllers.testApplication;

import cs601.project2.controllers.framework.implementation.BrokerHandler;
import cs601.project2.models.Review;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Publish review to all subscribers.
 *
 * @author Palak Jain
 */
public class Reviewer {

    private BrokerHandler<String> reviewManager;
    private String fileLocation;

    public Reviewer(String fileLocation, BrokerHandler<String> reviewManager) {
        this.fileLocation = fileLocation;
        this.reviewManager = reviewManager;
    }

    /**
     * Reads file line by line and requests manager to publish review to all subscribers.
     */
    public void read() {
        try(BufferedReader br = Files.newBufferedReader(Paths.get(fileLocation), StandardCharsets.ISO_8859_1)) {
            String line = br.readLine();

            while (line != null) {
                reviewManager.publish(line);
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
