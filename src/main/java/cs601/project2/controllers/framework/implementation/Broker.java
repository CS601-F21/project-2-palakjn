package cs601.project2.controllers.framework.implementation;

import cs601.project2.controllers.framework.IBroker;
import cs601.project2.models.Subscribers;

public class Broker<T> implements IBroker<T> {

    private Subscribers<T> subscribers;
    protected boolean running;

    public Broker(){
        subscribers = new Subscribers<>();
        running = true;
    }

    @Override
    public void publish(T item)  {
        int numOfSubscribers = subscribers.size();

        for(int i = 0; i < numOfSubscribers; i++) {
            Subscriber<T> subscriber = subscribers.get(i);

            if(subscriber != null) {
                subscriber.onEvent(item);
            }
        }
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        if(running) {
            subscribers.add(subscriber);
        }
    }

    @Override
    public void shutdown() {
        this.running = false;
    }
}
