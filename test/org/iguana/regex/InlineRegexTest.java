package org.iguana.regex;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InlineRegexTest {

    @Test
    public void testInline() {
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

    @Test
    public void testDetectCycles() {
        // A : a B* c
        // B : c C
        // C : A

        RegularExpression A = Seq.from(Char.from('a'), Star.from(Reference.from("B")), Char.from('c'));
        RegularExpression B = Seq.from(Char.from('c'), Reference.from("C"));
        RegularExpression C = Reference.from("A");

        Map<String, RegularExpression> definitions = new HashMap<>();
        definitions.put("A", A);
        definitions.put("B", B);
        definitions.put("C", C);

        try {
            InlineReferences.inline(definitions);
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Regular expression references cannot be cyclic: A->B->C->A");
        }
    }
}
