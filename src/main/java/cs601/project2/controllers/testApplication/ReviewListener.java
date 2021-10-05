package cs601.project2.controllers.testApplication;

import com.google.gson.Gson;
import cs601.project2.configuration.Constants;
import cs601.project2.controllers.framework.implementation.SubscribeHandler;
import cs601.project2.models.Review;
import cs601.project2.utils.Strings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A subscriber who listens for a review to write to a file from a publisher.
 *
 * @author Palak Jain
 */
public class ReviewListener extends SubscribeHandler<String> {
    private String fileLocation;
    private Constants.REVIEW_OPTION reviewOption;
    private BufferedWriter bufferedWriter;

    public ReviewListener(String fileLocation, Constants.REVIEW_OPTION reviewOption) throws IOException{
        this.fileLocation = fileLocation;
        this.reviewOption = reviewOption;
        //Deleting file if already exists
        if(Files.exists(Paths.get(fileLocation))) {
            Files.delete(Paths.get(fileLocation));
        }

        this.bufferedWriter = Files.newBufferedWriter(Paths.get(fileLocation), StandardCharsets.ISO_8859_1);
    }

    /**
     * Writing reviews based on whether it is a new or old review to a file.
     *
     * @param json json object to write to a file
     */
    @Override
    public void onEvent(String json) {
        if(Strings.isNullOrEmpty(json)) {
            //If reviews object is null then return
            return;
        }

        Gson gson = new Gson();
        Review review = gson.fromJson(json, Review.class);

        if((reviewOption == Constants.REVIEW_OPTION.NEW && review.isNew()) ||
                reviewOption == Constants.REVIEW_OPTION.OLD && review.isOld()) {

            try{
                bufferedWriter.write(json);
                bufferedWriter.newLine();
            }
            catch (IOException ioException) {
                StringWriter writer = new StringWriter();
                ioException.printStackTrace(new PrintWriter(writer));

                System.out.printf("Unable to write to a file %s. %s\n", fileLocation, writer);
            }
        }
    }

    /**
     * Closing BufferedWriter Instance.
     */
    public void close() {
        try {
            bufferedWriter.close();
        }
        catch (IOException ioException) {
            System.out.printf("Unable to write to a file %s. %s\n", fileLocation, ioException.getMessage());
        }
    }
}
