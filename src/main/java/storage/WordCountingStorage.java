package storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WordCountingStorage {

    private Node smallestNode;

    WordCountingStorage.Node createNewNode(QueueEntry entry) {
        WordCountingStorage.Node newNode = new WordCountingStorage.Node(entry.getWord());

        newNode.getSourceToCountMap().put(entry.getSource(), 1L);
        if (smallestNode != null) {
            smallestNode.smallerNode = newNode;
            newNode.largerNode = smallestNode;
        }
        smallestNode = newNode;

        return newNode;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Node {
        private final Map<String, Long> sourceToCountMap = new HashMap<>();
        private final String word;
        private Node smallerNode;
        private Node largerNode;

        public long getTotalCount() {
            return sourceToCountMap.values().stream().mapToLong(l -> l).sum();
        }

        void swapWithLargerNode() {
            Node oldSmallerNode = this.smallerNode;
            Node oldLargerNode = this.largerNode;
            Node newLargerNode = oldLargerNode != null ? oldLargerNode.largerNode : null;

            this.smallerNode = oldLargerNode;
            this.largerNode = newLargerNode;
            if (newLargerNode != null) {
                newLargerNode.smallerNode = this;
            }

            if (oldLargerNode != null) {
                oldLargerNode.smallerNode = oldSmallerNode;
                oldLargerNode.largerNode = this;
            }

            if (oldSmallerNode != null) {
                oldSmallerNode.largerNode = oldLargerNode;
            }
        }
    }
}
