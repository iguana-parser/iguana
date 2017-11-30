package iguana.utils.graph;

import iguana.utils.collections.IntHashSet;
import iguana.utils.collections.primitive.IntList;

public class StronglyConnectedComponents {

    public static DirectedGraph scc(DirectedGraph g) {
        int[] order = GraphTraversals.reversePostOrder(g.reverse());
        int count = 0;
        boolean[] marked = new boolean[g.numV()];
        int[] id = new int[g.numV()];
        for (int v : order)
            if (!marked[v]) {
                dfs(g, v, marked, id, count);
                count++;
            }

        IntList[] ccs = new IntList[count];

        for (int i = 0; i < count; i++)
            ccs[i] = new IntList();

        for (int v = 0; v < g.numV(); v++)
            ccs[id[v]].add(v);

        IntHashSet[] sets = new IntHashSet[count];
        for (int i = 0; i < count; i++) {
            sets[i] = new IntHashSet();
            sets[i].add(i);
        }

        DirectedGraph res = DirectedGraphs.newGraph(count);
        for (int v = 0; v < count; v++) {
            IntList components = ccs[v];
            for (int i = 0; i < components.size(); i++) {
                int c = components.get(i);
                for (int j = 0; j < g.adj(c).size(); j++) {
                    int w = id[g.adj(c, j)];
                    if (!sets[v].contains(w)) {
                        sets[v].add(w);
                        res.addEdge(v, w);
                    }
                }
            }
        }

        return res;
    }

    private static void dfs(DirectedGraph g, int v, boolean[] marked, int[] id, int count) {
        marked[v] = true;
        id[v] = count;
        for (int i = 0; i < g.degree(v); i++) {
            int w = g.adj(v, i);
            if (!marked[w])
                dfs(g, w, marked, id, count);
        }
    }
}
