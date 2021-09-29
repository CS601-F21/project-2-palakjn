package cs601.project2.configuration;

import com.google.gson.annotations.SerializedName;
import cs601.project2.utils.Strings;

public class Config {

    @SerializedName(value = "input1")
    private String appliancesDatasetPath;

    @SerializedName(value = "input2")
    private String appsDatasetPath;

    @SerializedName(value = "newReviewsOutputFile")
    private String newReviewsPath;

    @SerializedName(value = "oldReviewsOutputFile")
    private String oldReviewsPath;

    private String broker;

    public String getAppliancesDatasetPath() {
        return appliancesDatasetPath;
    }

    public String getAppsDatasetPath() {
        return appsDatasetPath;
    }

    public String getNewReviewsPath() {
        return newReviewsPath;
    }

    public String getOldReviewsPath() {
        return oldReviewsPath;
    }

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
