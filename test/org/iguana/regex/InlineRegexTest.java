package org.iguana.regex;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InlineRegexTest {

    @Test
    public void testInline() {
        // A : a B* c
        // B : c C
        // C : c
        org.iguana.regex.RegularExpression A = org.iguana.regex.Seq.from(org.iguana.regex.Char.from('a'), org.iguana.regex.Star.from(org.iguana.regex.Reference.from("B")), org.iguana.regex.Char.from('c'));
        org.iguana.regex.RegularExpression B = org.iguana.regex.Seq.from(org.iguana.regex.Char.from('c'), org.iguana.regex.Reference.from("C"));
        org.iguana.regex.RegularExpression C = org.iguana.regex.Char.from('c');

        Map<String, org.iguana.regex.RegularExpression> definitions = new HashMap<>();
        definitions.put("A", A);
        definitions.put("B", B);
        definitions.put("C", C);

        org.iguana.regex.RegularExpression newA = org.iguana.regex.Seq.from(org.iguana.regex.Char.from('a'), org.iguana.regex.Star.from(org.iguana.regex.Seq.from(org.iguana.regex.Char.from('c'), org.iguana.regex.Char.from('c'))), org.iguana.regex.Char.from('c'));
        org.iguana.regex.RegularExpression newB = org.iguana.regex.Seq.from(org.iguana.regex.Char.from('c'), org.iguana.regex.Char.from('c'));
        org.iguana.regex.RegularExpression newC = org.iguana.regex.Char.from('c');
        Map<String, org.iguana.regex.RegularExpression> newDefinitions = new HashMap<>();
        newDefinitions.put("A", newA);
        newDefinitions.put("B", newB);
        newDefinitions.put("C", newC);

        // A : a (cc)* c
        assertEquals(org.iguana.regex.InlineReferences.inline(definitions), newDefinitions);
    }

    @Test
    public void testDetectCycles() {
        // A : a B* c
        // B : c C
        // C : A

        org.iguana.regex.RegularExpression A = org.iguana.regex.Seq.from(org.iguana.regex.Char.from('a'), Star.from(org.iguana.regex.Reference.from("B")), org.iguana.regex.Char.from('c'));
        org.iguana.regex.RegularExpression B = Seq.from(Char.from('c'), org.iguana.regex.Reference.from("C"));
        org.iguana.regex.RegularExpression C = Reference.from("A");

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
