package iguana.utils.graph;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DirectedCycleTest {

    DirectedGraph graph1;

    @Before
    public void init() {
        graph1 = new FixedDegreeDirectedGraph(5, 1);
        graph1.addEdge(1, 2)
              .addEdge(2, 3)
              .addEdge(3, 4)
              .addEdge(4, 1);
    }

    @Test
    public void test1() {
        assertEquals(true, DirectedCycle.hasCycle(graph1));
    }
}
