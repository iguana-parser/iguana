package org.iguana.parser.ebnf;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.SPPFVisualization;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Tree;
import iguana.parsetrees.tree.TreeVisualization;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.*;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static org.junit.Assert.*;
import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;

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
    static Character a = Character.from('a');
    static Character b = Character.from('b');
    static Character c = Character.from('c');
    static Rule r1 = Rule.withHead(S).addSymbols(Opt.from(A), Plus.from(B), Star.from(C)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    static Rule r3 = Rule.withHead(B).addSymbols(b).build();
    static Rule r4 = Rule.withHead(C).addSymbols(c).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();

    private static Input input1 = Input.fromString("b");
    private static Input input2 = Input.fromString("ab");
    private static Input input3 = Input.fromString("bc");
    private static Input input4 = Input.fromString("abc");
    private static Input input5 = Input.fromString("abbbbccccc");

    @Test
    public void testParse1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = grammar.toGrammarGraph(input1, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParse2() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = grammar.toGrammarGraph(input2, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input2, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParse3() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = grammar.toGrammarGraph(input3, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input3, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
    }

    @Test
    public void testParse4() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = grammar.toGrammarGraph(input4, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input4, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult4(graph), result);
    }

    @Test
    public void testParse5() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = grammar.toGrammarGraph(input5, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input5, graph, S);
        assertTrue(result.isParseSuccess());
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
        return new ParseSuccess(expectedSPPF1(graph), getTree1(), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("epsilon"), 0, 0);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= ."), node0);
        TerminalNode node2 = createTerminalNode(registry.getSlot("b"), 0, 1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B ."), node3);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("S ::= A? B+ . C*"), node1, node4);
        TerminalNode node6 = createTerminalNode(registry.getSlot("epsilon"), 1, 1);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("C*"), registry.getSlot("C* ::= ."), node6);
        IntermediateNode node8 = createIntermediateNode(registry.getSlot("S ::= A? B+ C* ."), node5, node7);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? B+ C* ."), node8);
        return node9;
    }

    public static Tree getTree1() {
        Tree t0 = createEpsilon(0);
        Tree t1 = createOpt(t0);
        Tree t2 = createTerminal(0, 1);
        Tree t3 = createRule(r3, list(t2));
        Tree t4 = createPlus(list(t3));
        Tree t5 = createEpsilon(1);
        Tree t6 = createStar(list(t5));
        Tree t7 = createRule(r1, list(t1, t4, t6));
        return t7;
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
        return new ParseSuccess(expectedSPPF2(graph), getTree2(), statistics, input2);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= A ."), node1);
        TerminalNode node3 = createTerminalNode(registry.getSlot("b"), 1, 2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B ."), node4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("S ::= A? B+ . C*"), node2, node5);
        TerminalNode node7 = createTerminalNode(registry.getSlot("epsilon"), 2, 2);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("C*"), registry.getSlot("C* ::= ."), node7);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("S ::= A? B+ C* ."), node6, node8);
        NonterminalNode node10 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? B+ C* ."), node9);
        return node10;
    }

    public static Tree getTree2() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r2, list(t0));
        Tree t2 = createOpt(t1);
        Tree t3 = createTerminal(1, 2);
        Tree t4 = createRule(r3, list(t3));
        Tree t5 = createPlus(list(t4));
        Tree t6 = createEpsilon(2);
        Tree t7 = createStar(list(t6));
        Tree t8 = createRule(r1, list(t2, t5, t7));
        return t8;
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
        return new ParseSuccess(expectedSPPF3(graph), getTree3(), statistics, input3);
    }

    private static NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("epsilon"), 0, 0);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= ."), node0);
        TerminalNode node2 = createTerminalNode(registry.getSlot("b"), 0, 1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B ."), node3);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("S ::= A? B+ . C*"), node1, node4);
        TerminalNode node6 = createTerminalNode(registry.getSlot("c"), 1, 2);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node6);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C ."), node7);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("C*"), registry.getSlot("C* ::= C+ ."), node8);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("S ::= A? B+ C* ."), node5, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? B+ C* ."), node10);
        return node11;
    }

    public static Tree getTree3() {
        Tree t0 = createEpsilon(0);
        Tree t1 = createOpt(t0);
        Tree t2 = createTerminal(0, 1);
        Tree t3 = createRule(r3, list(t2));
        Tree t4 = createPlus(list(t3));
        Tree t5 = createTerminal(1, 2);
        Tree t6 = createRule(r4, list(t5));
        Tree t7 = createStar(list(t6));
        Tree t8 = createRule(r1, list(t1, t4, t7));
        return t8;
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
        return new ParseSuccess(expectedSPPF4(graph), getTree4(), statistics, input4);
    }

    private static NonterminalNode expectedSPPF4(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= A ."), node1);
        TerminalNode node3 = createTerminalNode(registry.getSlot("b"), 1, 2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B ."), node4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("S ::= A? B+ . C*"), node2, node5);
        TerminalNode node7 = createTerminalNode(registry.getSlot("c"), 2, 3);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node7);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C ."), node8);
        NonterminalNode node10 = createNonterminalNode(registry.getSlot("C*"), registry.getSlot("C* ::= C+ ."), node9);
        IntermediateNode node11 = createIntermediateNode(registry.getSlot("S ::= A? B+ C* ."), node6, node10);
        NonterminalNode node12 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? B+ C* ."), node11);
        return node12;
    }

    public static Tree getTree4() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r2, list(t0));
        Tree t2 = createOpt(t1);
        Tree t3 = createTerminal(1, 2);
        Tree t4 = createRule(r3, list(t3));
        Tree t5 = createPlus(list(t4));
        Tree t6 = createTerminal(2, 3);
        Tree t7 = createRule(r4, list(t6));
        Tree t8 = createStar(list(t7));
        Tree t9 = createRule(r1, list(t2, t5, t8));
        return t9;
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
        return new ParseSuccess(expectedSPPF5(graph), getTree5(), statistics, input5);
    }

    private static NonterminalNode expectedSPPF5(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= A ."), node1);
        TerminalNode node3 = createTerminalNode(registry.getSlot("b"), 1, 2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B ."), node4);
        TerminalNode node6 = createTerminalNode(registry.getSlot("b"), 2, 3);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node6);
        IntermediateNode node8 = createIntermediateNode(registry.getSlot("B+ ::= B+ B ."), node5, node7);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B+ B ."), node8);
        TerminalNode node10 = createTerminalNode(registry.getSlot("b"), 3, 4);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node10);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("B+ ::= B+ B ."), node9, node11);
        NonterminalNode node13 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B+ B ."), node12);
        TerminalNode node14 = createTerminalNode(registry.getSlot("b"), 4, 5);
        NonterminalNode node15 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node14);
        IntermediateNode node16 = createIntermediateNode(registry.getSlot("B+ ::= B+ B ."), node13, node15);
        NonterminalNode node17 = createNonterminalNode(registry.getSlot("B+"), registry.getSlot("B+ ::= B+ B ."), node16);
        IntermediateNode node18 = createIntermediateNode(registry.getSlot("S ::= A? B+ . C*"), node2, node17);
        TerminalNode node19 = createTerminalNode(registry.getSlot("c"), 5, 6);
        NonterminalNode node20 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node19);
        NonterminalNode node21 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C ."), node20);
        TerminalNode node22 = createTerminalNode(registry.getSlot("c"), 6, 7);
        NonterminalNode node23 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node22);
        IntermediateNode node24 = createIntermediateNode(registry.getSlot("C+ ::= C+ C ."), node21, node23);
        NonterminalNode node25 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C+ C ."), node24);
        TerminalNode node26 = createTerminalNode(registry.getSlot("c"), 7, 8);
        NonterminalNode node27 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node26);
        IntermediateNode node28 = createIntermediateNode(registry.getSlot("C+ ::= C+ C ."), node25, node27);
        NonterminalNode node29 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C+ C ."), node28);
        TerminalNode node30 = createTerminalNode(registry.getSlot("c"), 8, 9);
        NonterminalNode node31 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node30);
        IntermediateNode node32 = createIntermediateNode(registry.getSlot("C+ ::= C+ C ."), node29, node31);
        NonterminalNode node33 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C+ C ."), node32);
        TerminalNode node34 = createTerminalNode(registry.getSlot("c"), 9, 10);
        NonterminalNode node35 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node34);
        IntermediateNode node36 = createIntermediateNode(registry.getSlot("C+ ::= C+ C ."), node33, node35);
        NonterminalNode node37 = createNonterminalNode(registry.getSlot("C+"), registry.getSlot("C+ ::= C+ C ."), node36);
        NonterminalNode node38 = createNonterminalNode(registry.getSlot("C*"), registry.getSlot("C* ::= C+ ."), node37);
        IntermediateNode node39 = createIntermediateNode(registry.getSlot("S ::= A? B+ C* ."), node18, node38);
        NonterminalNode node40 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? B+ C* ."), node39);
        return node40;
    }

    public static Tree getTree5() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r2, list(t0));
        Tree t2 = createOpt(t1);
        Tree t3 = createTerminal(1, 2);
        Tree t4 = createRule(r3, list(t3));
        Tree t5 = createTerminal(2, 3);
        Tree t6 = createRule(r3, list(t5));
        Tree t7 = createTerminal(3, 4);
        Tree t8 = createRule(r3, list(t7));
        Tree t9 = createTerminal(4, 5);
        Tree t10 = createRule(r3, list(t9));
        Tree t11 = createPlus(list(t4, t6, t8, t10));
        Tree t12 = createTerminal(5, 6);
        Tree t13 = createRule(r4, list(t12));
        Tree t14 = createTerminal(6, 7);
        Tree t15 = createRule(r4, list(t14));
        Tree t16 = createTerminal(7, 8);
        Tree t17 = createRule(r4, list(t16));
        Tree t18 = createTerminal(8, 9);
        Tree t19 = createRule(r4, list(t18));
        Tree t20 = createTerminal(9, 10);
        Tree t21 = createRule(r4, list(t20));
        Tree t22 = createStar(list(t13, t15, t17, t19, t21));
        Tree t23 = createRule(r1, list(t2, t11, t22));
        return t23;
    }

}
