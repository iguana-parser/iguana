package org.iguana.utils;

import org.iguana.utils.collections.IntUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
