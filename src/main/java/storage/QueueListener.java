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
    private final CountDownLatch readersCountDown;
    private final CountDownLatch workCompleteLatch;

    @Override
    public void run() {
        try {
            while (!queue.isEmpty() || readersCountDown.getCount() > 0) {
                Optional.ofNullable(queue.poll(10, TimeUnit.MILLISECONDS))
                        .ifPresent(entry -> {
                            System.out.println(queue.size());
                            Optional.ofNullable(wordToNodeMap.get(entry.getWord()))
                                    .ifPresentOrElse(nodeFromMap -> updateNode(entry.getSource(), nodeFromMap),
                                            () -> wordToNodeMap.put(entry.getWord(), storage.createNewNode(entry)));
                        });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workCompleteLatch.countDown();
        }
    }

    private void updateNode(String source, WordCountingStorage.Node node) {
        updateCount(source, node);
        Optional.ofNullable(node.getLargerNode())
                .filter(largerNode -> node.getTotalCount() > largerNode.getTotalCount())
                .ifPresent(largerNode -> node.swapWithLargerNode());
    }

    private void updateCount(String source, WordCountingStorage.Node node) {
        Long count = node.getSourceToCountMap().getOrDefault(source, 0L);
        count++;
        node.getSourceToCountMap().put(source, count);
    }
}