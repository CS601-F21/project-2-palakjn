package cs601.project2.controllers.framework.implementation;

import cs601.project2.models.BlockingQueue;

public class AsynchronousOrderedBroker<T> extends Broker<T> {

    private BlockingQueue<T> queue;
    private Thread thread;

    public AsynchronousOrderedBroker() {
        super();
        queue = new BlockingQueue<>(10000); //Ques: What should be the size?
        thread = new Thread(this::process);
        thread.start();
        this.running = true;
    }

    /**
     * Adding an item to the blocking queue and will return to the caller immediately
     * @param item An item to be published to all the subscribers.
     */
    @Override
    public void publish(T item) {
        if(running) {
            queue.put(item);
        }
    }

    /**
     *
     */
    public void process() {
        T item = null;

        do {
            item = queue.poll(1000); //Waiting for one second

            if(item != null) {
                super.publish(item);
            }
        } while (item != null);
    }

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
