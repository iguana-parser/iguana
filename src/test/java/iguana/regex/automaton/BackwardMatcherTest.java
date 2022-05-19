package iguana.regex.automaton;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.regex.matcher.DFABackwardsMatcher;
import iguana.utils.input.Input;
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
