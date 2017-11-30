package iguana.utils.collections;

import iguana.utils.collections.primitive.IntList;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntListTest {

    @Test
    public void emptyToString() {
        IntList list = IntList.of();
        assertEquals("[]", list.toString());
    }

    @Test
    public void OneToString() {
        IntList list = IntList.of(1);
        assertEquals("[1]", list.toString());
    }

    @Test
    public void MoreThanOnetoString() {
        IntList list = IntList.of(1, 2, 3, 4, 5);
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
    }

    @Test
    public void testEquals() {
        IntList a = IntList.of(1, 2, 3, 4, 5);
        IntList b = IntList.of(1, 2, 3, 4, 5);
        assertEquals(a, b);
    }

    @Test
    public void testCapacity() {
        IntList a = new IntList(6);
        a.add(0);
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        a.add(5);
        assertEquals(6, a.getCapacity());
        assertEquals(6, a.size());
        a.add(6);
        assertEquals(12, a.getCapacity());
        assertEquals(7, a.size());
    }

}
