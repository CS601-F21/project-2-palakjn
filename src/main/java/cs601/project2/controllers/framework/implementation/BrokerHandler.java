package cs601.project2.controllers.framework.implementation;

import cs601.project2.controllers.framework.Broker;
import cs601.project2.models.Subscribers;

/**
 * Publish items from all publishers to all subscribers.
 *
 * @author Palak Jain
 * @param <T>
 */
public class BrokerHandler<T> implements Broker<T> {

    private Subscribers<T> subscribers;
    protected boolean running;

    public BrokerHandler(){
        subscribers = new Subscribers<>();
        running = true;
    }

    /**
     * Publish an item to all subscribers.
     * Call will not return until all subscribers received an item.     *
     * @param item An item to publish to all subscribers.
     */
    @Override
    public void publish(T item)  {
        int numOfSubscribers = subscribers.size();

        for(int i = 0; i < numOfSubscribers; i++) {
            SubscribeHandler<T> subscribeHandler = subscribers.get(i);

            if(subscribeHandler != null) {
                subscribeHandler.onEvent(item);
            }
        }
    }

    /**
     * Subscribes a new subscriber
     * @param subscribeHandler Subscriber
     */
    @Override
    public void subscribe(SubscribeHandler<T> subscribeHandler) {
        if(running) {
            subscribers.add(subscribeHandler);
        }
    }

    /**
     * Will not let new items to be added for a task to publish
     */
    @Override
    public void shutdown() {
        this.running = false;
    }
}
