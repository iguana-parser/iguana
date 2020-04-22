package iguana.utils.input;

import java.util.List;
import java.util.stream.Collectors;

public class GraphInput {
    private List<Edge>[] adjacencyList;
    private List<Integer> startVertices;
    private List<Integer> finalVertices;

    public GraphInput(List<Edge>[] adjacencyList, List<Integer> startVertices, List<Integer> finalVertices) {
        this.adjacencyList = adjacencyList;
        this.startVertices = startVertices;
        this.finalVertices = finalVertices;
    }

    public List<Integer> getStartVertices() {
        return this.startVertices;
    }

    public boolean isFinal(int v) {
        return finalVertices.contains(v);
    }

    public List<Integer> getDestVertex(int v, String t) {
        return adjacencyList[v].stream()
                .filter(edge -> edge.getTag().equals(t))
                .map(Edge::getDestVertex)
                .collect(Collectors.toList());
    }

    private class Edge {
        private String tag;
        private int destVertex;

        public Edge(String tag, int dest) {
            this.tag = tag;
            this.destVertex = dest;
        }

        public int getDestVertex() {
            return this.destVertex;
        }

        public String getTag() {
            return this.tag;
        }
    }
}
