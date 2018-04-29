package iguana.utils;

import iguana.utils.collections.IntUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntUtilsTest {

    @Test
    public void testMerge() {
        long merge = IntUtils.merge(5, 6);

        assertEquals((long)(5 * Math.pow(2, 32) + 6), merge);

        int high = IntUtils.high(merge);
        int low = IntUtils.low(merge);

        assertEquals(5, high);
        assertEquals(6, low);
    }

}
