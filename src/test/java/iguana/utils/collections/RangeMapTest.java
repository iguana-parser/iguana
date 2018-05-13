package iguana.utils.collections;

import iguana.utils.collections.rangemap.Range;
import iguana.utils.collections.rangemap.RangeMap;
import iguana.utils.collections.rangemap.RangeMapBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RangeMapTest {

    /**
     * 1-----7          a
     */
    @Test
    public void testSingleRange() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 7), "a")
                .build();

        assertArrayEquals(new String[]{}, map.get(0));
        assertArrayEquals(new String[]{"a"}, map.get(1));
        assertArrayEquals(new String[]{"a"}, map.get(3));
        assertArrayEquals(new String[]{"a"}, map.get(7));
        assertArrayEquals(new String[]{},   map.get(8));
    }


    /**
     * 1-----7                             a
     *         9--------13                 b
     *                         17----21    c
     */
    @Test
    public void testNonOverlapping() {
        RangeMap<String> map = new RangeMapBuilder<String>()
                .put(Range.in(1, 7), "a")
                .put(Range.in(9, 13), "b")
                .put(Range.in(17, 21), "c")
                .build();

        assertArrayEquals(new String[]{}, map.get(0));
        assertArrayEquals(new String[]{"a"}, map.get(1));
        assertArrayEquals(new String[]{"a"}, map.get(3));
        assertArrayEquals(new String[]{"a"}, map.get(7));
        assertArrayEquals(new String[]{},   map.get(8));
        assertArrayEquals(new String[]{"b"}, map.get(9));
        assertArrayEquals(new String[]{"b"}, map.get(11));
        assertArrayEquals(new String[]{"b"}, map.get(13));
        assertArrayEquals(new String[]{}, map.get(15));
        assertArrayEquals(new String[]{"c"}, map.get(17));
        assertArrayEquals(new String[]{"c"}, map.get(19));
        assertArrayEquals(new String[]{"c"}, map.get(21));
        assertArrayEquals(new String[]{}, map.get(22));
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
                .build();

        assertArrayEquals(new String[]{}, map.get(0));
        assertArrayEquals(new String[]{"a"}, map.get(1));
        assertArrayEquals(new String[]{"a"}, map.get(3));
        assertArrayEquals(new String[]{"a", "b"}, map.get(5));
        assertArrayEquals(new String[]{"a", "b", "c"}, map.get(6));
        assertArrayEquals(new String[]{"a", "b", "c"}, map.get(7));
        assertArrayEquals(new String[]{"b", "c"}, map.get(8));
        assertArrayEquals(new String[]{"b", "c"}, map.get(13));
        assertArrayEquals(new String[]{"c"}, map.get(17));
        assertArrayEquals(new String[]{"c"}, map.get(21));
        assertArrayEquals(new String[]{}, map.get(22));
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
                .build();

        assertArrayEquals(new String[]{}, map.get(0));
        assertArrayEquals(new String[]{"a"}, map.get(1));
        assertArrayEquals(new String[]{"a"}, map.get(3));
        assertArrayEquals(new String[]{"a", "b"}, map.get(5));
        assertArrayEquals(new String[]{"b"}, map.get(6));
        assertArrayEquals(new String[]{"b", "c"}, map.get(8));
        assertArrayEquals(new String[]{"c"}, map.get(10));
        assertArrayEquals(new String[]{"c"}, map.get(12));
        assertArrayEquals(new String[]{}, map.get(13));
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
                .build();

        assertArrayEquals(new String[]{}, map.get(0));
        assertArrayEquals(new String[]{"d", "g"}, map.get(1));
        assertArrayEquals(new String[]{"d", "g"}, map.get(2));
        assertArrayEquals(new String[]{"d", "e", "g"}, map.get(3));
        assertArrayEquals(new String[]{"d", "e", "g"}, map.get(4));
        assertArrayEquals(new String[]{"b", "d", "e", "g"}, map.get(5));
        assertArrayEquals(new String[]{"b", "e", "g"}, map.get(6));
        assertArrayEquals(new String[]{"a", "b", "c", "e", "g"}, map.get(10));
        assertArrayEquals(new String[]{"b", "c", "e", "f", "g"}, map.get(11));
        assertArrayEquals(new String[]{"b", "c", "f", "g"}, map.get(13));
        assertArrayEquals(new String[]{"g"}, map.get(15));
        assertArrayEquals(new String[]{"g"}, map.get(17));
        assertArrayEquals(new String[]{}, map.get(18));
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
                .build();

        assertArrayEquals(new String[]{}, map.get(4));
        assertArrayEquals(new String[]{"a", "b"}, map.get(5));
        assertArrayEquals(new String[]{"a", "b"}, map.get(7));
        assertArrayEquals(new String[]{"a", "b"}, map.get(13));
        assertArrayEquals(new String[]{"a", "b"}, map.get(13));
        assertArrayEquals(new String[]{}, map.get(14));
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
                .build();

        assertArrayEquals(new String[]{}, map.get(4));
        assertArrayEquals(new String[]{"a"}, map.get(5));
        assertArrayEquals(new String[]{"a"}, map.get(7));
        assertArrayEquals(new String[]{}, map.get(8));
        assertArrayEquals(new String[]{"b"}, map.get(9));
        assertArrayEquals(new String[]{"b", "c"}, map.get(10));
        assertArrayEquals(new String[]{"b", "c"}, map.get(10));
        assertArrayEquals(new String[]{"c"}, map.get(15));
        assertArrayEquals(new String[]{}, map.get(17));
        assertArrayEquals(new String[]{"d"}, map.get(18));
        assertArrayEquals(new String[]{"d", "e"}, map.get(19));
        assertArrayEquals(new String[]{"d", "e"}, map.get(20));
        assertArrayEquals(new String[]{"e", "f"}, map.get(21));
        assertArrayEquals(new String[]{"e", "f"}, map.get(23));
        assertArrayEquals(new String[]{"f"}, map.get(25));
        assertArrayEquals(new String[]{}, map.get(26));
    }
}