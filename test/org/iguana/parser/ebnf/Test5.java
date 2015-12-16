package org.iguana.parser.ebnf;


import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.regex.Character;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;
import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

/**
 *
 * S ::= (A)
 *
 * A ::= 'a'
 *
 */
public class Test5 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Character.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(Sequence.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();

    private static Input input1 = Input.fromString("a");

    @Test
    public void testParse1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    private static ParseResult getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(5)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(2)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(1)
                .setIntermediateNodesCount(0)
                .setPackedNodesCount(3)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(graph), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input1);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("(A)"), registry.getSlot("(A) ::= A ."), node1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= (A) ."), node2, input1);
        return node3;
    }

    public static Term getTree1() {
        Term t0 = createTerminal(a, 0, 1, input1);
        Term t1 = createRule(r2, list(t0), input1);
        Term t2 = createGroup(list(t1));
        Term t3 = createRule(r1, list(t2), input1);
        return t3;
    }
}
