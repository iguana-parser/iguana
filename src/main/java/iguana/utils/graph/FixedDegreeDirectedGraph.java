package iguana.utils.graph;

import iguana.utils.collections.CollectionsUtil;
import iguana.utils.collections.primitive.IntArray;
import iguana.utils.collections.tuple.IntTuple;

import java.util.Iterator;

public class FixedDegreeDirectedGraph implements DirectedGraph {

    private final int[] adj;
    private final int maxDegree;
    private final int numV;
    private int numE;
    private int[] numEdgeTo;

    public FixedDegreeDirectedGraph(int n, int maxDegree) {
        this.maxDegree = maxDegree; // add one for the size field
        adj = new int[n * (maxDegree + 1)];
        numV = n;
        numEdgeTo = new int[n];
    }

    @Override
    public FixedDegreeDirectedGraph addEdge(int v, int w) {
        int index = v * (maxDegree + 1);
        int degree = adj[index];
        adj[index] = degree + 1;
        adj[index + degree + 1] = w;
        numE++;
        numEdgeTo[w]++;
        return this;
    }

    @Override
    public int adj(int v, int i) {
        int index = v * (maxDegree + 1);
        return adj[index + i + 1];
    }

    @Override
    public IntArray adj(int v) {
        int index = v * (maxDegree + 1);
        int size = adj[index];
        return new IntArray(adj, index + 1, index + 1 + size);
    }

    @Override
    public int numV() {
        return numV;
    }

    @Override
    public int numE() {
        return numE;
    }

    @Override
    public int degree(int v) {
        return adj[v * (maxDegree + 1)];
    }

    public Iterable<IntTuple> edges() {
        return () -> new Iterator<IntTuple>() {

            int v = 0;
            int j = 1;

            @Override
            public boolean hasNext() {
                return v < numV;
            }

            @Override
            public IntTuple next() {
                int size;
                while ((size = degree(v)) == 0) {
                    v++;
                    j = 1;
                }
                int w = adj[v * (maxDegree + 1) + j++];
                IntTuple t = IntTuple.of(v, w);
                if (j > size) {
                    v++;
                    j = 1;
                }
                return t;
            }
        };
    }

    @Override
    public DirectedGraph reverse() {
        int maxDegreeTo = CollectionsUtil.max(numEdgeTo);
        DirectedGraph reverse = DirectedGraphs.newGraph(numV, maxDegreeTo);
        for (IntTuple t : edges())
            reverse.addEdge(t.second(), t.first());
        return reverse;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof DirectedGraph)) return false;
        DirectedGraph other = (DirectedGraph) obj;
        return CollectionsUtil.isEqual(edges(), other.edges());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IntTuple t : edges())
            sb.append("(" + t.first() + "," + t.second() + ")");
        return sb.toString();
    }

}
