package org.iguana.utils.collections;

import org.iguana.utils.collections.primitive.IntArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntArrayTest {

    @Test
    public void toStringEmpty() {
        assertEquals("[]", IntArray.EMPTY.toString());
    }

    @Test
    public void toStringOneElement() {
        assertEquals("[1]", new IntArray(new int[] {1}).toString());
    }

    @Test
    public void toStringMoreThanOneElement() {
        int[] a = {0, 1, 2, 3, 4, 5};
        assertEquals("[0,1,2,3,4,5]", new IntArray(a).toString());
        assertEquals("[]", new IntArray(a, 3, 3).toString());
        assertEquals("[3]", new IntArray(a, 3, 4).toString());
        assertEquals("[3,4,5]", new IntArray(a, 3, 6).toString());
    }

    @Test
    public void illeaglArrayInstantiation1() {
        assertThrows(IllegalArgumentException.class, () -> new IntArray(new int[] {0, 1, 2, 3, 4, 5}, -1, 4));
    }

    @Test
    public void illeaglArrayInstantiation2() {
        assertThrows(IllegalArgumentException.class, () -> new IntArray(new int[] {0, 1, 2, 3, 4, 5}, 3, 7));
    }

}
