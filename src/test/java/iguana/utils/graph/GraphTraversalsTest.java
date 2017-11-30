package iguana.utils.graph;

import iguana.utils.collections.primitive.IntArray;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphTraversalsTest {

    private DirectedGraph graph;

    @Before
    public void init() {
        graph = DirectedGraphs.newGraph(6, 2);
        graph.addEdge(1, 3)
             .addEdge(1, 4)
             .addEdge(2, 5)
             .addEdge(3, 2)
             .addEdge(5, 4)
             .addEdge(5, 1);
    }

    @Test
    public void testToString() {
        assertEquals("(1,3)(1,4)(2,5)(3,2)(5,4)(5,1)", graph.toString());
    }

    @Test
    public void testToStringEmptyGraph() {
        assertEquals("", DirectedGraphs.EMPTY.toString());
    }

    @Test
    public void testAdj1() {
        assertEquals(3, graph.adj(1, 0));
        assertEquals(4, graph.adj(1, 1));
        assertEquals(5, graph.adj(2, 0));
        assertEquals(2, graph.adj(3, 0));
        assertEquals(4, graph.adj(5, 0));
        assertEquals(1, graph.adj(5, 1));
    }

    @Test
    public void testAdj2() {
        assertEquals(graph.adj(1), IntArray.of(3, 4));
        assertEquals(graph.adj(2), IntArray.of(5));
        assertEquals(graph.adj(3), IntArray.of(2));
        assertEquals(graph.adj(5), IntArray.of(4, 1));
    }

    @Test
    public void testReverse() {
        DirectedGraph expected = DirectedGraphs.newGraph(6, 2);
        expected.addEdge(3, 1)
                .addEdge(4, 1)
                .addEdge(5, 2)
                .addEdge(2, 3)
                .addEdge(4, 5)
                .addEdge(1, 5);
        assertEquals(expected, graph.reverse());
    }

    @Test
    public void Reachable1() {
        IntArray reachable = GraphTraversals.reachable(graph, 1);
        IntArray expected = IntArray.of(1, 3, 2, 5, 4);
        assertEquals(expected, reachable);
    }

    @Test
    public void testReachable2() {
        IntArray reachable = GraphTraversals.reachable(graph, 3);
        IntArray expecdted = IntArray.of(3, 2, 5, 4, 1);
        assertEquals(expecdted, reachable);
    }

    @Test
    public void testPostOrder() {
        int[] post = GraphTraversals.postOrder(graph);
        assertArrayEquals(new int[] {0, 4, 5, 2, 3, 1}, post);
    }

    @Test
    public void testReversePostOrder() {
        int[] reversePostOrder = GraphTraversals.reversePostOrder(graph);
        assertArrayEquals(new int[] {1, 3, 2, 5, 4, 0}, reversePostOrder);
    }

}
