package iguana.utils.graph;

import org.junit.Test;

import static org.junit.Assert.*;

public class TopologicalSortTest {

    @Test
    public void test1() {
        DirectedGraph graph = DirectedGraphs.newGraph(7);
        graph.addEdge(0, 1)
             .addEdge(0, 2)
             .addEdge(0, 5)
             .addEdge(1, 4)
             .addEdge(3, 2)
             .addEdge(3, 4)
             .addEdge(3, 5)
             .addEdge(3, 6)
             .addEdge(5, 2)
             .addEdge(6, 4)
             .addEdge(6, 0);

        int[] expected = new int[] {3, 6, 0, 5, 2, 1, 4};
        int[] actual = GraphTraversals.topologicalOrder(graph);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test2() {
        DirectedGraph graph = DirectedGraphs.newGraph(13);
        graph.addEdge(0, 1)
             .addEdge(0, 5)
             .addEdge(0, 6)
             .addEdge(2, 0)
             .addEdge(2, 3)
             .addEdge(3, 5)
             .addEdge(5, 4)
             .addEdge(6, 4)
             .addEdge(6, 9)
             .addEdge(7, 6)
             .addEdge(8, 7)
             .addEdge(9, 10)
             .addEdge(9, 11)
             .addEdge(9, 12)
             .addEdge(11, 12);

        int[] expected = new int[] {8, 7, 2, 3, 0, 6, 9, 11, 12, 10, 5, 4, 1};
        int[] actual = GraphTraversals.topologicalOrder(graph);
        assertArrayEquals(expected, actual);
    }
}
