package storage;

import lombok.Getter;

@Getter
public class WordCountingStorage {

    private Node smallestNode;

    Node createNewNode(QueueEntry entry) {
        Node newNode = new Node(entry.getWord());

        newNode.getSourceToCountMap().put(entry.getSource(), 1L);
        if (smallestNode != null) {
            newNode.setNextNode(smallestNode);
        }
        smallestNode = newNode;

        return newNode;
    }
}
