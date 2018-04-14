package org.iguana.parser.basic;

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.ParseStatistics;
import org.iguana.util.TestRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * E ::= E '+' E
 *     | 'a'
 */
public class Test18 {

    static Nonterminal E = Nonterminal.withName("E");
    static Terminal a = Terminal.from(Char.from('a'));
    static Terminal plus = Terminal.from(Char.from('+'));

    static Rule r1 = Rule.withHead(E).addSymbols(E, plus, E).build();
    static Rule r2 = Rule.withHead(E).addSymbols(a).build();

    private static Start startSymbol = Start.from(E);
    public static Grammar grammar = new DesugarStartSymbol().transform(Grammar.builder().addRules(r1, r2).setStartSymbol(startSymbol).build());

    private static Input input1 = Input.fromString("a+a");
    private static Input input2 = Input.fromString("a+a+a");
    private static Input input3 = Input.fromString("a+a+a+a+a");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "basic").toAbsolutePath().toString();
        TestRunner.record(grammar, input1, 1, path + "/Test18");
        TestRunner.record(grammar, input2, 2, path + "/Test18");
        TestRunner.record(grammar, input3, 3, path + "/Test18");
    }

    @Test
    public void testParser1() {
        ParseResult result = Iguana.parse(input1, grammar);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        ParseResult result = Iguana.parse(input2, grammar);
        assertTrue(result.isParseSuccess());
//        GrammarGraph graph = GrammarGraph.from(grammar, input2, new ParserResultOps());
//        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        ParseResult result = Iguana.parse(input3, grammar);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult3(graph), result);
    }

    private static ParseSuccess getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(6)
                .setGSSNodesCount(2)
                .setGSSEdgesCount(3)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(5)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("E", "E ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("+", 1, 2);
        IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", node1, node2);
        TerminalNode node4 = factory.createTerminalNode("a", 2, 3);
        NonterminalNode node5 = factory.createNonterminalNode("E", "E ::= a .", node4);
        IntermediateNode node6 = factory.createIntermediateNode("E ::= E + E .", node3, node5);
        NonterminalNode node7 = factory.createNonterminalNode("E", "E ::= E + E .", node6);
        return node7;
    }

    private static ParseSuccess getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(12)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(6)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(5)
                .setIntermediateNodesCount(6)
                .setPackedNodesCount(13)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("E", "E ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("+", 1, 2);
        IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", node1, node2);
        TerminalNode node4 = factory.createTerminalNode("a", 2, 3);
        NonterminalNode node5 = factory.createNonterminalNode("E", "E ::= a .", node4);
        IntermediateNode node6 = factory.createIntermediateNode("E ::= E + E .", node3, node5);
        NonterminalNode node7 = factory.createNonterminalNode("E", "E ::= E + E .", node6);
        TerminalNode node8 = factory.createTerminalNode("+", 3, 4);
        IntermediateNode node9 = factory.createIntermediateNode("E ::= E + . E", node7, node8);
        TerminalNode node10 = factory.createTerminalNode("a", 4, 5);
        NonterminalNode node11 = factory.createNonterminalNode("E", "E ::= a .", node10);
        IntermediateNode node12 = factory.createIntermediateNode("E ::= E + . E", node5, node8);
        IntermediateNode node13 = factory.createIntermediateNode("E ::= E + E .", node12, node11);
        NonterminalNode node14 = factory.createNonterminalNode("E", "E ::= E + E .", node13);
        IntermediateNode node15 = factory.createIntermediateNode("E ::= E + E .", node9, node11);
        node15.addPackedNode(factory.createPackedNode("E ::= E + E .", node3, node14));
        NonterminalNode node16 = factory.createNonterminalNode("E", "E ::= E + E .", node15);
        return node16;
    }

    private static ParseSuccess getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(30)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(15)
                .setNonterminalNodesCount(15)
                .setTerminalNodesCount(9)
                .setIntermediateNodesCount(20)
                .setPackedNodesCount(45)
                .setAmbiguousNodesCount(6).build();
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("E", "E ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("+", 1, 2);
        IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", node1, node2);
        TerminalNode node4 = factory.createTerminalNode("a", 2, 3);
        NonterminalNode node5 = factory.createNonterminalNode("E", "E ::= a .", node4);
        IntermediateNode node6 = factory.createIntermediateNode("E ::= E + E .", node3, node5);
        NonterminalNode node7 = factory.createNonterminalNode("E", "E ::= E + E .", node6);
        TerminalNode node8 = factory.createTerminalNode("+", 3, 4);
        IntermediateNode node9 = factory.createIntermediateNode("E ::= E + . E", node7, node8);
        TerminalNode node10 = factory.createTerminalNode("a", 4, 5);
        NonterminalNode node11 = factory.createNonterminalNode("E", "E ::= a .", node10);
        IntermediateNode node12 = factory.createIntermediateNode("E ::= E + . E", node5, node8);
        IntermediateNode node13 = factory.createIntermediateNode("E ::= E + E .", node12, node11);
        NonterminalNode node14 = factory.createNonterminalNode("E", "E ::= E + E .", node13);
        IntermediateNode node15 = factory.createIntermediateNode("E ::= E + E .", node9, node11);
        node15.addPackedNode(factory.createPackedNode("E ::= E + E .", node3, node14));
        NonterminalNode node16 = factory.createNonterminalNode("E", "E ::= E + E .", node15);
        TerminalNode node17 = factory.createTerminalNode("+", 5, 6);
        IntermediateNode node18 = factory.createIntermediateNode("E ::= E + . E", node16, node17);
        TerminalNode node19 = factory.createTerminalNode("a", 6, 7);
        NonterminalNode node20 = factory.createNonterminalNode("E", "E ::= a .", node19);
        IntermediateNode node21 = factory.createIntermediateNode("E ::= E + . E", node11, node17);
        IntermediateNode node22 = factory.createIntermediateNode("E ::= E + E .", node21, node20);
        NonterminalNode node23 = factory.createNonterminalNode("E", "E ::= E + E .", node22);
        IntermediateNode node24 = factory.createIntermediateNode("E ::= E + . E", node14, node17);
        IntermediateNode node25 = factory.createIntermediateNode("E ::= E + E .", node12, node23);
        node25.addPackedNode(factory.createPackedNode("E ::= E + E .", node24, node20));
        NonterminalNode node26 = factory.createNonterminalNode("E", "E ::= E + E .", node25);
        IntermediateNode node27 = factory.createIntermediateNode("E ::= E + E .", node18, node20);
        node27.addPackedNode(factory.createPackedNode("E ::= E + E .", node9, node23));
        node27.addPackedNode(factory.createPackedNode("E ::= E + E .", node3, node26));
        NonterminalNode node28 = factory.createNonterminalNode("E", "E ::= E + E .", node27);
        TerminalNode node29 = factory.createTerminalNode("+", 7, 8);
        IntermediateNode node30 = factory.createIntermediateNode("E ::= E + . E", node28, node29);
        TerminalNode node31 = factory.createTerminalNode("a", 8, 9);
        NonterminalNode node32 = factory.createNonterminalNode("E", "E ::= a .", node31);
        IntermediateNode node33 = factory.createIntermediateNode("E ::= E + . E", node20, node29);
        IntermediateNode node34 = factory.createIntermediateNode("E ::= E + E .", node33, node32);
        NonterminalNode node35 = factory.createNonterminalNode("E", "E ::= E + E .", node34);
        IntermediateNode node36 = factory.createIntermediateNode("E ::= E + . E", node23, node29);
        IntermediateNode node37 = factory.createIntermediateNode("E ::= E + E .", node21, node35);
        node37.addPackedNode(factory.createPackedNode("E ::= E + E .", node36, node32));
        NonterminalNode node38 = factory.createNonterminalNode("E", "E ::= E + E .", node37);
        IntermediateNode node39 = factory.createIntermediateNode("E ::= E + . E", node26, node29);
        IntermediateNode node40 = factory.createIntermediateNode("E ::= E + E .", node12, node38);
        node40.addPackedNode(factory.createPackedNode("E ::= E + E .", node39, node32));
        node40.addPackedNode(factory.createPackedNode("E ::= E + E .", node24, node35));
        NonterminalNode node41 = factory.createNonterminalNode("E", "E ::= E + E .", node40);
        IntermediateNode node42 = factory.createIntermediateNode("E ::= E + E .", node30, node32);
        node42.addPackedNode(factory.createPackedNode("E ::= E + E .", node18, node35));
        node42.addPackedNode(factory.createPackedNode("E ::= E + E .", node9, node38));
        node42.addPackedNode(factory.createPackedNode("E ::= E + E .", node3, node41));
        NonterminalNode node43 = factory.createNonterminalNode("E", "E ::= E + E .", node42);
        return node43;
    }

}
