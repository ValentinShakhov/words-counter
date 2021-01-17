import storage.Node;
import storage.QueueListener;
import storage.StorageQueueFacade;
import storage.WordCountSortedList;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class WordCountingApp {

    private static final List<String> FILE_NAMES = List.of("text1", "text2");

    private final StorageQueueFacade queue = new StorageQueueFacade();
    private final WordCountSortedList wordCountList = new WordCountSortedList();
    private final CountDownLatch readersCountDownLatch = new CountDownLatch(FILE_NAMES.size());
    private final CountDownLatch listenerLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        new WordCountingApp().launch();
    }

    private void launch() throws InterruptedException {
        new QueueListener(queue, wordCountList, readersCountDownLatch, listenerLatch).start();

        FILE_NAMES.forEach(fileName ->
                new WordsFromFileReader(fileName, queue, readersCountDownLatch).start());

        listenerLatch.await();

        printResult();
    }

    private void printResult() {
        Node node = wordCountList.getLast();

        while (node != null) {
            System.out.println(node.getWord() +
                    " " +
                    node.getTotalCount() +
                    " = " +
                    FILE_NAMES.stream()
                            .map(node::getCountBySource)
                            .map(String::valueOf)
                            .collect(Collectors.joining(" + ")));

            node = node.getPrev();
        }
    }
}