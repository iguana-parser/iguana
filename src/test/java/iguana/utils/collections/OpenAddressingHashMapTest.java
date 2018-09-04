package iguana.utils.collections;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;

public class OpenAddressingHashMapTest {

    @Test
    public void testGet() {
        OpenAddressingHashMap<Integer, String> map = new OpenAddressingHashMap<>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(3, "d");
        map.put(4, "e");
        map.put(4, "f");

        assertEquals(4, map.size());
        assertEquals("a", map.get(1));
        assertEquals("b", map.get(2));
        assertEquals("d", map.get(3));
        assertEquals("f", map.get(4));
    }

    @Test
    public void testOverrideExistingValue() {
        OpenAddressingHashMap<Integer, Integer> map = new OpenAddressingHashMap<>();
        map.put(1, 2);
        map.put(1, 3);

        assertEquals(1, map.size());
        assertEquals(3, (int) map.get(1));
    }

    @Test
    public void testIteration() {
        OpenAddressingHashMap<Integer, String> map = new OpenAddressingHashMap<>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(3, "d");
        map.put(4, "e");
        map.put(4, "e");

        assertEquals(set("a", "b", "d", "e"), map.values());
    }

    @Test
    public void testResize() {
        OpenAddressingHashMap<Integer, String> map = new OpenAddressingHashMap<>(4);
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(4, "d");

        assertEquals(set("a", "b", "c", "d"), map.values());
    }

    @Test
    public void testAgainstHashMap() {
        Map<Object, Object> javaHashMap = new HashMap<>();
        OpenAddressingHashMap<Object, Object> openAddressingHashMap = new OpenAddressingHashMap<>();

        Random rand = new Random();
        for (int i = 0; i < 1000000; i++) {
            int key = 1 + rand.nextInt(100);
            String value = "val";
            javaHashMap.put(key, value);
            openAddressingHashMap.put(key, value);
        }

        assertEquals(openAddressingHashMap.size(), javaHashMap.size());

        for (Map.Entry<Object, Object> entry : javaHashMap.entrySet()) {
            Object value = openAddressingHashMap.get(entry.getKey());
            assertEquals(entry.getValue(), value);
        }
    }
}
