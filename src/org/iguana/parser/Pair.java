package org.iguana.parser;

import java.util.Objects;

public class Pair {
    public int startVertex;
    public int endVertex;

    public Pair(int startVertex, int endVertex) {
        this.startVertex = startVertex;
        this.endVertex = endVertex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return startVertex == pair.startVertex &&
                endVertex == pair.endVertex;
    }

    public boolean equalsKey(int start) {
        return startVertex == start;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startVertex, endVertex);
    }
}
