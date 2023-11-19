package org.iguana.parser.firstfollow;

import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.regex.CharRange;
import org.iguana.regex.EOF;
import org.iguana.regex.Epsilon;
import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;
import static org.iguana.utils.collections.CollectionsUtil.set;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstFollowTest {

    @Test
    public void test() {
        RuntimeGrammar grammar = GrammarTransformer.transform(fromIggyGrammar(
            "start A = B C | 'a';\n" +
            "B = A | 'b';\n" +
            "C = 'c';").toRuntimeGrammar());

        Nonterminal A = Nonterminal.withName("A");
        Nonterminal B = Nonterminal.withName("B");
        Nonterminal C = Nonterminal.withName("C");
        CharRange a = CharRange.from('a');
        CharRange b = CharRange.from('b');
        CharRange c = CharRange.from('c');

        FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
        assertEquals(set(a, b), firstFollowSets.getFirstSet(A));
        assertEquals(set(a, b), firstFollowSets.getFirstSet(B));
        assertEquals(set(c), firstFollowSets.getFirstSet(C));
        assertEquals(set(c, EOF.asCharRange()), firstFollowSets.getFollowSet(A));
        assertEquals(set(c, EOF.asCharRange()), firstFollowSets.getFollowSet(B));
    }

    @Test
    public void testWithLayout() {
        RuntimeGrammar grammar = GrammarTransformer.transform(fromIggyGrammar(
                "A = B C 'd';\n" +
                "B = 'b' | ;\n" +
                "C = 'c';\n" +
                "layout regex Layout = [\\ \\n]*").toRuntimeGrammar());

        Nonterminal A = Nonterminal.withName("A");
        Nonterminal B = Nonterminal.withName("B");
        Nonterminal C = Nonterminal.withName("C");
        CharRange b = CharRange.from('b');
        CharRange c = CharRange.from('c');
        CharRange d = CharRange.from('d');
        CharRange newLine = CharRange.from('\n');
        CharRange space = CharRange.from(' ');

        FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
        assertEquals(set(space, newLine, b, c), firstFollowSets.getFirstSet(A));
        assertEquals(set(EOF.asCharRange()), firstFollowSets.getFollowSet(A));

        assertEquals(set(b, Epsilon.asCharRange()), firstFollowSets.getFirstSet(B));
        assertEquals(set(space, newLine, c, EOF.asCharRange()), firstFollowSets.getFollowSet(B));

        assertEquals(set(c), firstFollowSets.getFirstSet(C));
        assertEquals(set(d, space, newLine, EOF.asCharRange()), firstFollowSets.getFollowSet(C));
    }
}
