package org.iguana.parser.basic;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Tree;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;
import static org.junit.Assert.*;

/**
 * E ::= E '+' E
 *     | 'a'
 */
public class Test18 {

    static Nonterminal E = Nonterminal.withName("E");
    static Character a = Character.from('a');
    static Character plus = Character.from('+');
    static Rule r1 = Rule.withHead(E).addSymbols(E, plus, E).build();
    static Rule r2 = Rule.withHead(E).addSymbols(a).build();

    public static Grammar grammar = Grammar.builder().addRules(r1, r2).build();
    private static Nonterminal startSymbol = E;

    private static Input input1 = Input.fromString("a+a");
    private static Input input2 = Input.fromString("a+a+a");
    private static Input input3 = Input.fromString("a+a+a+a+a");


    @Test
    public void testParser1() {
        GrammarGraph graph = grammar.toGrammarGraph(input1, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParser2() {
        GrammarGraph graph = grammar.toGrammarGraph(input2, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParser3() {
        GrammarGraph graph = grammar.toGrammarGraph(input3, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input3, graph, startSymbol);
        assertTrue(result.isParseSuccess());
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
        return new ParseSuccess(expectedSPPF1(graph), getTree1(), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6);
        return node7;
    }

    private static Tree getTree1() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r2, list(t0));
        Tree t2 = createTerminal(1, 2);
        Tree t3 = createTerminal(2, 3);
        Tree t4 = createRule(r2, list(t3));
        Tree t5 = createRule(r1, list(t1, t2, t4));
        return t5;
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
        return new ParseSuccess(expectedSPPF2(graph), getTree2(), statistics, input1);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6);
        TerminalNode node8 = createTerminalNode(registry.getSlot("+"), 3, 4);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node7, node8);
        TerminalNode node10 = createTerminalNode(registry.getSlot("a"), 4, 5);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node10);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node5, node8);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node11);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node13);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node9, node11);
        node15.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node14);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node15);
        return node16;
    }

    private static Tree getTree2() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r2, list(t0));
        Tree t2 = createTerminal(1, 2);
        Tree t3 = createTerminal(2, 3);
        Tree t4 = createRule(r2, list(t3));
        Tree t5 = createRule(r1, list(t1, t2, t4));
        Tree t6 = createTerminal(3, 4);
        Tree t7 = createTerminal(4, 5);
        Tree t8 = createRule(r2, list(t7));
        Tree t9 = createRule(r1, list(t4, t6, t8));
        Tree t10 = createAmbiguity(set(createBranch(list(t5, t6, t8)), createBranch(list(t1, t2, t9))));
        Tree t11 = createRule(r1, list(t10));
        return t11;
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
        return new ParseSuccess(expectedSPPF3(graph), getTree3(), statistics, input1);
    }

    private static NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6);
        TerminalNode node8 = createTerminalNode(registry.getSlot("+"), 3, 4);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node7, node8);
        TerminalNode node10 = createTerminalNode(registry.getSlot("a"), 4, 5);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node10);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node5, node8);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node11);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node13);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node9, node11);
        node15.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node14);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node15);
        TerminalNode node17 = createTerminalNode(registry.getSlot("+"), 5, 6);
        IntermediateNode node18 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node16, node17);
        TerminalNode node19 = createTerminalNode(registry.getSlot("a"), 6, 7);
        NonterminalNode node20 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node19);
        IntermediateNode node21 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node11, node17);
        IntermediateNode node22 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node21, node20);
        NonterminalNode node23 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node22);
        IntermediateNode node24 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node14, node17);
        IntermediateNode node25 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node23);
        node25.addPackedNode(registry.getSlot("E ::= E + E ."), node24, node20);
        NonterminalNode node26 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node25);
        IntermediateNode node27 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node18, node20);
        node27.addPackedNode(registry.getSlot("E ::= E + E ."), node9, node23);
        node27.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node26);
        NonterminalNode node28 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node27);
        TerminalNode node29 = createTerminalNode(registry.getSlot("+"), 7, 8);
        IntermediateNode node30 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node28, node29);
        TerminalNode node31 = createTerminalNode(registry.getSlot("a"), 8, 9);
        NonterminalNode node32 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node31);
        IntermediateNode node33 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node20, node29);
        IntermediateNode node34 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node33, node32);
        NonterminalNode node35 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node34);
        IntermediateNode node36 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node23, node29);
        IntermediateNode node37 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node21, node35);
        node37.addPackedNode(registry.getSlot("E ::= E + E ."), node36, node32);
        NonterminalNode node38 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node37);
        IntermediateNode node39 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node26, node29);
        IntermediateNode node40 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node12, node38);
        node40.addPackedNode(registry.getSlot("E ::= E + E ."), node39, node32);
        node40.addPackedNode(registry.getSlot("E ::= E + E ."), node24, node35);
        NonterminalNode node41 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node40);
        IntermediateNode node42 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node30, node32);
        node42.addPackedNode(registry.getSlot("E ::= E + E ."), node18, node35);
        node42.addPackedNode(registry.getSlot("E ::= E + E ."), node9, node38);
        node42.addPackedNode(registry.getSlot("E ::= E + E ."), node3, node41);
        NonterminalNode node43 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node42);
        return node43;
    }

    private static Tree getTree3() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r2, list(t0));
        Tree t2 = createTerminal(1, 2);
        Tree t3 = createTerminal(2, 3);
        Tree t4 = createRule(r2, list(t3));
        Tree t5 = createRule(r1, list(t1, t2, t4));
        Tree t6 = createTerminal(3, 4);
        Tree t7 = createTerminal(4, 5);
        Tree t8 = createRule(r2, list(t7));
        Tree t9 = createTerminal(5, 6);
        Tree t10 = createTerminal(6, 7);
        Tree t11 = createRule(r2, list(t10));
        Tree t12 = createRule(r1, list(t8, t9, t11));
        Tree t13 = createTerminal(7, 8);
        Tree t14 = createTerminal(8, 9);
        Tree t15 = createRule(r2, list(t14));
        Tree t16 = createRule(r1, list(t11, t13, t15));
        Tree t17 = createAmbiguity(set(createBranch(list(t12, t13, t15)), createBranch(list(t8, t9, t16))));
        Tree t18 = createRule(r1, list(t17));
        Tree t19 = createRule(r1, list(t4, t6, t8));
        Tree t20 = createAmbiguity(set(createBranch(list(t5, t6, t8)), createBranch(list(t1, t2, t19))));
        Tree t21 = createRule(r1, list(t20));
        Tree t22 = createAmbiguity(set(createBranch(list(t4, t6, t12)), createBranch(list(t19, t9, t11))));
        Tree t23 = createRule(r1, list(t22));
        Tree t24 = createAmbiguity(set(createBranch(list(t21, t9, t11)), createBranch(list(t5, t6, t12)), createBranch(list(t1, t2, t23))));
        Tree t25 = createRule(r1, list(t24));
        Tree t26 = createAmbiguity(set(createBranch(list(t4, t6, t18)), createBranch(list(t23, t13, t15)), createBranch(list(t19, t9, t16))));
        Tree t27 = createRule(r1, list(t26));
        Tree t28 = createAmbiguity(set(createBranch(list(t1, t2, t27)), createBranch(list(t5, t6, t18)), createBranch(list(t25, t13, t15)), createBranch(list(t21, t9, t16))));
        Tree t29 = createRule(r1, list(t28));
        return t29;
    }

}
