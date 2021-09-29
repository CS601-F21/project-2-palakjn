package cs601.project2.controllers.framework.implementation;

import cs601.project2.models.BlockingQueue;

/**
 * Asynchronously publishing items in an order to Subscribers.
 *
 * @author Palak Jain
 * @param <T>
 */
public class AsyncOrderedDispatchBroker<T> extends BrokerHandler<T> {

    private BlockingQueue<T> queue;
    private Thread thread;

    public AsyncOrderedDispatchBroker() {
        super();
        queue = new BlockingQueue<>(10000);
        thread = new Thread(this::process);
        thread.start();
        this.running = true;
    }

    /**
     * Adding an item to the blocking queue and will return to the caller immediately
     * @param item An item to publish to all the subscribers.
     */
    @Override
    public void publish(T item) {
        if(running) {
            queue.put(item);
        }
    }

    /**
     * Publish next available item in a queue.
     * Waiting for 1 second.
     */
    public void process() {

        while (running) {
            T item = queue.poll(1000);

            if(item != null) {
                super.publish(item);
            }
        }

        while (!queue.isEmpty()) {
            T item = queue.poll(1000);

            if(item != null) {
                super.publish(item);
            }
        }
    }

    /**
     * Not accepting new items and waiting for thread to finish existing tasks.
     */
    @Override
    public void shutdown() {
        super.shutdown();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
