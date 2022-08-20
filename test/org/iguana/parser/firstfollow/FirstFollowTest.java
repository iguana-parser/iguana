package org.iguana.parser.firstfollow;

import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.iggy.gen.IggyParser;
import org.iguana.regex.CharRange;
import org.iguana.regex.EOF;
import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;
import static org.iguana.utils.collections.CollectionsUtil.set;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstFollowTest {

    @Test
    public void test() {
        RuntimeGrammar grammar = GrammarTransformer.transform(fromIggyGrammar(
            "start A = B C | 'a'\n" +
            "B = A | 'b'\n" +
            "C = 'c'").toRuntimeGrammar());

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
}
