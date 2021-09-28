package cs601.project2.models;

public class BlockingQueue<T> {

    private T[] items;
    private int start;
    private int end;
    private int size;

    public BlockingQueue(int size) {
        this.items = (T[]) new Object[size];
        this.start = 0;
        this.end = -1;
        this.size = 0;
    }

    public synchronized void put(T item) {
        while(size == items.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int next = (end+1)%items.length;
        items[next] = item;
        end = next;
        size++;
        if(size == 1) {
            this.notifyAll();
        }

        String name = Thread.currentThread().getName();
    }

    public synchronized T take() {
        while(size == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        T item = items[start];
        start = (start+1)%items.length;
        size--;
        /*
        If the queue was previously full and a new slot has now opened
        notify any waiters in the put method.
         */
        if(size == items.length-1) {
            this.notifyAll();
        }

        return item;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }
}
