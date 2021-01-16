package storage;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class QueueListener extends Thread {

    private final BlockingQueue<QueueEntry> queue;
    private final WordCountingStorage storage;
    private final Map<String, WordCountingStorage.Node> wordToNodeMap = new HashMap<>();
    private final CountDownLatch readersCountDownLatch;
    private final CountDownLatch workCompleteLatch;

    @Override
    public void run() {
        try {
            while (!queue.isEmpty() || readersCountDownLatch.getCount() > 0) {
                Optional.ofNullable(queue.poll(100, TimeUnit.MILLISECONDS))
                        .ifPresent(entry -> Optional.ofNullable(wordToNodeMap.get(entry.getWord()))
                                .ifPresentOrElse(nodeFromMap -> nodeFromMap.increaseCount(entry.getSource()),
                                        () -> wordToNodeMap.put(entry.getWord(), storage.createNewNode(entry))));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workCompleteLatch.countDown();
        }
    }
}