package org.iguana.parser.basic;

import iguana.regex.Character;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
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
 * E ::= E '*' E
 *     | E '+' E
 *     | 'a'
 */
public class Test19 {

    static Nonterminal E = Nonterminal.withName("E");
    static Terminal a = Terminal.from(Character.from('a'));
    static Terminal plus = Terminal.from(Character.from('+'));
    static Terminal star = Terminal.from(Character.from('*'));

    static Rule r1 = Rule.withHead(E).addSymbols(E, star, E).build();
    static Rule r2 = Rule.withHead(E).addSymbols(E, plus, E).build();
    static Rule r3 = Rule.withHead(E).addSymbols(a).build();

    public static Grammar grammar = Grammar.builder().addRules(r1, r2, r3).build();
    private static Nonterminal startSymbol = E;

    private static Input input1 = Input.fromString("a+a");
    private static Input input2 = Input.fromString("a+a*a");
    private static Input input3 = Input.fromString("a+a*a+a*a");

    @Test
    public void testParser1() {
        GrammarGraph graph = GrammarGraph.from(grammar, input1);
        ParseResult result = Iguana.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        GrammarGraph graph = GrammarGraph.from(grammar, input3);
        ParseResult result = Iguana.parse(input3, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
    }

    private ParseSuccess getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(8)
                .setGSSNodesCount(2)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(5)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input1);
    }

    private NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input1);
        NonterminalNode node1 = factory.createNonterminalNode("E", "E ::= a .", node0, input1);
        TerminalNode node2 = factory.createTerminalNode("+", 1, 2, input1);
        IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", node1, node2);
        TerminalNode node4 = factory.createTerminalNode("a", 2, 3, input1);
        NonterminalNode node5 = factory.createNonterminalNode("E", "E ::= a .", node4, input1);
        IntermediateNode node6 = factory.createIntermediateNode("E ::= E + E .", node3, node5);
        NonterminalNode node7 = factory.createNonterminalNode("E", "E ::= E + E .", node6, input1);
        return node7;
    }

    private ParseSuccess getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(16)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(9)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(5)
                .setIntermediateNodesCount(7)
                .setPackedNodesCount(14)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics, input2);
    }

    private NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input2);
        NonterminalNode node1 = factory.createNonterminalNode("E", "E ::= a .", node0, input2);
        TerminalNode node2 = factory.createTerminalNode("+", 1, 2, input2);
        IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", node1, node2);
        TerminalNode node4 = factory.createTerminalNode("a", 2, 3, input2);
        NonterminalNode node5 = factory.createNonterminalNode("E", "E ::= a .", node4, input2);
        TerminalNode node6 = factory.createTerminalNode("*", 3, 4, input2);
        IntermediateNode node7 = factory.createIntermediateNode("E ::= E * . E", node5, node6);
        TerminalNode node8 = factory.createTerminalNode("a", 4, 5, input2);
        NonterminalNode node9 = factory.createNonterminalNode("E", "E ::= a .", node8, input2);
        IntermediateNode node10 = factory.createIntermediateNode("E ::= E * E .", node7, node9);
        NonterminalNode node11 = factory.createNonterminalNode("E", "E ::= E * E .", node10, input2);
        IntermediateNode node12 = factory.createIntermediateNode("E ::= E + E .", node3, node11);
        IntermediateNode node13 = factory.createIntermediateNode("E ::= E + E .", node3, node5);
        NonterminalNode node14 = factory.createNonterminalNode("E", "E ::= E + E .", node13, input2);
        IntermediateNode node15 = factory.createIntermediateNode("E ::= E * . E", node14, node6);
        IntermediateNode node16 = factory.createIntermediateNode("E ::= E * E .", node15, node9);
        NonterminalNode node17 = factory.createNonterminalNode("E", "E ::= E + E .", node12, input2);
        node17.addPackedNode(factory.createPackedNode("E ::= E * E .", node16));
        return node17;
    }

    private ParseSuccess getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(41)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(20)
                .setNonterminalNodesCount(15)
                .setTerminalNodesCount(9)
                .setIntermediateNodesCount(26)
                .setPackedNodesCount(51)
                .setAmbiguousNodesCount(10).build();
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics, input3);
    }

    private NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input3);
        NonterminalNode node1 = factory.createNonterminalNode("E", "E ::= a .", node0, input3);
        TerminalNode node2 = factory.createTerminalNode("+", 1, 2, input3);
        IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", node1, node2);
        TerminalNode node4 = factory.createTerminalNode("a", 2, 3, input3);
        NonterminalNode node5 = factory.createNonterminalNode("E", "E ::= a .", node4, input3);
        TerminalNode node6 = factory.createTerminalNode("*", 3, 4, input3);
        IntermediateNode node7 = factory.createIntermediateNode("E ::= E * . E", node5, node6);
        TerminalNode node8 = factory.createTerminalNode("a", 4, 5, input3);
        NonterminalNode node9 = factory.createNonterminalNode("E", "E ::= a .", node8, input3);
        IntermediateNode node10 = factory.createIntermediateNode("E ::= E * E .", node7, node9);
        NonterminalNode node11 = factory.createNonterminalNode("E", "E ::= E * E .", node10, input3);
        TerminalNode node12 = factory.createTerminalNode("+", 5, 6, input3);
        IntermediateNode node13 = factory.createIntermediateNode("E ::= E + . E", node11, node12);
        TerminalNode node14 = factory.createTerminalNode("a", 6, 7, input3);
        NonterminalNode node15 = factory.createNonterminalNode("E", "E ::= a .", node14, input3);
        IntermediateNode node16 = factory.createIntermediateNode("E ::= E + E .", node13, node15);
        IntermediateNode node17 = factory.createIntermediateNode("E ::= E + . E", node9, node12);
        IntermediateNode node18 = factory.createIntermediateNode("E ::= E + E .", node17, node15);
        NonterminalNode node19 = factory.createNonterminalNode("E", "E ::= E + E .", node18, input3);
        IntermediateNode node20 = factory.createIntermediateNode("E ::= E * E .", node7, node19);
        NonterminalNode node21 = factory.createNonterminalNode("E", "E ::= E + E .", node16, input3);
        node21.addPackedNode(factory.createPackedNode("E ::= E * E .", node20));
        TerminalNode node22 = factory.createTerminalNode("*", 7, 8, input3);
        IntermediateNode node23 = factory.createIntermediateNode("E ::= E * . E", node21, node22);
        TerminalNode node24 = factory.createTerminalNode("a", 8, 9, input3);
        NonterminalNode node25 = factory.createNonterminalNode("E", "E ::= a .", node24, input3);
        IntermediateNode node26 = factory.createIntermediateNode("E ::= E * . E", node15, node22);
        IntermediateNode node27 = factory.createIntermediateNode("E ::= E * E .", node26, node25);
        NonterminalNode node28 = factory.createNonterminalNode("E", "E ::= E * E .", node27, input3);
        IntermediateNode node29 = factory.createIntermediateNode("E ::= E + E .", node17, node28);
        IntermediateNode node30 = factory.createIntermediateNode("E ::= E * . E", node19, node22);
        IntermediateNode node31 = factory.createIntermediateNode("E ::= E * E .", node30, node25);
        NonterminalNode node32 = factory.createNonterminalNode("E", "E ::= E + E .", node29, input3);
        node32.addPackedNode(factory.createPackedNode("E ::= E * E .", node31));
        IntermediateNode node33 = factory.createIntermediateNode("E ::= E * E .", node23, node25);
        node33.addPackedNode(factory.createPackedNode("E ::= E * E .", node7, node32));
        IntermediateNode node34 = factory.createIntermediateNode("E ::= E + E .", node13, node28);
        NonterminalNode node35 = factory.createNonterminalNode("E", "E ::= E * E .", node33, input3);
        node35.addPackedNode(factory.createPackedNode("E ::= E + E .", node34));
        IntermediateNode node36 = factory.createIntermediateNode("E ::= E + E .", node3, node11);
        IntermediateNode node37 = factory.createIntermediateNode("E ::= E + E .", node3, node5);
        NonterminalNode node38 = factory.createNonterminalNode("E", "E ::= E + E .", node37, input3);
        IntermediateNode node39 = factory.createIntermediateNode("E ::= E * . E", node38, node6);
        IntermediateNode node40 = factory.createIntermediateNode("E ::= E * E .", node39, node9);
        NonterminalNode node41 = factory.createNonterminalNode("E", "E ::= E + E .", node36, input3);
        node41.addPackedNode(factory.createPackedNode("E ::= E * E .", node40));
        IntermediateNode node42 = factory.createIntermediateNode("E ::= E + . E", node41, node12);
        IntermediateNode node43 = factory.createIntermediateNode("E ::= E + E .", node3, node35);
        node43.addPackedNode(factory.createPackedNode("E ::= E + E .", node42, node28));
        IntermediateNode node44 = factory.createIntermediateNode("E ::= E + E .", node3, node21);
        node44.addPackedNode(factory.createPackedNode("E ::= E + E .", node42, node15));
        IntermediateNode node45 = factory.createIntermediateNode("E ::= E * E .", node39, node19);
        NonterminalNode node46 = factory.createNonterminalNode("E", "E ::= E + E .", node44, input3);
        node46.addPackedNode(factory.createPackedNode("E ::= E * E .", node45));
        IntermediateNode node47 = factory.createIntermediateNode("E ::= E * . E", node46, node22);
        IntermediateNode node48 = factory.createIntermediateNode("E ::= E * E .", node47, node25);
        node48.addPackedNode(factory.createPackedNode("E ::= E * E .", node39, node32));
        NonterminalNode node49 = factory.createNonterminalNode("E", "E ::= E + E .", node43, input3);
        node49.addPackedNode(factory.createPackedNode("E ::= E * E .", node48));
        return node49;
    }

}
