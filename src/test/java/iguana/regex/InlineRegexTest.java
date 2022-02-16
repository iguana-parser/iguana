package iguana.regex;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InlineRegexTest {

    @Test
    public void test1() {
        // A : a B* c
        // B : c C
        // C : c
        RegularExpression A = Seq.from(Char.from('a'), Star.from(Reference.from("B")), Char.from('c'));
        RegularExpression B = Seq.from(Char.from('c'), Reference.from("C"));
        RegularExpression C = Char.from('c');

        Map<String, RegularExpression> definitions = new HashMap<>();
        definitions.put("A", A);
        definitions.put("B", B);
        definitions.put("C", C);

        RegularExpression newA = Seq.from(Char.from('a'), Star.from(Seq.from(Char.from('c'), Char.from('c'))), Char.from('c'));
        RegularExpression newB = Seq.from(Char.from('c'), Char.from('c'));
        RegularExpression newC = Char.from('c');
        Map<String, RegularExpression> newDefinitions = new HashMap<>();
        newDefinitions.put("A", newA);
        newDefinitions.put("B", newB);
        newDefinitions.put("C", newC);

        // A : a (cc)* c
        assertEquals(InlineReferences.inline(definitions), newDefinitions);
    }
}
