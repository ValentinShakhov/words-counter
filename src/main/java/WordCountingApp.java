import storage.*;

import java.util.concurrent.CountDownLatch;

public class WordCountingApp {

    private static final String FIRST_FILE_NAME = "text1";
    private static final String SECOND_FILE_NAME = "text2";
    private static final int NUMBER_OF_READERS = 2;

    private final StorageQueueFacade queue = new StorageQueueFacade();
    private final WordCountingStorage storage = new WordCountingStorage();
    private final CountDownLatch readersCountDownLatch = new CountDownLatch(NUMBER_OF_READERS);
    private final CountDownLatch listenerLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        new WordCountingApp().launch();
    }

    private void launch() throws InterruptedException {
        new QueueListener(queue, storage, readersCountDownLatch, listenerLatch).start();

        new WordsFromFileReader(FIRST_FILE_NAME, queue, readersCountDownLatch).start();
        new WordsFromFileReader(SECOND_FILE_NAME, queue, readersCountDownLatch).start();

        listenerLatch.await();

        print(storage.getSmallestNode());
    }

    private void print(Node node) {
        if (node.getNextNode() != null) {
            print(node.getNextNode());
        }
        System.out.printf("%s %s = %s + %s%n",
                node.getWord(),
                node.getTotalCount(),
                node.getCountBySource(FIRST_FILE_NAME),
                node.getCountBySource(SECOND_FILE_NAME));
    }
}