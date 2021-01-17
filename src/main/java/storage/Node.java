package storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Node {

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Long> sourceToCountMap = new HashMap<>();

    @Getter
    private final String word;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Node next;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Node prev;

    public long getTotalCount() {
        return this.getSourceToCountMap().values().stream().mapToLong(l -> l).sum();
    }

    public Long getCountBySource(String source) {
        return this.getSourceToCountMap().getOrDefault(source, 0L);
    }

    void updateCount(String source) {
        Long count = this.getSourceToCountMap().getOrDefault(source, 0L);
        count++;
        this.getSourceToCountMap().put(source, count);
    }
}
