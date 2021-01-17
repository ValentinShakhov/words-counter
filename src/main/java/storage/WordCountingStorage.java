package storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter(AccessLevel.PACKAGE)
public class WordCountingStorage {

    private Node smallestNode;

    private Node largestNode;

    Node createNewNode(QueueEntry entry) {
        Node newNode = new Node(entry.getWord());

        newNode.getSourceToCountMap().put(entry.getSource(), 1L);
        newNode.setLargerNode(this.getSmallestNode());
        if (this.getSmallestNode() != null) {
            this.getSmallestNode().setSmallerNode(newNode);
        }
        this.setSmallestNode(newNode);

        return newNode;
    }

    void updateNode(String source, Node node) {
        node.updateCount(source);
        Optional.ofNullable(node.getLargerNode())
                .filter(largerNode -> node.getTotalCount() > largerNode.getTotalCount())
                .ifPresent(largerNode -> swapWithLargerNode(node));
    }

    private void swapWithLargerNode(Node node) {
        Node oldLargerNode = node.getLargerNode();
        Node oldSmallerNode = node.getSmallerNode();

        node.setLargerNode(oldLargerNode.getLargerNode());
        if (oldLargerNode.getLargerNode() != null) {
            oldLargerNode.getLargerNode().setSmallerNode(node);
        }
        oldLargerNode.setLargerNode(node);
        node.setSmallerNode(oldLargerNode);
        if (oldSmallerNode != null) {
            oldSmallerNode.setLargerNode(oldLargerNode);
            oldLargerNode.setSmallerNode(oldSmallerNode);
        }

        if (node.getLargerNode() == null) {
            largestNode = node;
        }
    }
}
