package iguana.utils.graph;

import org.junit.Test;

import static org.junit.Assert.*;

public class StronglyConnectedComponentsTest {

    @Test
    public void test1() {
        DirectedGraph g = DirectedGraphs.fromResource("/graphs/graph1.txt");
        DirectedGraph expected = DirectedGraphs.newGraph(5, 2);
        expected.addEdge(1, 0).addEdge(2, 1).addEdge(3, 1).addEdge(3, 2).addEdge(4, 3).addEdge(4, 2);
        DirectedGraph actual = StronglyConnectedComponents.scc(g);
        assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        DirectedGraph g = DirectedGraphs.fromResource("/graphs/graph2.txt");
        DirectedGraph expected = DirectedGraphs.newGraph(3, 2);
        expected.addEdge(1, 0).addEdge(2, 0).addEdge(2, 1);
        DirectedGraph actual = StronglyConnectedComponents.scc(g);
        assertEquals(expected, actual);
    }
}
