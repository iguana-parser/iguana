package iguana.utils.collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static iguana.utils.collections.CollectionsUtil.set;

public class TestIntHashMap {

    private IntHashMap<String> map;

    @Before
    public void init() {
        map = new OpenAddressingIntHashMap<>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(3, "c");
        map.put(4, "d");
        map.put(4, "d");
    }

    @Test
    public void testIteration() {
        Set<String> actualValues = new HashSet<>();
        for (String s : map.values()) {
            actualValues.add(s);
        }

        Assert.assertEquals(actualValues, set("a", "b", "c", "d"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("{ }", new OpenAddressingIntHashMap<String>().toString());
        Assert.assertEquals("{(1, a), (3, c), (2, b), (4, d)}", map.toString());
    }

}
