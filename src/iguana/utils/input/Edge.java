package iguana.utils.input;

public class Edge {
    private final String tag;
    private final int destVertex;

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