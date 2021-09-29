package cs601.project2.controllers.framework.implementation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsynchronousUnorderedBroker<T> extends Broker<T> {

    private ExecutorService threadPool;

    public AsynchronousUnorderedBroker() {
        this.threadPool = Executors.newFixedThreadPool(30);
    }

    @Override
    public void publish(T item) {
        if(!running) {
            //Not accepting new items
            return;
        }
        threadPool.execute(() -> AsynchronousUnorderedBroker.super.publish(item));
    }

    @Override
    public void shutdown() {
        super.shutdown();

        threadPool.shutdown();

        try {
            if(!threadPool.awaitTermination(2, TimeUnit.MINUTES)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
