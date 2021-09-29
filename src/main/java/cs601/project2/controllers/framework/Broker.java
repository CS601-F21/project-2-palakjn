package cs601.project2.controllers.framework;

import cs601.project2.controllers.framework.implementation.SubscribeHandler;

/**
 * Broker has a responsibility to publish all the items from publishers to all the subscribers.
 *
 * @author Palak Jain
 * @param <T>
 */
public interface Broker<T> {
    /**
     * Called by a publisher to publish a new item. The
     * item will be delivered to all current subscribers.
     *
     * @param item
     */
    public void publish(T item);

    /**
     * Called once by each subscribeHandler. SubscribeHandler will be
     * registered and receive notification of all future
     * published items.
     *
     * @param subscribeHandler
     */
    public void subscribe(SubscribeHandler<T> subscribeHandler);

    /**
     * Indicates this broker should stop accepting new
     * items to be published and shut down all threads.
     * The method will block until all items that have been
     * published have been delivered to all subscribers.
     */
    public void shutdown();
}
