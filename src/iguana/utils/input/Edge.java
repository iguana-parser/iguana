package iguana.utils.input;

public class Edge {
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