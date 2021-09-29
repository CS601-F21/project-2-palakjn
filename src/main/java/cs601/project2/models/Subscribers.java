package cs601.project2.models;

import cs601.project2.controllers.framework.implementation.SubscribeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe data structure which will hold Subscribers object.
 *
 * @param <T>
 * @author Palak Jain
 */
public class Subscribers<T> {
    private List<SubscribeHandler<T>> subscribeHandlers;
    private ReentrantReadWriteLock lock;

    public Subscribers(){
        this.subscribeHandlers = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Adding subscribeHandler to the list.
     * @param subscribeHandler The one who wants to subscribe.
     */
    public void add(SubscribeHandler<T> subscribeHandler) {
        this.lock.writeLock().lock();

        try {
            this.subscribeHandlers.add(subscribeHandler);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Gets the total number of subscribeHandlers.
     * @return size of an array
     */
    public int size() {
        this.lock.readLock().lock();

        try {
            return this.subscribeHandlers.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Gets the subscriber object at a given index
     * @param index location of an item in an array
     * @return SubscribeHandler object if a given index is less than the size of an array else null
     */
    public SubscribeHandler<T> get(int index) {
        this.lock.readLock().lock();

        try {
            if(index < this.subscribeHandlers.size()) {
                return subscribeHandlers.get(index);
            }
            else {
                return null;
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
