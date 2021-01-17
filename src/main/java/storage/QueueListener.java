package storage;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class QueueListener extends Thread {

    private final StorageQueueFacade queueFacade;
    private final WordCountSortedList wordCountList;
    private final Map<String, Node> wordToNodeMap = new HashMap<>();
    private final CountDownLatch readersCountDownLatch;
    private final CountDownLatch workCompleteLatch;

    @Override
    public void run() {
        try {
            while (queueFacade.isNotEmpty() || readersCountDownLatch.getCount() > 0) {
                Optional.ofNullable(queueFacade.pollWithTimeout())
                        .ifPresent(entry -> Optional.ofNullable(wordToNodeMap.get(entry.getWord()))
                                .ifPresentOrElse(node -> wordCountList.updateNode(entry.getSource(), node),
                                        () -> wordToNodeMap.put(entry.getWord(), wordCountList.createNewNode(entry))));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workCompleteLatch.countDown();
        }
    }
}