package org.iguana.parser.ebnf;

import iguana.regex.Character;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * S ::= A*
 * A ::= 'a'
 */
public class Test2 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Character.from('a'));

    static Rule r1 = Rule.withHead(S).addSymbols(Star.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(Terminal.from(Character.from('a'))).build();

    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();

    private static Input input0 = Input.fromString("");
    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("aa");
    private static Input input3 = Input.fromString("aaaaaaaaaaa");

    @Test
    public void testParser0() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input0, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input0, graph, S);
        assertTrue(result.isParseSuccess());
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

    @Test
    public void testParser2() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input2, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input2, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input3, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input3, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
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
        NonterminalNode node1 = factory.createNonterminalNode("A*", "A* ::= .", node0, input0);
        NonterminalNode node2 = factory.createNonterminalNode("S", "S ::= A* .", node1, input0);
        return node2;
    }

    private static ParseResult getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(8)
                .setGSSNodesCount(4)
                .setGSSEdgesCount(4)
                .setNonterminalNodesCount(4)
                .setTerminalNodesCount(1)
                .setIntermediateNodesCount(0)
                .setPackedNodesCount(4)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input0);
    }

    private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0, input1);
        NonterminalNode node2 = factory.createNonterminalNode("A+", "A+ ::= A .", node1, input1);
        NonterminalNode node3 = factory.createNonterminalNode("A*", "A* ::= A+ .", node2, input1);
        NonterminalNode node4 = factory.createNonterminalNode("S", "S ::= A* .", node3, input1);
        return node4;
    }

    private static ParseResult getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(11)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(2)
                .setIntermediateNodesCount(1)
                .setPackedNodesCount(7)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics, input0);
    }

    private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input2);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0, input2);
        NonterminalNode node2 = factory.createNonterminalNode("A+", "A+ ::= A .", node1, input2);
        TerminalNode node3 = factory.createTerminalNode("a", 1, 2, input2);
        NonterminalNode node4 = factory.createNonterminalNode("A", "A ::= a .", node3, input2);
        IntermediateNode node5 = factory.createIntermediateNode("A+ ::= A+ A .", node2, node4);
        NonterminalNode node6 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node5, input2);
        NonterminalNode node7 = factory.createNonterminalNode("A*", "A* ::= A+ .", node6, input2);
        NonterminalNode node8 = factory.createNonterminalNode("S", "S ::= A* .", node7, input2);
        return node8;
    }

    private static ParseResult getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(38)
                .setGSSNodesCount(14)
                .setGSSEdgesCount(14)
                .setNonterminalNodesCount(24)
                .setTerminalNodesCount(11)
                .setIntermediateNodesCount(10)
                .setPackedNodesCount(34)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics, input0);
    }

    private static NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input3);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0, input3);
        NonterminalNode node2 = factory.createNonterminalNode("A+", "A+ ::= A .", node1, input3);
        TerminalNode node3 = factory.createTerminalNode("a", 1, 2, input3);
        NonterminalNode node4 = factory.createNonterminalNode("A", "A ::= a .", node3, input3);
        IntermediateNode node5 = factory.createIntermediateNode("A+ ::= A+ A .", node2, node4);
        NonterminalNode node6 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node5, input3);
        TerminalNode node7 = factory.createTerminalNode("a", 2, 3, input3);
        NonterminalNode node8 = factory.createNonterminalNode("A", "A ::= a .", node7, input3);
        IntermediateNode node9 = factory.createIntermediateNode("A+ ::= A+ A .", node6, node8);
        NonterminalNode node10 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node9, input3);
        TerminalNode node11 = factory.createTerminalNode("a", 3, 4, input3);
        NonterminalNode node12 = factory.createNonterminalNode("A", "A ::= a .", node11, input3);
        IntermediateNode node13 = factory.createIntermediateNode("A+ ::= A+ A .", node10, node12);
        NonterminalNode node14 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node13, input3);
        TerminalNode node15 = factory.createTerminalNode("a", 4, 5, input3);
        NonterminalNode node16 = factory.createNonterminalNode("A", "A ::= a .", node15, input3);
        IntermediateNode node17 = factory.createIntermediateNode("A+ ::= A+ A .", node14, node16);
        NonterminalNode node18 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node17, input3);
        TerminalNode node19 = factory.createTerminalNode("a", 5, 6, input3);
        NonterminalNode node20 = factory.createNonterminalNode("A", "A ::= a .", node19, input3);
        IntermediateNode node21 = factory.createIntermediateNode("A+ ::= A+ A .", node18, node20);
        NonterminalNode node22 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node21, input3);
        TerminalNode node23 = factory.createTerminalNode("a", 6, 7, input3);
        NonterminalNode node24 = factory.createNonterminalNode("A", "A ::= a .", node23, input3);
        IntermediateNode node25 = factory.createIntermediateNode("A+ ::= A+ A .", node22, node24);
        NonterminalNode node26 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node25, input3);
        TerminalNode node27 = factory.createTerminalNode("a", 7, 8, input3);
        NonterminalNode node28 = factory.createNonterminalNode("A", "A ::= a .", node27, input3);
        IntermediateNode node29 = factory.createIntermediateNode("A+ ::= A+ A .", node26, node28);
        NonterminalNode node30 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node29, input3);
        TerminalNode node31 = factory.createTerminalNode("a", 8, 9, input3);
        NonterminalNode node32 = factory.createNonterminalNode("A", "A ::= a .", node31, input3);
        IntermediateNode node33 = factory.createIntermediateNode("A+ ::= A+ A .", node30, node32);
        NonterminalNode node34 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node33, input3);
        TerminalNode node35 = factory.createTerminalNode("a", 9, 10, input3);
        NonterminalNode node36 = factory.createNonterminalNode("A", "A ::= a .", node35, input3);
        IntermediateNode node37 = factory.createIntermediateNode("A+ ::= A+ A .", node34, node36);
        NonterminalNode node38 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node37, input3);
        TerminalNode node39 = factory.createTerminalNode("a", 10, 11, input3);
        NonterminalNode node40 = factory.createNonterminalNode("A", "A ::= a .", node39, input3);
        IntermediateNode node41 = factory.createIntermediateNode("A+ ::= A+ A .", node38, node40);
        NonterminalNode node42 = factory.createNonterminalNode("A+", "A+ ::= A+ A .", node41, input3);
        NonterminalNode node43 = factory.createNonterminalNode("A*", "A* ::= A+ .", node42, input3);
        NonterminalNode node44 = factory.createNonterminalNode("S", "S ::= A* .", node43, input3);
        return node44;
    }

}
