package iguana.utils.graph;

import iguana.utils.collections.primitive.IntArray;
import iguana.utils.collections.tuple.IntTuple;

/**
 * Vertex {
 *     marked: boolean      1 bit
 *     onStack: boolean     1 bit
 *     degree: int          6 bit
 *     index: int           16 bit
 *     lowlink: int         16 bit
 *     adj: set             24 bit
 * }
 */
public class CompactDirectedGraph implements  DirectedGraph {

    private final long[] adj;

    private static final long MARKED_MASK = 0x8000_0000_0000_0000L;
    private static final long onStackMask = 0x4000_0000_0000_0000L;
    private static final long degreeMask  = 0x3f00_0000_0000_0000L;
    private static final long indexMask   = 0x00ff_ff00_0000_0000L;
    private static final long lowlinkMask = 0x0000_00ff_ff00_0000L;

    private static final int degreeLength = 6;
    private static final int indexLength = 16;
    private static final int lowlink = 16;

    private static final int MAX_V = 1 << 16;

    public CompactDirectedGraph(int n) {
        adj = new long[n];
    }

    @Override
    public int adj(int v, int i) {
        return 0;
    }

    @Override
    public IntArray adj(int v) {
        return null;
    }

    @Override
    public DirectedGraph addEdge(int v, int w) {
        adj[v] |= (1 << w);
        return this;
    }

    @Override
    public DirectedGraph reverse() {
        return null;
    }

    @Override
    public Iterable<IntTuple> edges() {
        return null;
    }

    @Override
    public int degree(int v) {
        return 0;
    }

    @Override
    public int numV() {
        return 0;
    }

    @Override
    public int numE() {
        return 0;
    }

    public void mark(int v) {
        adj[v] |= MARKED_MASK;
    }

    public boolean isMarked(int v) {
        return (adj[v] & MARKED_MASK) != 0;
    }

    public void unmark(int v) {
        adj[v] &= ~MARKED_MASK;
    }
}
