package org.iguana.parser.basic;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Tree;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Character;
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
 * E ::= E '*' E
 *     | E '+' E
 *     | 'a'
 */
public class Test19 {

    static Nonterminal E = Nonterminal.withName("E");
    static org.iguana.grammar.symbol.Character a = Character.from('a');
    static Character plus = Character.from('+');
    static Character star = Character.from('*');

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
        GrammarGraph graph = grammar.toGrammarGraph(input1, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser2() {
        GrammarGraph graph = grammar.toGrammarGraph(input2, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser3() {
        GrammarGraph graph = grammar.toGrammarGraph(input3, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input3, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
        assertEquals(getTree3(), result.asParseSuccess().getTree());
    }

    private ParseSuccess getParseResult1(GrammarGraph registry) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(8)
                .setGSSNodesCount(2)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(5)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF1(registry), statistics, input1);
    }

    private NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input1);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input1);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input1);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input1);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node6, input1);
        return node7;
    }

    private Tree getTree1() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r3, list(t0));
        Tree t2 = createTerminal(1, 2);
        Tree t3 = createTerminal(2, 3);
        Tree t4 = createRule(r3, list(t3));
        Tree t5 = createRule(r2, list(t1, t2, t4));
        return t5;
    }

    private ParseSuccess getParseResult2(GrammarGraph registry) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(16)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(9)
                .setNonterminalNodesCount(6)
                .setTerminalNodesCount(5)
                .setIntermediateNodesCount(7)
                .setPackedNodesCount(14)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(expectedSPPF2(registry), statistics, input1);
    }

    private NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input2);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input2);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input2);
        TerminalNode node6 = createTerminalNode(registry.getSlot("*"), 3, 4, input2);
        IntermediateNode node7 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node5, node6);
        TerminalNode node8 = createTerminalNode(registry.getSlot("a"), 4, 5, input2);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node8, input2);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node7, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node10, input2);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node11);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node13, input2);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node14, node6);
        IntermediateNode node16 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node15, node9);
        NonterminalNode node17 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node12, input2);
        node17.addPackedNode(registry.getSlot("E ::= E * E ."), node16);
        return node17;
    }

    private Tree getTree2() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r3, list(t0));
        Tree t2 = createTerminal(1, 2);
        Tree t3 = createTerminal(2, 3);
        Tree t4 = createRule(r3, list(t3));
        Tree t5 = createRule(r2, list(t1, t2, t4));
        Tree t6 = createTerminal(3, 4);
        Tree t7 = createTerminal(4, 5);
        Tree t8 = createRule(r3, list(t7));
        Tree t9 = createRule(r1, list(t4, t6, t8));
        Tree t10 = createAmbiguity(set(createBranch(list(t5, t6, t8)), createBranch(list(t1, t2, t9))));
        return t10;
    }

    private ParseSuccess getParseResult3(GrammarGraph registry) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(41)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(20)
                .setNonterminalNodesCount(15)
                .setTerminalNodesCount(9)
                .setIntermediateNodesCount(26)
                .setPackedNodesCount(51)
                .setAmbiguousNodesCount(10).build();
        return new ParseSuccess(expectedSPPF3(registry), statistics, input1);
    }

    private NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input3);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node0, input3);
        TerminalNode node2 = createTerminalNode(registry.getSlot("+"), 1, 2, input3);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node1, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("a"), 2, 3, input3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node4, input3);
        TerminalNode node6 = createTerminalNode(registry.getSlot("*"), 3, 4, input3);
        IntermediateNode node7 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node5, node6);
        TerminalNode node8 = createTerminalNode(registry.getSlot("a"), 4, 5, input3);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node8, input3);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node7, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node10, input3);
        TerminalNode node12 = createTerminalNode(registry.getSlot("+"), 5, 6, input3);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node11, node12);
        TerminalNode node14 = createTerminalNode(registry.getSlot("a"), 6, 7, input3);
        NonterminalNode node15 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node14, input3);
        IntermediateNode node16 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node13, node15);
        IntermediateNode node17 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node9, node12);
        IntermediateNode node18 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node17, node15);
        NonterminalNode node19 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node18, input3);
        IntermediateNode node20 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node7, node19);
        NonterminalNode node21 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node16, input3);
        node21.addPackedNode(registry.getSlot("E ::= E * E ."), node20);
        TerminalNode node22 = createTerminalNode(registry.getSlot("*"), 7, 8, input3);
        IntermediateNode node23 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node21, node22);
        TerminalNode node24 = createTerminalNode(registry.getSlot("a"), 8, 9, input3);
        NonterminalNode node25 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= a ."), node24, input3);
        IntermediateNode node26 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node15, node22);
        IntermediateNode node27 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node26, node25);
        NonterminalNode node28 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node27, input3);
        IntermediateNode node29 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node17, node28);
        IntermediateNode node30 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node19, node22);
        IntermediateNode node31 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node30, node25);
        NonterminalNode node32 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node29, input3);
        node32.addPackedNode(registry.getSlot("E ::= E * E ."), node31);
        IntermediateNode node33 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node23, node25);
        node33.addPackedNode(registry.getSlot("E ::= E * E ."), node7, node32);
        IntermediateNode node34 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node13, node28);
        NonterminalNode node35 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E * E ."), node33, input3);
        node35.addPackedNode(registry.getSlot("E ::= E + E ."), node34);
        IntermediateNode node36 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node11);
        IntermediateNode node37 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node5);
        NonterminalNode node38 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node37, input3);
        IntermediateNode node39 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node38, node6);
        IntermediateNode node40 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node39, node9);
        NonterminalNode node41 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node36, input3);
        node41.addPackedNode(registry.getSlot("E ::= E * E ."), node40);
        IntermediateNode node42 = createIntermediateNode(registry.getSlot("E ::= E + . E"), node41, node12);
        IntermediateNode node43 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node35);
        node43.addPackedNode(registry.getSlot("E ::= E + E ."), node42, node28);
        IntermediateNode node44 = createIntermediateNode(registry.getSlot("E ::= E + E ."), node3, node21);
        node44.addPackedNode(registry.getSlot("E ::= E + E ."), node42, node15);
        IntermediateNode node45 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node39, node19);
        NonterminalNode node46 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node44, input3);
        node46.addPackedNode(registry.getSlot("E ::= E * E ."), node45);
        IntermediateNode node47 = createIntermediateNode(registry.getSlot("E ::= E * . E"), node46, node22);
        IntermediateNode node48 = createIntermediateNode(registry.getSlot("E ::= E * E ."), node47, node25);
        node48.addPackedNode(registry.getSlot("E ::= E * E ."), node39, node32);
        NonterminalNode node49 = createNonterminalNode(registry.getSlot("E"), registry.getSlot("E ::= E + E ."), node43, input3);
        node49.addPackedNode(registry.getSlot("E ::= E * E ."), node48);
        return node49;
    }

    private Tree getTree3() {
        Tree t0 = createTerminal(0, 1);
        Tree t1 = createRule(r3, list(t0));
        Tree t2 = createTerminal(1, 2);
        Tree t3 = createTerminal(2, 3);
        Tree t4 = createRule(r3, list(t3));
        Tree t5 = createRule(r2, list(t1, t2, t4));
        Tree t6 = createTerminal(3, 4);
        Tree t7 = createTerminal(4, 5);
        Tree t8 = createRule(r3, list(t7));
        Tree t9 = createTerminal(5, 6);
        Tree t10 = createTerminal(6, 7);
        Tree t11 = createRule(r3, list(t10));
        Tree t12 = createRule(r2, list(t8, t9, t11));
        Tree t13 = createTerminal(7, 8);
        Tree t14 = createTerminal(8, 9);
        Tree t15 = createRule(r3, list(t14));
        Tree t16 = createRule(r1, list(t11, t13, t15));
        Tree t17 = createAmbiguity(set(createBranch(list(t12, t13, t15)), createBranch(list(t8, t9, t16))));
        Tree t18 = createRule(r1, list(t4, t6, t8));
        Tree t19 = createAmbiguity(set(createBranch(list(t4, t6, t12)), createBranch(list(t18, t9, t11))));
        Tree t20 = createAmbiguity(set(createBranch(list(t1, t2, t18)), createBranch(list(t5, t6, t8))));
        Tree t21 = createAmbiguity(set(createBranch(list(t20, t9, t11)), createBranch(list(t1, t2, t19))));
        Tree t22 = createAmbiguity(set(createBranch(list(t21)), createBranch(list(t5, t6, t12))));
        Tree t23 = createAmbiguity(set(createBranch(list(t5, t6, t17)), createBranch(list(t22, t13, t15))));
        Tree t24 = createAmbiguity(set(createBranch(list(t19, t13, t15)), createBranch(list(t4, t6, t17))));
        Tree t25 = createAmbiguity(set(createBranch(list(t24)), createBranch(list(t18, t9, t16))));
        Tree t26 = createAmbiguity(set(createBranch(list(t20, t9, t16)), createBranch(list(t1, t2, t25))));
        Tree t27 = createAmbiguity(set(createBranch(list(t26)), createBranch(list(t23))));
        return t27;
    }

}
