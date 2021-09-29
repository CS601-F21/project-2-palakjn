package cs601.project2.models;

/**
 * Thread-safe queue which allows caller to do LIFO operation on it.
 *
 * @ref: https://github.com/CS601-F21/code-examples/tree/main/Threads/src/main/java/concurrent/CS601BlockingQueue.java
 * @author Palak Jain
 * @param <T>
 */
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

    /**
     * Add an element to a queue if not full.
     * If full then wait until a thread takes out an item from a queue.
     *
     * @param item An item to add.
     */
    public synchronized void put(T item) {
        while(size == items.length) {
            try {
                //Will wait until another thread takes out an item from a queue.
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
            //Notify thread who is waiting for next item to take.
            this.notifyAll();
        }
    }

    /**
     * Will take first available item from a queue.
     * @param time time in milliseconds
     * @return An item or null if no items found in a queue after waiting for a specified mentioned time
     */
    public synchronized T poll(long time) {
        while(size == 0) {
            try {
                //waiting for certain amount of time.
                this.wait(time);

                //If still the size is empty then returns null
                if(size == 0) {
                    return null;
                }
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
}
