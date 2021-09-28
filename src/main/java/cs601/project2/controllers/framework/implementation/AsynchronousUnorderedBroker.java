package cs601.project2.controllers.framework.implementation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousUnorderedBroker<T> extends Broker<T> {

    private ExecutorService threadPool;

    public AsynchronousUnorderedBroker() {
        this.threadPool = Executors.newFixedThreadPool(30);
    }

    @Override
    public void publish(T item) {
        if(running) {
           threadPool.execute(() -> AsynchronousUnorderedBroker.super.publish(item));
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();

        threadPool.shutdown();
        //Ques: Do we need to do this? As part of the project, we need to ensure that threadpool should not
        //let new tasks to take and have to wait until existing tasks not being completed
        //If we have to wait then after timeout, what to do? Throw an exception?

//        try {
//            if(!threadPool.awaitTermination(2, TimeUnit.MINUTES)) {
//                threadPool.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
