package cs601.project2.configuration;

/**
 * The class holding constant values. If there is a change in hardcoded strings, there will be only one place to change it.
 *
 * @author Palak Jain
 */
public class Constants {
    public enum REVIEW_OPTION {
        NEW,
        OLD
    }

    public enum BROKER_OPTION {
        SYNCHRONIZED_ORDERED,
        ASYNCHRONIZED_ORDERED,
        ASYNCHRONIZED_UNORDERED,
        INVALID
    }

    public static final int MAX_UNIX_TIMESTAMP = 1362268800;
    public static final String IPADDRESS = "localhost";
    public static final int CONNECTION_PORT = 3032;
    public static final int MESSAGE_PORT = 3034;
    public static final int DISCONNECTION_PORT = 3036;

    public static class MESSAGES {
        public static final String SUBSCRIBED = "Subscribed!";
        public static final String INVALID_REQUEST = "Invalid request. Try Again!";
        public static final String SUBSCRIBE_REQUEST = "Subscribe";
        public static final String END_TOKEN = "EOT";
        public static final String RECEIVED = "Received!";
        public static final String CLOSE_REQUEST = "Close the connection.";
        public static final String CLOSE_RESPONSE = "Closed!";
    }
}

