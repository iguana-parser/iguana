package org.iguana.regex.automaton;

import iguana.utils.input.Input;
import org.iguana.regex.Char;
import org.iguana.regex.Seq;
import org.iguana.regex.matcher.DFABackwardsMatcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BackwardMatcherTest {

    @Test
    public void test() {
        Seq<Char> k1 = Seq.from("for");
        DFABackwardsMatcher matcher = new DFABackwardsMatcher(k1);
        assertEquals(3, matcher.match(Input.fromString("forall"), 3));
    }

}
