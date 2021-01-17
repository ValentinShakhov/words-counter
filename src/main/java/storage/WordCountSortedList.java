package storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter(AccessLevel.PACKAGE)
public class WordCountSortedList {

    private Node first;

    private Node last;

    Node createNewNode(QueueEntry entry) {
        Node newNode = new Node(entry.getWord());

        newNode.getSourceToCountMap().put(entry.getSource(), 1L);
        newNode.setNext(this.getFirst());
        if (this.getFirst() != null) {
            this.getFirst().setPrev(newNode);
        }
        this.setFirst(newNode);

        return newNode;
    }

    void updateNode(String source, Node node) {
        node.updateCount(source);
        Optional.ofNullable(node.getNext())
                .filter(largerNode -> node.getTotalCount() > largerNode.getTotalCount())
                .ifPresent(largerNode -> swapWithLargerNode(node));
    }

    private void swapWithLargerNode(Node node) {
        Node oldLargerNode = node.getNext();
        Node oldSmallerNode = node.getPrev();

        node.setNext(oldLargerNode.getNext());
        if (oldLargerNode.getNext() != null) {
            oldLargerNode.getNext().setPrev(node);
        }
        oldLargerNode.setNext(node);
        node.setPrev(oldLargerNode);
        if (oldSmallerNode != null) {
            oldSmallerNode.setNext(oldLargerNode);
            oldLargerNode.setPrev(oldSmallerNode);
        }

        if (node.getNext() == null) {
            this.setLast(node);
        }
    }
}
