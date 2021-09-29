package cs601.project2.controllers.framework.implementation;

import cs601.project2.controllers.framework.Subscriber;

/**
 * A subscriber which will subscribe for events from publisher.
 *
 * @author Palak Jain
 * @param <T>
 */
public abstract class SubscribeHandler<T> implements Subscriber<T> {

    /**
     * Closing all the instances of objects being used.
     */
    public abstract void close();
}
