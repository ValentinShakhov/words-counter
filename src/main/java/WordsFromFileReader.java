import lombok.RequiredArgsConstructor;
import storage.QueueEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
class WordsFromFileReader extends Thread {

    private static final String FILE_NOT_FOUND_EXCEPTION = "File not found";

    private final String fileName;
    private final BlockingQueue<QueueEntry> queue;
    private final CountDownLatch readersCountDownLatch;

    @Override
    public void run() {
        try (InputStream inputStream = Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(fileName))
                .orElseThrow(() -> new RuntimeException(FILE_NOT_FOUND_EXCEPTION))) {
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNext()) {
                //TODO check non UTF-8 handling
                String word = scanner.next();
                queue.put(new QueueEntry(word, fileName));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            readersCountDownLatch.countDown();
        }
    }
}