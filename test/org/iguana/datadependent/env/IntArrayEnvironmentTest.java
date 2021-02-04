package org.iguana.datadependent.env;

import org.iguana.datadependent.env.intarray.IntArrayEvaluatorContext;
import org.junit.Test;

import static java.lang.Integer.toUnsignedLong;
import static org.junit.Assert.*;

public class IntArrayEnvironmentTest {

    private long combine(int first, int second) {
        return toUnsignedLong(first) << 32 | toUnsignedLong(second);
    }

    @Test
    public void test1() {
        IntArrayEvaluatorContext context = new IntArrayEvaluatorContext();
        Environment init = context.getEmptyEnvironment();

        Environment env = init._declare(1);
        assertEquals(1, env.size());
        assertEquals(1, env.lookup(0));

        env = env._declare(2);
        assertEquals(2, env.size());
        assertEquals(2, env.lookup(1));

        env = env._declare(combine(3, 4));
        assertEquals(3, env.size());
//        assertEquals(combine(3, 4), env.lookup(2));
//
        env = env._declare(combine(5, 6));
        assertEquals(4, env.size());
//        assertEquals(combine(5, 6), env.lookup(3));

        try {
            env._declare(7);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals("Integer values can only be added at position 0 or 1", e.getMessage());
        }

        assertEquals("{1, 2, [3, 4], [5, 6]}", env.toString());
    }

    @Test
    public void test2() {
        IntArrayEvaluatorContext context = new IntArrayEvaluatorContext();
        Environment init = context.getEmptyEnvironment();

        Environment env = init.declare(new Integer[] {1, 2, 3, 4, 5, 6});
        assertEquals(6, env.size());

        assertEquals(1, env.lookup(0));
        assertEquals(2, env.lookup(1));
        // assertEquals(3, env.lookup(2));
        // assertEquals(4, env.lookup(3));
        // assertEquals(5, env.lookup(4));
        // assertEquals(6, env.lookup(5));

        assertEquals("{1, 2, [4, 3], [6, 5]}", env.toString());
    }
}
