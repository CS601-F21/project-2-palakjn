package cs601.project2.controllers.framework;

/**
 * Subscriber which listens for new events.
 *
 * @author Palak Jain
 * @param <T>
 */
public interface Subscriber<T> {

    /**
     * Called by the BrokerHandler when a new item
     * has been published.
     * @param item
     */
    public void onEvent(T item);
}
