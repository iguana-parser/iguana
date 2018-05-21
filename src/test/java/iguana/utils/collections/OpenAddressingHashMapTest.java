package iguana.utils.collections;

import org.junit.jupiter.api.Test;

import java.util.*;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OpenAddressingHashMapTest {

    @Test
    public void testNegativeInitialCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new OpenAddressingHashMap(-1));
    }

    @Test
    public void testNegativeLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> new OpenAddressingHashMap(16, -1));
    }

    @Test
    public void testGreaterThanOneLoadFactor() {
        assertThrows(IllegalArgumentException.class, () -> new OpenAddressingHashMap(1.05f));
    }

    @Test
    public void testCapacity() {
        assertEquals(16, new OpenAddressingHashMap().getCapacity());
        assertEquals(8, new OpenAddressingHashMap(5).getCapacity());
        assertEquals(4, new OpenAddressingHashMap(4).getCapacity());
        assertEquals(32, new OpenAddressingHashMap(17).getCapacity());
    }

    @Test
    public void testGetThreshold() {
        assertEquals(11, new OpenAddressingHashMap().getThreshold());
        assertEquals(4, new OpenAddressingHashMap(5, 0.5f).getThreshold());
        assertEquals(0, new OpenAddressingHashMap(4, 0.1f).getThreshold());
        assertEquals(22, new OpenAddressingHashMap(17).getThreshold());
    }

    @Test
    public void testGet() {
        OpenAddressingHashMap map = new OpenAddressingHashMap();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(3, "d");
        map.put(4, "e");
        map.put(4, "e");

        assertEquals(4, map.size());
        assertEquals("a", map.get(1));
        assertEquals("b", map.get(2));
        assertEquals("d", map.get(3));
        assertEquals("e", map.get(4));
    }

    @Test
    public void testOverrideExistingValue() {
        OpenAddressingHashMap map = new OpenAddressingHashMap();
        map.put(1, 2);
        map.put(1, 3);

        assertEquals(1, map.size());
        assertEquals(3, map.get(1));
    }

    @Test
    public void testIteration() {
        OpenAddressingHashMap map = new OpenAddressingHashMap();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(3, "d");
        map.put(4, "e");
        map.put(4, "e");

        Set<Object> actualValues = new HashSet<>();
        map.values().forEach(actualValues::add);

        assertEquals(set("a", "b", "d", "e"), actualValues);
    }

    @Test
    public void testResize() {
        OpenAddressingHashMap map = new OpenAddressingHashMap(6);

        assertEquals(8, map.getCapacity());
        assertEquals(5, map.getThreshold());

        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(4, "d");
        map.put(5, "e");

        assertEquals(5, map.getThreshold());
        assertEquals(8, map.getCapacity());

        map.put(6, "f");

        assertEquals(16, map.getCapacity());
        assertEquals(11, map.getThreshold());

        assertEquals("a", map.get(1));
        assertEquals("b", map.get(2));
        assertEquals("c", map.get(3));
        assertEquals("d", map.get(4));
        assertEquals("e", map.get(5));
        assertEquals("f", map.get(6));
    }

    @Test
    public void testAgainstHashMap() {
        Map<Object, Object> javaHashMap = new HashMap<>();
        OpenAddressingHashMap openAddressingHashMap = new OpenAddressingHashMap();

        Random rand = new Random();
        for (int i = 0; i < 10000; i++) {
            int key = 1 + rand.nextInt(100);
            String value = "val";
            javaHashMap.put(key, value);
            openAddressingHashMap.put(key, value);
        }

//        assertEquals(openAddressingHashMap.size(), javaHashMap.size());

        for (Map.Entry<Object, Object> entry : javaHashMap.entrySet()) {
            Object value = openAddressingHashMap.get(entry.getKey());
            assertEquals(entry.getValue(), value);
        }
    }
}
