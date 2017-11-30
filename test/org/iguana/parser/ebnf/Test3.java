package org.iguana.parser.ebnf;

import iguana.regex.Character;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * S ::= A?
 *
 * A ::= 'a'
 *
 */
public class Test3 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Character.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(Opt.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();

    private static Input input0 = Input.fromString("");
    private static Input input1 = Input.fromString("a");

    @Test
    public void testParser0() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input0, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input0, graph, S);
        assertEquals(getParseResult0(graph), result);
    }

    @Test
    public void testParser1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
    }

    private static ParseResult getParseResult0(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(3)
                .setGSSNodesCount(2)
                .setGSSEdgesCount(1)
                .setNonterminalNodesCount(2)
                .setTerminalNodesCount(1)
                .setIntermediateNodesCount(0)
                .setPackedNodesCount(2)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF0(new SPPFNodeFactory(graph)), statistics, input0);
    }

    private static NonterminalNode expectedSPPF0(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("epsilon", 0, 0, input0);
        NonterminalNode node1 = factory.createNonterminalNode("A?", "A? ::= .", node0, input0);
        NonterminalNode node2 = factory.createNonterminalNode("S", "S ::= A? .", node1, input0);
        return node2;
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
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0, input1);
        NonterminalNode node2 = factory.createNonterminalNode("A?", "A? ::= A .", node1, input1);
        NonterminalNode node3 = factory.createNonterminalNode("S", "S ::= A? .", node2, input1);
        return node3;
    }

}
