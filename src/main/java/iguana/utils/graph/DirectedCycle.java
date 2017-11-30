package iguana.utils.graph;

public class DirectedCycle {

    public static boolean hasCycle(DirectedGraph graph) {
        int n = graph.numV();
        boolean[] marked = new boolean[n];
        boolean[] onStack = new boolean[n];
        for (int i = 0; i < graph.numV(); i++)
            if (dfs(graph, i, marked, onStack))
                return true;
        return false;
    }

    private static boolean dfs(DirectedGraph graph, int v, boolean[] marked, boolean[] onStack) {
        marked[v] = true;
        onStack[v] = true;
        for (int i = 0; i < graph.degree(v); i++) {
            int w = graph.adj(v, i);
            if (!marked[w])
                dfs(graph, w, marked, onStack);
            else if (onStack[w])
                return true;
        }
        onStack[v] = false;
        return false;
    }
}
