package iguana.utils.collections;

import iguana.utils.collections.rangemap.Range;
import iguana.utils.collections.rangemap.RangeMap;
import iguana.utils.collections.rangemap.RangeMapBuilder;
import org.junit.jupiter.api.Test;

import static iguana.utils.collections.CollectionsUtil.list;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangeMapTest {

    /**
     * 1-----7          a
     */
    @Test
    public void testSingleRange() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 7), "a")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(0));
        assertEquals(list("a"), map.get(1));
        assertEquals(list("a"), map.get(3));
        assertEquals(list("a"), map.get(7));
        assertEquals(emptyList(),   map.get(8));
    }


    /**
     * 1-----7                             a
     *         9--------13                 b
     *                         17----21    c
     */
    @Test
    public void testNonOverlapping1() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 7), "a")
                .put(Range.in(9, 13), "b")
                .put(Range.in(17, 21), "c")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(0));
        assertEquals(list("a"), map.get(1));
        assertEquals(list("a"), map.get(3));
        assertEquals(list("a"), map.get(7));
        assertEquals(emptyList(),   map.get(8));
        assertEquals(list("b"), map.get(9));
        assertEquals(list("b"), map.get(11));
        assertEquals(list("b"), map.get(13));
        assertEquals(emptyList(), map.get(15));
        assertEquals(list("c"), map.get(17));
        assertEquals(list("c"), map.get(19));
        assertEquals(list("c"), map.get(21));
        assertEquals(emptyList(), map.get(22));
    }

    /**
     * 1-1                 a
     *       5-5           b
     *            10-10    c
     */
    @Test
    public void testNonOverlapping2() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 1), "a")
                .put(Range.in(5, 5), "b")
                .put(Range.in(10, 10), "c")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(0));
        assertEquals(list("a"), map.get(1));
        assertEquals(emptyList(), map.get(3));
        assertEquals(list("b"), map.get(5));
        assertEquals(emptyList(),   map.get(7));
        assertEquals(list("c"), map.get(10));
        assertEquals(emptyList(), map.get(11));
    }


    /**
     * 1-----7                          a
     *     5------------13              b
     *      6---------------------21    c
     */
    @Test
    public void testOverlapping() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 7), "a")
                .put(Range.in(5, 13), "b")
                .put(Range.in(6, 21), "c")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(0));
        assertEquals(list("a"), map.get(1));
        assertEquals(list("a"), map.get(3));
        assertEquals(list("a", "b"), map.get(5));
        assertEquals(list("a", "b", "c"), map.get(6));
        assertEquals(list("a", "b", "c"), map.get(7));
        assertEquals(list("b", "c"), map.get(8));
        assertEquals(list("b", "c"), map.get(13));
        assertEquals(list("c"), map.get(17));
        assertEquals(list("c"), map.get(21));
        assertEquals(emptyList(), map.get(22));
    }

    /**
     * 1---5                a
     *     5--8             b
     *        8-------12    c
     */
    @Test
    public void testOverlapping2() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 5), "a")
                .put(Range.in(5, 8), "b")
                .put(Range.in(8, 12), "c")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(0));
        assertEquals(list("a"), map.get(1));
        assertEquals(list("a"), map.get(3));
        assertEquals(list("a", "b"), map.get(5));
        assertEquals(list("b"), map.get(6));
        assertEquals(list("b", "c"), map.get(8));
        assertEquals(list("c"), map.get(10));
        assertEquals(list("c"), map.get(12));
        assertEquals(emptyList(), map.get(13));
    }

    /**
     *           10-10                   a
     *     5----------------13           b
     *              10------13           c
     * 1---5                             d
     *   3-------------11                e
     *                 11---13           f
     * 1---------------------------17    g
     */
    @Test
    public void testOverlapping3() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(10, 10), "a")
                .put(Range.in(5, 13), "b")
                .put(Range.in(10, 13), "c")
                .put(Range.in(1, 5), "d")
                .put(Range.in(3, 11), "e")
                .put(Range.in(11, 13), "f")
                .put(Range.in(1, 17), "g")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(0));
        assertEquals(list("d", "g"), map.get(1));
        assertEquals(list("d", "g"), map.get(2));
        assertEquals(list("d", "g", "e"), map.get(3));
        assertEquals(list("d", "g", "e"), map.get(4));
        assertEquals(list("d", "g", "e", "b"), map.get(5));
        assertEquals(list("g", "e", "b"), map.get(6));
        assertEquals(list("g", "e", "b", "a", "c"), map.get(10));
        assertEquals(list("g", "e", "b", "c", "f"), map.get(11));
        assertEquals(list("g", "b", "c", "f"), map.get(13));
        assertEquals(list("g"), map.get(15));
        assertEquals(list("g"), map.get(17));
        assertEquals(emptyList(), map.get(18));
    }

    /**
     *     5----------------13           a
     *     5----------------13           b
     */
    @Test
    public void testOverlapping4() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(5, 13), "a")
                .put(Range.in(5, 13), "b")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(4));
        assertEquals(list("a", "b"), map.get(5));
        assertEquals(list("a", "b"), map.get(7));
        assertEquals(list("a", "b"), map.get(13));
        assertEquals(list("a", "b"), map.get(13));
        assertEquals(emptyList(), map.get(14));
    }

    /**
     *     5-7                                  a
     *         9-11                             b
     *          10---15                         c
     *                      18-20               d
     *                        19--23            e
     *                          21---25         f
     */
    @Test
    public void testOverlapping5() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(5, 7), "a")
                .put(Range.in(9, 11), "b")
                .put(Range.in(10, 15), "c")
                .put(Range.in(18, 20), "d")
                .put(Range.in(19, 23), "e")
                .put(Range.in(21, 25), "f")
                .buildRangeMap();

        assertEquals(emptyList(), map.get(4));
        assertEquals(list("a"), map.get(5));
        assertEquals(list("a"), map.get(7));
        assertEquals(emptyList(), map.get(8));
        assertEquals(list("b"), map.get(9));
        assertEquals(list("b", "c"), map.get(10));
        assertEquals(list("b", "c"), map.get(10));
        assertEquals(list("c"), map.get(15));
        assertEquals(emptyList(), map.get(17));
        assertEquals(list("d"), map.get(18));
        assertEquals(list("d", "e"), map.get(19));
        assertEquals(list("d", "e"), map.get(20));
        assertEquals(list("e", "f"), map.get(21));
        assertEquals(list("e", "f"), map.get(23));
        assertEquals(list("f"), map.get(25));
        assertEquals(emptyList(), map.get(26));
    }
}