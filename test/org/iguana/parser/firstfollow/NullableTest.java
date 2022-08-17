package org.iguana.parser.firstfollow;

import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.iggy.gen.IggyParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NullableTest {

    @Test
    public void test1() {
        RuntimeGrammar grammar = GrammarTransformer.transform(IggyParser.fromIggyGrammar(
            "start A = B A 'd' | 'a'\n" +
            "B = 'b' | \n").toRuntimeGrammar());

        Nonterminal A = Nonterminal.withName("A");
        Nonterminal B = Nonterminal.withName("B");

        FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
		assertTrue(firstFollowSets.isNullable(B));
    }

    @Test
    public void test() {
        RuntimeGrammar grammar = GrammarTransformer.transform(IggyParser.fromIggyGrammar(
            "start A\n" +
            "  = B 'c'\n" +
            "  | C 'd'\n" +
            "  | 'e'\n" +
            "B = A 'f'\n" +
            "  | A 'g'\n" +
            "C = A 'g'").toRuntimeGrammar());

        Nonterminal A = Nonterminal.withName("A");
        Nonterminal B = Nonterminal.withName("B");

        FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
        assertFalse(firstFollowSets.isNullable(A));
        assertFalse(firstFollowSets.isNullable(B));
    }


}
