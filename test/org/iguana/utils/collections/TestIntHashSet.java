package org.iguana.utils.collections;

import org.iguana.utils.collections.IntHashSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIntHashSet {

    @Test
    public void test() {
        org.iguana.utils.collections.IntHashSet set = new IntHashSet();
        set.add(1);
        set.add(2);
        set.add(3);

        assertFalse(set.isEmpty());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }
}
