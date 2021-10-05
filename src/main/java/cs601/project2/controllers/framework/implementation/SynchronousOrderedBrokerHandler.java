package cs601.project2.controllers.framework.implementation;

/**
 * Synchronously publishing items in an order to all subscribers.
 *
 * @author Palak Jain
 * @param <T>
 */
public class SynchronousOrderedBrokerHandler<T> extends BrokerHandler<T> {

    public SynchronousOrderedBrokerHandler() {
        super();
    }

    /**
     * Publish an item and wait until SubscribeHandler receive it.
     * @param item An item to publish to all the subscribers.
     */
    @Override
    public synchronized void publish(T item) {
        if(running) {
            super.publish(item);
        }
    }

    @Override
    public void shutdown() {
        running = false;

        super.shutdown();
    }
}
