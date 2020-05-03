package iguana.utils.input;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class GraphInput implements Input {
    private static final int EOF = -1;

    private List<List<Edge>> adjacencyList;
    private List<Integer> startVertices;
    private List<Integer> finalVertices;

    public GraphInput(List<List<Edge>> adjacencyList, List<Integer> startVertices, List<Integer> finalVertices) {
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

    public boolean isFinal(int v) {
        return finalVertices.contains(v);
    }

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

    @Override
    public int length() {
        return 0;
    }

    @Override
    public int getLineNumber(int inputIndex) {
        return 0;
    }

    @Override
    public int getColumnNumber(int inputIndex) {
        return 0;
    }

    @Override
    public String subString(int start, int end) {
        return null;
    }

    @Override
    public int getLineCount() {
        return 0;
    }

    @Override
    public URI getURI() {
        return null;
    }

    @Override
    public boolean isStartOfLine(int inputIndex) {
        return false;
    }

    @Override
    public boolean isEndOfLine(int inputIndex) {
        return false;
    }

    @Override
    public boolean isEndOfFile(int inputIndex) {
        return false;
    }
}
