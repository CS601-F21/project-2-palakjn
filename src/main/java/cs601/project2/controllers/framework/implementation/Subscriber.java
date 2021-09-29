package cs601.project2.controllers.framework.implementation;

import cs601.project2.controllers.framework.ISubscriber;

public abstract class Subscriber<T> implements ISubscriber<T> {

    @Override
    public void onEvent(T item) {

    }

    public abstract void flush();
}
