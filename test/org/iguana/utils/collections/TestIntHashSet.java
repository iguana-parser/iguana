package org.iguana.utils.collections;

import org.iguana.utils.collections.primitive.IntIterator;
import org.iguana.utils.collections.primitive.IntList;
import org.iguana.utils.collections.primitive.IntSet;
import org.iguana.utils.collections.primitive.IntSetFactory;
import org.iguana.utils.collections.primitive.OpenAddressingIntHashSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIntHashSet {

    @Test
    public void testOneElement() {
        IntSet set = IntSetFactory.create(1);
        assertInstanceOf(IntSetFactory.ImmutableSingleElementIntSet.class, set);

        assertFalse(set.isEmpty());
        assertTrue(set.contains(1));

        IntIterator it = set.iterator();
        Set<Integer> actual = new HashSet<>();
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals(Set.of(1), actual);

        assertThrows(UnsupportedOperationException.class, () -> set.add(2));
    }

    @Test
    public void testTwoElement() {
        IntSet set = IntSetFactory.create(1, 2);
        assertInstanceOf(IntSetFactory.ImmutableTwoElementIntSet.class, set);

        assertFalse(set.isEmpty());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));

        IntIterator it = set.iterator();
        Set<Integer> actual = new HashSet<>();
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals(Set.of(1, 2), actual);

        assertThrows(UnsupportedOperationException.class, () -> set.add(3));
    }

    @Test
    public void testMoreThanTwoElements() {
        IntSet set = IntSetFactory.create(1, 2, 3);
        assertInstanceOf(OpenAddressingIntHashSet.class, set);

        assertFalse(set.isEmpty());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));

        IntIterator it = set.iterator();
        Set<Integer> actual = new HashSet<>();
        while (it.hasNext()) {
            actual.add(it.next());
        }
        assertEquals(Set.of(1, 2, 3), actual);

        assertThrows(IllegalArgumentException.class, () -> set.add(-1));
        assertTrue(set.add(4));
        assertFalse(set.add(1));
    }
}
