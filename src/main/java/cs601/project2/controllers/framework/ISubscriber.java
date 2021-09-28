package cs601.project2.controllers.framework;

public interface ISubscriber<T> {

    /**
     * Called by the Broker when a new item
     * has been published.
     * @param item
     */
    public void onEvent(T item);
}
