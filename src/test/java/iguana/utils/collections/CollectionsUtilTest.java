package iguana.utils.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionsUtilTest {

    @Test
    public void testConcat() {
        Iterable<Integer> list1 = Arrays.asList(1, 2, 3, 4);
        Iterable<Integer> list2 = Arrays.asList(5, 6, 7);
        Iterable<Integer> concat = CollectionsUtil.concat(list1, list2);

        List<Integer> actual = new ArrayList<>();
        for (int val : concat) actual.add(val);

        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), actual);
    }
}
