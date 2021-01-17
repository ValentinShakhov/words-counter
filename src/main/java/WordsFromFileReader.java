import lombok.RequiredArgsConstructor;
import storage.QueueEntry;
import storage.StorageQueueFacade;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
class WordsFromFileReader extends Thread {

    private static final String FILE_NOT_FOUND_EXCEPTION = "File not found";

    private final String fileName;
    private final StorageQueueFacade queue;
    private final CountDownLatch readersCountDownLatch;

    @Override
    public void run() {
        try (InputStream inputStream = Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(fileName))
                .orElseThrow(() -> new RuntimeException(FILE_NOT_FOUND_EXCEPTION))) {
            Scanner scanner = new Scanner(inputStream);
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            while (scanner.hasNext()) {
                String word = scanner.next();
                try {
                    decoder.decode(ByteBuffer.wrap(word.getBytes(StandardCharsets.UTF_8)));
                    queue.put(new QueueEntry(word, fileName));
                } catch (CharacterCodingException ignored) {
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            readersCountDownLatch.countDown();
        }
    }
}