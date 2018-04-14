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
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.iguana.util.TestRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * S ::= A? B+ C*
 *
 * A ::= 'a'
 * B ::= 'b'
 * C ::= 'c'
 *
 */
public class Test7 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Nonterminal B = Nonterminal.withName("B");
    static Nonterminal C = Nonterminal.withName("C");
    static Terminal a = Terminal.from(Char.from('a'));
    static Terminal b = Terminal.from(Char.from('b'));
    static Terminal c = Terminal.from(Char.from('c'));
    static Rule r1 = Rule.withHead(S).addSymbols(Opt.from(A), Plus.from(B), Star.from(C)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    static Rule r3 = Rule.withHead(B).addSymbols(b).build();
    static Rule r4 = Rule.withHead(C).addSymbols(c).build();

    static Start start = Start.from(S);
    private static Grammar grammar = EBNFToBNF.convert(new DesugarStartSymbol().transform(Grammar.builder().addRules(r1, r2, r3, r4).setStartSymbol(start).build()));

    private static Input input1 = Input.fromString("b");
    private static Input input2 = Input.fromString("ab");
    private static Input input3 = Input.fromString("bc");
    private static Input input4 = Input.fromString("abc");
    private static Input input5 = Input.fromString("abbbbccccc");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "ebnf").toAbsolutePath().toString();
        TestRunner.record(grammar, input1, 1, path + "/Test7");
        TestRunner.record(grammar, input2, 2, path + "/Test7");
        TestRunner.record(grammar, input3, 3, path + "/Test7");
        TestRunner.record(grammar, input4, 4, path + "/Test7");
        TestRunner.record(grammar, input5, 5, path + "/Test7");
    }

    @Test
    public void testParse1() {
        ParseResult result = Iguana.parse(input1, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParse2() {
        ParseResult result = Iguana.parse(input2, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParse3() {
        ParseResult result = Iguana.parse(input3, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult3(graph), result);
    }

    @Test
    public void testParse4() {
        ParseResult result = Iguana.parse(input4, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult4(graph), result);
    }

    @Test
    public void testParse5() {
        ParseResult result = Iguana.parse(input5, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult5(graph), result);
    }

    private static ParseResult getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(10)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(5)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(7)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("epsilon", 0, 0);
        NonterminalNode node1 = factory.createNonterminalNode("A?", "A? ::= .", node0);
        TerminalNode node2 = factory.createTerminalNode("b", 0, 1);
        NonterminalNode node3 = factory.createNonterminalNode("B", "B ::= b .", node2);
        NonterminalNode node4 = factory.createNonterminalNode("B+", "B+ ::= B .", node3);
        IntermediateNode node5 = factory.createIntermediateNode("S ::= A? B+ . C*", node1, node4);
        TerminalNode node6 = factory.createTerminalNode("epsilon", 1, 1);
        NonterminalNode node7 = factory.createNonterminalNode("C*", "C* ::= .", node6);
        IntermediateNode node8 = factory.createIntermediateNode("S ::= A? B+ C* .", node5, node7);
        NonterminalNode node9 = factory.createNonterminalNode("S", "S ::= A? B+ C* .", node8);
        return node9;
    }

    private static ParseResult getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(12)
                .setGSSNodesCount(6)
                .setGSSEdgesCount(6)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(8)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A?", "A? ::= A .", node1);
        TerminalNode node3 = factory.createTerminalNode("b", 1, 2);
        NonterminalNode node4 = factory.createNonterminalNode("B", "B ::= b .", node3);
        NonterminalNode node5 = factory.createNonterminalNode("B+", "B+ ::= B .", node4);
        IntermediateNode node6 = factory.createIntermediateNode("S ::= A? B+ . C*", node2, node5);
        TerminalNode node7 = factory.createTerminalNode("epsilon", 2, 2);
        NonterminalNode node8 = factory.createNonterminalNode("C*", "C* ::= .", node7);
        IntermediateNode node9 = factory.createIntermediateNode("S ::= A? B+ C* .", node6, node8);
        NonterminalNode node10 = factory.createNonterminalNode("S", "S ::= A? B+ C* .", node9);
        return node10;
    }

    private static ParseResult getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(15)
                .setGSSNodesCount(7)
                .setGSSEdgesCount(8)
                .setNonterminalNodesCount(7)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(9)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("epsilon", 0, 0);
        NonterminalNode node1 = factory.createNonterminalNode("A?", "A? ::= .", node0);
        TerminalNode node2 = factory.createTerminalNode("b", 0, 1);
        NonterminalNode node3 = factory.createNonterminalNode("B", "B ::= b .", node2);
        NonterminalNode node4 = factory.createNonterminalNode("B+", "B+ ::= B .", node3);
        IntermediateNode node5 = factory.createIntermediateNode("S ::= A? B+ . C*", node1, node4);
        TerminalNode node6 = factory.createTerminalNode("c", 1, 2);
        NonterminalNode node7 = factory.createNonterminalNode("C", "C ::= c .", node6);
        NonterminalNode node8 = factory.createNonterminalNode("C+", "C+ ::= C .", node7);
        NonterminalNode node9 = factory.createNonterminalNode("C*", "C* ::= C+ .", node8);
        IntermediateNode node10 = factory.createIntermediateNode("S ::= A? B+ C* .", node5, node9);
        NonterminalNode node11 = factory.createNonterminalNode("S", "S ::= A? B+ C* .", node10);
        return node11;
    }

    private static ParseResult getParseResult4(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(17)
                .setGSSNodesCount(8)
                .setGSSEdgesCount(9)
                .setNonterminalNodesCount(8)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(10)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF4(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF4(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A?", "A? ::= A .", node1);
        TerminalNode node3 = factory.createTerminalNode("b", 1, 2);
        NonterminalNode node4 = factory.createNonterminalNode("B", "B ::= b .", node3);
        NonterminalNode node5 = factory.createNonterminalNode("B+", "B+ ::= B .", node4);
        IntermediateNode node6 = factory.createIntermediateNode("S ::= A? B+ . C*", node2, node5);
        TerminalNode node7 = factory.createTerminalNode("c", 2, 3);
        NonterminalNode node8 = factory.createNonterminalNode("C", "C ::= c .", node7);
        NonterminalNode node9 = factory.createNonterminalNode("C+", "C+ ::= C .", node8);
        NonterminalNode node10 = factory.createNonterminalNode("C*", "C* ::= C+ .", node9);
        IntermediateNode node11 = factory.createIntermediateNode("S ::= A? B+ C* .", node6, node10);
        NonterminalNode node12 = factory.createNonterminalNode("S", "S ::= A? B+ C* .", node11);
        return node12;
    }

    private static ParseResult getParseResult5(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(38)
                .setGSSNodesCount(15)
                .setGSSEdgesCount(16)
                .setNonterminalNodesCount(22)
                .setTerminalNodesCount(10)
                .setIntermediateNodesCount(9)
                .setPackedNodesCount(31)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF5(new SPPFNodeFactory(graph)), statistics);
    }

    private static NonterminalNode expectedSPPF5(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("A?", "A? ::= A .", node1);
        TerminalNode node3 = factory.createTerminalNode("b", 1, 2);
        NonterminalNode node4 = factory.createNonterminalNode("B", "B ::= b .", node3);
        NonterminalNode node5 = factory.createNonterminalNode("B+", "B+ ::= B .", node4);
        TerminalNode node6 = factory.createTerminalNode("b", 2, 3);
        NonterminalNode node7 = factory.createNonterminalNode("B", "B ::= b .", node6);
        IntermediateNode node8 = factory.createIntermediateNode("B+ ::= B+ B .", node5, node7);
        NonterminalNode node9 = factory.createNonterminalNode("B+", "B+ ::= B+ B .", node8);
        TerminalNode node10 = factory.createTerminalNode("b", 3, 4);
        NonterminalNode node11 = factory.createNonterminalNode("B", "B ::= b .", node10);
        IntermediateNode node12 = factory.createIntermediateNode("B+ ::= B+ B .", node9, node11);
        NonterminalNode node13 = factory.createNonterminalNode("B+", "B+ ::= B+ B .", node12);
        TerminalNode node14 = factory.createTerminalNode("b", 4, 5);
        NonterminalNode node15 = factory.createNonterminalNode("B", "B ::= b .", node14);
        IntermediateNode node16 = factory.createIntermediateNode("B+ ::= B+ B .", node13, node15);
        NonterminalNode node17 = factory.createNonterminalNode("B+", "B+ ::= B+ B .", node16);
        IntermediateNode node18 = factory.createIntermediateNode("S ::= A? B+ . C*", node2, node17);
        TerminalNode node19 = factory.createTerminalNode("c", 5, 6);
        NonterminalNode node20 = factory.createNonterminalNode("C", "C ::= c .", node19);
        NonterminalNode node21 = factory.createNonterminalNode("C+", "C+ ::= C .", node20);
        TerminalNode node22 = factory.createTerminalNode("c", 6, 7);
        NonterminalNode node23 = factory.createNonterminalNode("C", "C ::= c .", node22);
        IntermediateNode node24 = factory.createIntermediateNode("C+ ::= C+ C .", node21, node23);
        NonterminalNode node25 = factory.createNonterminalNode("C+", "C+ ::= C+ C .", node24);
        TerminalNode node26 = factory.createTerminalNode("c", 7, 8);
        NonterminalNode node27 = factory.createNonterminalNode("C", "C ::= c .", node26);
        IntermediateNode node28 = factory.createIntermediateNode("C+ ::= C+ C .", node25, node27);
        NonterminalNode node29 = factory.createNonterminalNode("C+", "C+ ::= C+ C .", node28);
        TerminalNode node30 = factory.createTerminalNode("c", 8, 9);
        NonterminalNode node31 = factory.createNonterminalNode("C", "C ::= c .", node30);
        IntermediateNode node32 = factory.createIntermediateNode("C+ ::= C+ C .", node29, node31);
        NonterminalNode node33 = factory.createNonterminalNode("C+", "C+ ::= C+ C .", node32);
        TerminalNode node34 = factory.createTerminalNode("c", 9, 10);
        NonterminalNode node35 = factory.createNonterminalNode("C", "C ::= c .", node34);
        IntermediateNode node36 = factory.createIntermediateNode("C+ ::= C+ C .", node33, node35);
        NonterminalNode node37 = factory.createNonterminalNode("C+", "C+ ::= C+ C .", node36);
        NonterminalNode node38 = factory.createNonterminalNode("C*", "C* ::= C+ .", node37);
        IntermediateNode node39 = factory.createIntermediateNode("S ::= A? B+ C* .", node18, node38);
        NonterminalNode node40 = factory.createNonterminalNode("S", "S ::= A? B+ C* .", node39);
        return node40;
    }

}
