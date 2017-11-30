package iguana.utils.collections;

import iguana.utils.collections.primitive.IntArray;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test(expected = IllegalArgumentException.class)
    public void illeaglArrayInstantiation1() {
        new IntArray(new int[] {0, 1, 2, 3, 4, 5}, -1, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illeaglArrayInstantiation2() {
        new IntArray(new int[] {0, 1, 2, 3, 4, 5}, 3, 7);
    }

}
