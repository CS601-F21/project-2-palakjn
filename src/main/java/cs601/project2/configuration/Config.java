package cs601.project2.configuration;

import cs601.project2.utils.Strings;

/**
 * Configuration file holds the inputs to the program.
 *
 * @author Palak Jain
 */
public class Config {

    private String appliancesDatasetPath;
    private String appsDatasetPath;
    private String newReviewsPath;
    private String oldReviewsPath;
    private String broker;

    /**
     * Get a file location of Home_And_Kitchen_5 review dataset
     * @return file location
     */
    public String getAppliancesDatasetPath() {
        return appliancesDatasetPath;
    }

    /**
     * Get a file location of Apps_for_Android_5 review dataset
     * @return file location
     */
    public String getAppsDatasetPath() {
        return appsDatasetPath;
    }

    /**
     * Get a file location where to write new reviews.
     * @return file location
     */
    public String getNewReviewsPath() {
        return newReviewsPath;
    }

    /**
     * Gets a file location where to write old reviews.
     * @return
     */
    public String getOldReviewsPath() {
        return oldReviewsPath;
    }

    /**
     * Get specific broker option to use for the run
     * @return Broker option
     */
    public Constants.BROKER_OPTION getBroker() {

        if(!Strings.isNullOrEmpty(broker)) {
            if (broker.equalsIgnoreCase("SynchronousOrderedBroker")) {
                return Constants.BROKER_OPTION.SYNCHRONIZED_ORDERED;
            } else if (broker.equalsIgnoreCase("AsynchronousOrderedBroker")) {
                return Constants.BROKER_OPTION.ASYNCHRONIZED_ORDERED;
            } else if (broker.equalsIgnoreCase("AsynchronousUnorderedBroker")) {
                return Constants.BROKER_OPTION.ASYNCHRONIZED_UNORDERED;
            }
        }

        return Constants.BROKER_OPTION.INVALID;
    }
}
