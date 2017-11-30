package iguana.utils.graph;

import iguana.utils.collections.primitive.IntArray;
import iguana.utils.collections.tuple.IntTuple;

public interface DirectedGraph {
    int adj(int v, int i);
    IntArray adj(int v);
    DirectedGraph addEdge(int v, int w);
    DirectedGraph reverse();
    Iterable<IntTuple> edges();
    int degree(int v);
    int numV();
    int numE();
}
