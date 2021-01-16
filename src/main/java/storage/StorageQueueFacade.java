package storage;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StorageQueueFacade {

    private static final int QUEUE_CAPACITY = 10000;

    @Getter(AccessLevel.PACKAGE)
    private final BlockingQueue<QueueEntry> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    public void put(QueueEntry queueEntry) throws InterruptedException {
        queue.put(queueEntry);
    }

    boolean isNotEmpty() {
        return !queue.isEmpty();
    }

    QueueEntry pollWithTimeout() throws InterruptedException {
        return queue.poll(100, TimeUnit.MILLISECONDS);
    }
}
