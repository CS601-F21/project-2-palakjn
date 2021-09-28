package cs601.project2.controllers.framework.implementation;

import cs601.project2.controllers.framework.IBroker;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broker<T> implements IBroker<T> {

    private CopyOnWriteArrayList<Subscriber<T>> subscribers;
//    private Subscribers<T> subscribers;
    protected boolean running;

    public Broker(){
//        subscribers = new Subscribers<>();
        subscribers = new CopyOnWriteArrayList<>();
        running = true;
    }

    @Override
    public void publish(T item)  {
//        int numOfSubscribers = subscribers.size();
//
//        for(int i = 0; i < numOfSubscribers; i++) {
//            Subscriber<T> subscriber = subscribers.get(i);
//
//            if(subscriber != null) {
//                subscriber.onEvent(item);
//            }
//        }

        Iterator<Subscriber<T>> subscriberIterator = subscribers.iterator();

        while (subscriberIterator.hasNext()) {
            Subscriber<T> subscriber = subscriberIterator.next();
            subscriber.onEvent(item);
        }
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        if(running) { //Ques: Do we need to allow adding subscribers when running is set to false?
            subscribers.add(subscriber);
        }
    }

    @Override
    public void shutdown() {
        this.running = false;
    }
}
