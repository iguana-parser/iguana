package org.iguana.parser.ebnf;

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * S ::= A+
 *
 * A ::= 'a'
 *
 */
public class Test4 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Char.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(Plus.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();

    static Start start = Start.from(S);
    private static Grammar grammar = EBNFToBNF.convert(new DesugarStartSymbol().transform(Grammar.builder().addRules(r1, r2).setStartSymbol(start).build()));

    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("aa");
    private static Input input3 = Input.fromString("aaa");

    @Test
    public void testParser1() {
        ParseResult result = Iguana.parse(input1, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        ParseResult result = Iguana.parse(input2, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        ParseResult result = Iguana.parse(input3, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult3(graph), result);
    }

    private static ParseResult getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(6)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(3)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(1)
                .setIntermediateNodesCount(0)
                .setPackedNodesCount(3)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A+", "A+ ::= A .", node1);
        NonterminalNode node3 = factory.createNonterminalNode("S", "S ::= A+ .", node2);
        return node3;
    }

    private static ParseResult getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(9)
                .setGSSNodesCount(4)
                .setGSSEdgesCount(4)
                .setNonterminalNodesCount(5)
                .setTerminalNodesCount(2)
                .setIntermediateNodesCount(1)
                .setPackedNodesCount(6)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A+", "A+ ::= A .", node1);
        TerminalNode node3 = factory.createTerminalNode("a", 1, 2);
        NonterminalNode node4 = factory.createNonterminalNode("A", "A ::= a .", node3);
        IntermediateNode node5 = factory.createIntermediateNode("A+ ::= A+ A .", node2, node4);
        NonterminalNode node6 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node5);
        NonterminalNode node7 = factory.createNonterminalNode("S", "S ::= A+ .", node6);
        return node7;
    }

    private static ParseResult getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(12)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(7)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(9)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A+", "A+ ::= A .", node1);
        TerminalNode node3 = factory.createTerminalNode("a", 1, 2);
        NonterminalNode node4 = factory.createNonterminalNode("A", "A ::= a .", node3);
        IntermediateNode node5 = factory.createIntermediateNode("A+ ::= A+ A .", node2, node4);
        NonterminalNode node6 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node5);
        TerminalNode node7 = factory.createTerminalNode("a", 2, 3);
        NonterminalNode node8 = factory.createNonterminalNode("A", "A ::= a .", node7);
        IntermediateNode node9 = factory.createIntermediateNode("A+ ::= A+ A .", node6, node8);
        NonterminalNode node10 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node9);
        NonterminalNode node11 = factory.createNonterminalNode("S", "S ::= A+ .", node10);
        return node11;
    }

}
