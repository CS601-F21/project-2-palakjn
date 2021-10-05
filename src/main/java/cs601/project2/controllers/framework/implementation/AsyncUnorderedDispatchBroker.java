package cs601.project2.controllers.framework.implementation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronously publishing items to all subscribers without ensuring an order.
 *
 * @author Palak Jain
 * @param <T>
 */
public class AsyncUnorderedDispatchBroker<T> extends BrokerHandler<T> {

    private ExecutorService threadPool;

    public AsyncUnorderedDispatchBroker() {
        this.threadPool = Executors.newFixedThreadPool(30);
    }

    /**
     * Adding an item to a queue for ThreadPool to publish next.
     * @param item An item to publish to all the subscribers.
     */
    @Override
    public void publish(T item) {
        if(!running) {
            //Not accepting new items
            return;
        }
        threadPool.execute(() -> AsyncUnorderedDispatchBroker.super.publish(item));
    }

    /**
     * Not accepting new items and waiting for threadPool to shut down existing tasks.
     */
    @Override
    public void shutdown() {
        running = false;

        threadPool.shutdown();

        try {
            if(!threadPool.awaitTermination(2, TimeUnit.MINUTES)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.shutdown();
    }
}
