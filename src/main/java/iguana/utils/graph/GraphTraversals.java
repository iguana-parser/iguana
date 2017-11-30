package iguana.utils.graph;

import iguana.utils.collections.primitive.IntArray;
import iguana.utils.collections.primitive.IntList;

public class GraphTraversals {

    public static IntArray reachable(DirectedGraph graph, int v) {
        int n = graph.numV();
        IntList reachable = new IntList();
        preDFS(graph, v, new boolean[n], reachable);
        return reachable.toIntArray();
    }

    public static int[] preOrder(DirectedGraph graph) {
        int n = graph.numV();
        IntList preOrder = new IntList(n);
        boolean[] marked = new boolean[n];
        for (int v = 0; v < n; v++)
            if (!marked[v])
                preDFS(graph, v, marked, preOrder);
        return preOrder.toArray();
    }

    public static int[] postOrder(DirectedGraph graph) {
        int n = graph.numV();
        IntList postOrder = new IntList(n);
        boolean[] marked = new boolean[n];
        for (int v = 0; v < n; v++)
            if (!marked[v])
                postDFS(graph, v, marked, postOrder);
        return postOrder.toArray();
    }

    public static int[] reversePostOrder(DirectedGraph graph) {
        int[] postOrder = postOrder(graph);
        int n = postOrder.length;
        for (int i = 0; i < n / 2; i++) {
            int tmp = postOrder[i];
            postOrder[i] = postOrder[n - 1 - i];
            postOrder[n - 1 - i] = tmp;
        }
        return postOrder;
    }

    public static int[] topologicalOrder(DirectedGraph graph) {
        if (DirectedCycle.hasCycle(graph)) throw new RuntimeException("Graph is cyclic.");
        return reversePostOrder(graph);
    }

    private static void preDFS(DirectedGraph graph, int v, boolean[] marked, IntList acc) {
        acc.add(v);
        marked[v] = true;
        for (int i = 0; i < graph.degree(v); i++) {
            int w = graph.adj(v, i);
            if (!marked[w])
                preDFS(graph, w, marked, acc);
        }
    }

    private static void postDFS(DirectedGraph graph, int v, boolean[] marked, IntList acc) {
        marked[v] = true;
        for (int i = 0; i < graph.degree(v); i++) {
            int w = graph.adj(v, i);
            if (!marked[w])
                postDFS(graph, w, marked, acc);
        }
        acc.add(v);
    }

}
