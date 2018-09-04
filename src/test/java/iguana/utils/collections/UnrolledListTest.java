package iguana.utils.collections;

import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnrolledListTest {

    private List<Integer> list;

    @BeforeEach
    void init() {
        list = new UnrolledList<>(4);
    }

    @Test
    public void test() {
        assertEquals(0, list.size());

        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(4, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(4));

        list.add(4);

        assertEquals(5, list.size());

        assertEquals(0, (int) list.get(0));
        assertEquals(1, (int) list.get(1));
        assertEquals(2, (int) list.get(2));
        assertEquals(3, (int) list.get(3));
        assertEquals(4, (int) list.get(4));

        list.add(5);
        list.add(6);
        list.add(7);

        assertEquals(5, (int) list.get(5));
        assertEquals(6, (int) list.get(6));
        assertEquals(7, (int) list.get(7));

        list.add(8);

        assertEquals(9, list.size());
        assertEquals(8, (int) list.get(8));
    }
}
