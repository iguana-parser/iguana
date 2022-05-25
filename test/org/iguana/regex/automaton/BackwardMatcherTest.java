package org.iguana.regex.automaton;

import org.iguana.regex.Char;
import org.iguana.regex.Seq;
import org.iguana.regex.matcher.DFABackwardsMatcher;
import org.iguana.utils.input.Input;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackwardMatcherTest {

    @Test
    public void test() {
        Seq<Char> k1 = Seq.from("for");
        DFABackwardsMatcher matcher = new DFABackwardsMatcher(k1);
        assertEquals(3, matcher.match(Input.fromString("forall"), 3));
    }

}
