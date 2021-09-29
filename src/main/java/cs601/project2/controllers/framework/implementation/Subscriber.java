package cs601.project2.controllers.framework.implementation;

import cs601.project2.controllers.framework.ISubscriber;

public abstract class Subscriber<T> implements ISubscriber<T>, AutoCloseable {

    @Override
    public void onEvent(T item) {

    }
}
