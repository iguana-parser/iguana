package iguana.utils.graph;

import java.io.InputStream;
import java.util.Scanner;

public class DirectedGraphs {

    public static final DirectedGraph EMPTY = new FixedDegreeDirectedGraph(0, 0);

    public static DirectedGraph fromResource(String path) {
        InputStream in = DirectedGraphs.class.getResourceAsStream(path);
        Scanner scanner = new Scanner(in);
        int n = scanner.nextInt();
        int maxDegree = scanner.nextInt();
        DirectedGraph graph = DirectedGraphs.newGraph(n, maxDegree);
        while (scanner.hasNextLine()) {
            int v = scanner.nextInt();
            int w = scanner.nextInt();
            graph.addEdge(v, w);
        }
        return graph;
    }

    public static DirectedGraph newGraph(int n) {
        return new VariableDegreeDirectedGraph(n);
    }

    public static DirectedGraph newGraph(int n, int maxDegree) {
        return new FixedDegreeDirectedGraph(n, maxDegree);
    }

}
