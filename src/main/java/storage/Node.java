package storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class Node {

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Long> sourceToCountMap = new HashMap<>();

    @Getter
    private final String word;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Node nextNode;

    public long getTotalCount() {
        return sourceToCountMap.values().stream().mapToLong(l -> l).sum();
    }

    public Long getCountBySource(String source) {
        return sourceToCountMap.getOrDefault(source, 0L);
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
