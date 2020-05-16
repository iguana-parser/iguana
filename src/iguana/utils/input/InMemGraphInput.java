package iguana.utils.input;

import java.util.List;
import java.util.stream.Collectors;

public class InMemGraphInput extends GraphInput {
    private List<List<Edge>> adjacencyList;
    private List<Integer> startVertices;
    private List<Integer> finalVertices;

    public InMemGraphInput(List<List<Edge>> adjacencyList, List<Integer> startVertices, List<Integer> finalVertices) {
        this.adjacencyList = adjacencyList;
        this.startVertices = startVertices;
        this.finalVertices = finalVertices;
    }

    @Override
    public List<Integer> getStartVertices() {
        return this.startVertices;
    }

    @Override
    public List<Integer> getFinalVertices() {
        return finalVertices;
    }

    @Override
    public boolean isFinal(int v) {
        return finalVertices.contains(v);
    }

    @Override
    public List<Integer> getDestVertex(int v, String t) {
        return adjacencyList.get(v).stream()
                .filter(edge -> edge.getTag().equals(t))
                .map(Edge::getDestVertex)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> nextSymbols(int v) {
        List<Integer> nextSymbols = adjacencyList.get(v).stream()
                .map(edge -> (int) edge.getTag().charAt(0))
                .collect(Collectors.toList());
        if (isFinal(v)) {
            nextSymbols.add(EOF);
        }
        return nextSymbols;
    }

}
