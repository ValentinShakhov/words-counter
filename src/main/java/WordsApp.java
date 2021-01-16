import storage.QueueEntry;
import storage.QueueListener;
import storage.WordCountingStorage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class WordsApp {

    private static final String FIRST_FILE_NAME = "text1";
    private static final String SECOND_FILE_NAME = "text2";
    private static final int INTERNAL_QUEUE_CAPACITY = 10000;
    private static final int NUMBER_OF_READERS = 2;

    private final BlockingQueue<QueueEntry> queue = new LinkedBlockingQueue<>(INTERNAL_QUEUE_CAPACITY);
    private final WordCountingStorage storage = new WordCountingStorage();
    private final CountDownLatch readersCountDown = new CountDownLatch(NUMBER_OF_READERS);
    private final CountDownLatch listenerLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        new WordsApp().launch();
    }

    private void launch() throws InterruptedException {
        new QueueListener(queue, storage, readersCountDown, listenerLatch).start();

        new WordsFromFileReader(FIRST_FILE_NAME, queue, readersCountDown).start();
        new WordsFromFileReader(SECOND_FILE_NAME, queue, readersCountDown).start();

        listenerLatch.await();

        print(storage.getSmallestNode());
    }

    private void print(WordCountingStorage.Node node) {
        if (node.getLargerNode() != null) {
            print(node.getLargerNode());
        }
        System.out.printf("%s %s = %s + %s%n",
                node.getWord(),
                node.getTotalCount(),
                node.getSourceToCountMap().getOrDefault(FIRST_FILE_NAME, 0L),
                node.getSourceToCountMap().getOrDefault(SECOND_FILE_NAME, 0L));
    }
}