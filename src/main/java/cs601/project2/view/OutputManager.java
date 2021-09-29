package cs601.project2.view;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class OutputManager {

    /**
     * Appends a line to the given file location.
     * @param fileLocation Absolute or relative location of the file.
     * @param line A string
     */
    public static void writeToFile(String fileLocation, String line) {

        //Create if file doesn't exist and append to a file instead of overwriting the content.
        StandardOpenOption[] options = new StandardOpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.APPEND};

        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(fileLocation), StandardCharsets.ISO_8859_1, options)) {
            bw.write(line);
            bw.newLine();
        }
        catch (IOException ioException) {
            System.out.printf("Unable to write to a file %s. %s\n", fileLocation, ioException.getMessage());
        }
    }
}
