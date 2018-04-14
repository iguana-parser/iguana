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
import org.iguana.util.TestRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A ::= 'a'*
 */
public class Test1 {

    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Char.from('a'));
    static Start start = Start.from(A);
    static Rule r1 = Rule.withHead(A).addSymbols(Star.from(a)).build();

    private static Grammar grammar = new DesugarStartSymbol().transform(EBNFToBNF.convert(Grammar.builder().addRule(r1).setStartSymbol(start).build()));
    private static Input input0 = Input.fromString("");
    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("aa");
    private static Input input3 = Input.fromString("aaa");
    private static Input input4 = Input.fromString("aaaaaaaaaaa");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "ebnf").toAbsolutePath().toString();
        TestRunner.record(grammar, input0, 1, path + "/Test1");
        TestRunner.record(grammar, input1, 2, path + "/Test1");
        TestRunner.record(grammar, input2, 3, path + "/Test1");
        TestRunner.record(grammar, input3, 4, path + "/Test1");
        TestRunner.record(grammar, input4, 5, path + "/Test1");
    }


    @Test
    public void testParser0() {
        ParseResult result = Iguana.parse(input0, grammar, A);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult0(graph), result);
    }

    @Test
    public void testParser1() {
        ParseResult result = Iguana.parse(input1, grammar, A);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        ParseResult result = Iguana.parse(input2, grammar, A);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        ParseResult result = Iguana.parse(input3, grammar, A);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult3(graph), result);
    }

    @Test
    public void testParser4() {
        ParseResult result = Iguana.parse(input4, grammar, A);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult4(graph), result);
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
        return new ParseSuccess(expectedSPPF0(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF0(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("epsilon", 0, 0);
        NonterminalNode node1 = factory.createNonterminalNode("a*", "a* ::= .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A", "A ::= a* .", node1);
        return node2;
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
        NonterminalNode node1 = factory.createNonterminalNode("a+", "a+ ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("a*", "a* ::= a+ .", node1);
        NonterminalNode node3 = factory.createNonterminalNode("A", "A ::= a* .", node2);
        return node3;
    }

    private static ParseResult getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(7)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(3)
                .setNonterminalNodesCount(4)
                .setTerminalNodesCount(2)
                .setIntermediateNodesCount(1)
                .setPackedNodesCount(5)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("a+", "a+ ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("a", 1, 2);
        IntermediateNode node3 = factory.createIntermediateNode("a+ ::= a+ a .", node1, node2);
        NonterminalNode node4 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node3);
        NonterminalNode node5 = factory.createNonterminalNode("a*", "a* ::= a+ .", node4);
        NonterminalNode node6 = factory.createNonterminalNode("A", "A ::= a* .", node5);
        return node6;
    }

    private static ParseResult getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(8)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(3)
                .setNonterminalNodesCount(5)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(7)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("a+", "a+ ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("a", 1, 2);
        IntermediateNode node3 = factory.createIntermediateNode("a+ ::= a+ a .", node1, node2);
        NonterminalNode node4 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node3);
        TerminalNode node5 = factory.createTerminalNode("a", 2, 3);
        IntermediateNode node6 = factory.createIntermediateNode("a+ ::= a+ a .", node4, node5);
        NonterminalNode node7 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node6);
        NonterminalNode node8 = factory.createNonterminalNode("a*", "a* ::= a+ .", node7);
        NonterminalNode node9 = factory.createNonterminalNode("A", "A ::= a* .", node8);
        return node9;
    }

    private static ParseResult getParseResult4(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(16)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(3)
                .setNonterminalNodesCount(13)
                .setTerminalNodesCount(11)
                .setIntermediateNodesCount(10)
                .setPackedNodesCount(23)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF4(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF4(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("a+", "a+ ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("a", 1, 2);
        IntermediateNode node3 = factory.createIntermediateNode("a+ ::= a+ a .", node1, node2);
        NonterminalNode node4 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node3);
        TerminalNode node5 = factory.createTerminalNode("a", 2, 3);
        IntermediateNode node6 = factory.createIntermediateNode("a+ ::= a+ a .", node4, node5);
        NonterminalNode node7 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node6);
        TerminalNode node8 = factory.createTerminalNode("a", 3, 4);
        IntermediateNode node9 = factory.createIntermediateNode("a+ ::= a+ a .", node7, node8);
        NonterminalNode node10 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node9);
        TerminalNode node11 = factory.createTerminalNode("a", 4, 5);
        IntermediateNode node12 = factory.createIntermediateNode("a+ ::= a+ a .", node10, node11);
        NonterminalNode node13 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node12);
        TerminalNode node14 = factory.createTerminalNode("a", 5, 6);
        IntermediateNode node15 = factory.createIntermediateNode("a+ ::= a+ a .", node13, node14);
        NonterminalNode node16 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node15);
        TerminalNode node17 = factory.createTerminalNode("a", 6, 7);
        IntermediateNode node18 = factory.createIntermediateNode("a+ ::= a+ a .", node16, node17);
        NonterminalNode node19 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node18);
        TerminalNode node20 = factory.createTerminalNode("a", 7, 8);
        IntermediateNode node21 = factory.createIntermediateNode("a+ ::= a+ a .", node19, node20);
        NonterminalNode node22 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node21);
        TerminalNode node23 = factory.createTerminalNode("a", 8, 9);
        IntermediateNode node24 = factory.createIntermediateNode("a+ ::= a+ a .", node22, node23);
        NonterminalNode node25 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node24);
        TerminalNode node26 = factory.createTerminalNode("a", 9, 10);
        IntermediateNode node27 = factory.createIntermediateNode("a+ ::= a+ a .", node25, node26);
        NonterminalNode node28 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node27);
        TerminalNode node29 = factory.createTerminalNode("a", 10, 11);
        IntermediateNode node30 = factory.createIntermediateNode("a+ ::= a+ a .", node28, node29);
        NonterminalNode node31 = factory.createNonterminalNode("a+", "a+ ::= a+ a .", node30);
        NonterminalNode node32 = factory.createNonterminalNode("a*", "a* ::= a+ .", node31);
        NonterminalNode node33 = factory.createNonterminalNode("A", "A ::= a* .", node32);
        return node33;
    }

}