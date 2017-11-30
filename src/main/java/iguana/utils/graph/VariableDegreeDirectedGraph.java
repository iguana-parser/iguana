package iguana.utils.graph;

import iguana.utils.collections.primitive.IntArray;
import iguana.utils.collections.primitive.IntList;
import iguana.utils.collections.tuple.IntTuple;

import java.util.Iterator;

public class VariableDegreeDirectedGraph implements DirectedGraph {

    private final IntList[] adj;
    private int numE;

    public VariableDegreeDirectedGraph(int n) {
        adj = new IntList[n];
    }

    @Override
    public int adj(int v, int i) {
        IntList ws = adj[v];
        if (ws == null) throw new NullPointerException();
        return ws.get(i);
    }

    @Override
    public IntArray adj(int src) {
        IntList ws = adj[src];
        if (ws == null) return IntArray.EMPTY;
        return ws.toIntArray();
    }

    @Override
    public DirectedGraph addEdge(int src, int dest) {
        if (adj[src] == null) adj[src] = new IntList();
        adj[src].add(dest);
        numE++;
        return this;
    }

    @Override
    public DirectedGraph reverse() {
        int n = adj.length;
        DirectedGraph reverse = new VariableDegreeDirectedGraph(adj.length);
        for (int v = 0; v < n; v++) {
            IntList ws = adj[v];
            if (ws == null) continue;
            for (int i = 0; i < ws.size(); i++) {
                int w = ws.get(i);
                reverse.addEdge(w, v);
            }
        }
        return reverse;
    }

    @Override
    public Iterable<IntTuple> edges() {
        return () -> new Iterator<IntTuple>() {

            int v = 0;
            int j = 0;

            @Override
            public boolean hasNext() {
                return v < adj.length;
            }

            @Override
            public IntTuple next() {
                IntList l;
                while ((l = adj[v]) == null) {
                    v++; j = 0;
                }

                int w = l.get(j++);
                IntTuple t = IntTuple.of(v, w);

                if (j == adj[v].size()) {
                    v++;
                    j = 0;
                }

                return t;
            }
        };
    }

    @Override
    public int degree(int v) {
        IntList ws = adj[v];
        return ws == null ? 0 : ws.size();
    }

    @Override
    public int numV() {
        return adj.length;
    }

    @Override
    public int numE() {
        return numE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IntTuple t : edges())
            sb.append("(" + t.first() + "," + t.second() + ")");
        return sb.toString();
    }
}
