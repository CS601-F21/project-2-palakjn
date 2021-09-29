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
}

