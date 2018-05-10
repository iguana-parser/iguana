package iguana.utils.collections;

import org.junit.Assert;
import org.junit.Test;

public class TestIntHashSet {

    @Test
    public void test() {
        IntHashSet set = new IntHashSet();
        set.add(1);
        set.add(2);
        set.add(3);

        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.contains(1));
        Assert.assertTrue(set.contains(2));
        Assert.assertTrue(set.contains(3));
    }
}
