package iguana.utils.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemGraphInput extends GraphInput {
    private final List<List<Edge>> adjacencyList;
    private final Stream<Integer> startVertices;
    private final List<Integer> finalVertices;

    public InMemGraphInput(List<List<Edge>> adjacencyList, Stream<Integer> startVertices, List<Integer> finalVertices) {
        this.adjacencyList = adjacencyList;
        this.startVertices = startVertices;
        this.finalVertices = finalVertices;
    }

    @Override
    public Stream<Integer> getStartVertices() {
        return this.startVertices;
    }

    @Override
    public Stream<Integer> getFinalVertices() {
        return finalVertices.stream();
    }

    @Override
    public boolean isFinal(int v) {
        return finalVertices.contains(v);
    }

    @Override
    public List<Integer> getDestVertex(int v, String t) {
        return adjacencyList.get(v).stream()
                .filter(edge -> edge.getTag().equals(t))
                .map(Edge::getDestVertex).collect(Collectors.toList());
    }

    @Override
    public Stream<Integer> nextSymbols(int v) {
        Stream<Integer> nextSymbols = adjacencyList.get(v).stream()
                .map(edge -> (int) edge.getTag().charAt(0));
        if (isFinal(v)) {
            nextSymbols = Stream.concat(Stream.of(EOF), nextSymbols);
        }
        return nextSymbols;
    }

}
