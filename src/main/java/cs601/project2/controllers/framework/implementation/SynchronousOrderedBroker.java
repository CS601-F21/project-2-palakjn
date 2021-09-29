package cs601.project2.controllers.framework.implementation;

public class SynchronousOrderedBroker<T> extends Broker<T> {

    public SynchronousOrderedBroker() {
        super();
    }

    @Override
    public synchronized void publish(T item) {
        if(running) {
            super.publish(item);
        }
    }
}
