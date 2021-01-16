package storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class WordCountingStorage {

    private Node smallestNode;

    WordCountingStorage.Node createNewNode(QueueEntry entry) {
        WordCountingStorage.Node newNode = new WordCountingStorage.Node(entry.getWord());

        newNode.getSourceToCountMap().put(entry.getSource(), 1L);
        if (smallestNode != null) {
            newNode.nextNode = smallestNode;
        }
        smallestNode = newNode;

        return newNode;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Node {

        private final Map<String, Long> sourceToCountMap = new HashMap<>();
        private final String word;
        private Node nextNode;

        public long getTotalCount() {
            return sourceToCountMap.values().stream().mapToLong(l -> l).sum();
        }

        void increaseCount(String source) {
            updateCount(source);
            Optional.ofNullable(this.getNextNode())
                    .filter(largerNode -> this.getTotalCount() > largerNode.getTotalCount())
                    .ifPresent(largerNode -> this.swapWithLargerNode());
        }

        private void updateCount(String source) {
            Long count = this.getSourceToCountMap().getOrDefault(source, 0L);
            count++;
            this.getSourceToCountMap().put(source, count);
        }

        private void swapWithLargerNode() {
            Node oldLargerNode = this.nextNode;

            if (oldLargerNode != null) {
                this.nextNode = oldLargerNode.nextNode;
                oldLargerNode.nextNode = this;
            }
        }
    }
}
