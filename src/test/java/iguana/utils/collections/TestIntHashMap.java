package iguana.utils.collections;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;

public class TestIntHashMap {

    private IntHashMap<String> map;

    @Before
    public void init() {
        map = new OpenAddressingIntHashMap<>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(3, "d");
        map.put(4, "e");
        map.put(4, "e");
    }

    @Test
    public void testGet() {
        assertEquals("a", map.get(1));
        assertEquals("b", map.get(2));
        assertEquals("d", map.get(3));
        assertEquals("e", map.get(4));
    }

    @Test
    public void testCompute() {
        String s1 = map.compute(5, (k, v) -> {
            assertEquals(null, v);
            return "e";
        });
        assertEquals("e", s1);

        String s2 = map.compute(3, (k, v) -> {
            assertEquals("d", v);
            return "x";
        });
        assertEquals(null, s2);
    }

    @Test
    public void testIteration() {
        Set<String> actualValues = new HashSet<>();
        for (String s : map.values()) {
            actualValues.add(s);
        }

        assertEquals(set("a", "b", "d", "e"), actualValues);
    }

    @Test
    public void testToString() {
        assertEquals("{ }", new OpenAddressingIntHashMap<String>().toString());
        assertEquals("{(1, a), (3, d), (2, b), (4, e)}", map.toString());
    }

}
