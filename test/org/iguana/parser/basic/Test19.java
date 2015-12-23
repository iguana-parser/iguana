package org.iguana.parser.basic;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import iguana.regex.Character;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;
import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

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
        assertTrue(getTree1().equals(result.asParseSuccess().getTree()));
    }

    @Test
    public void testParser2() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
        assertTrue(getTree2().equals(result.asParseSuccess().getTree()));
    }

    @Test
    public void testParser3() {
        GrammarGraph graph = GrammarGraph.from(grammar, input3);
        ParseResult result = Iguana.parse(input3, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
        assertTrue(getTree3().equals(result.asParseSuccess().getTree()));
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

    private Term getTree1() {
        Term t0 = createTerminal(a, 0, 1, input1);
        Term t1 = createRule(r3, list(t0), input1);
        Term t2 = createTerminal(plus, 1, 2, input1);
        Term t3 = createTerminal(a, 2, 3, input1);
        Term t4 = createRule(r3, list(t3), input1);
        Term t5 = createRule(r2, list(t1, t2, t4), input1);
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
        return new ParseSuccess(expectedSPPF2(registry), statistics, input2);
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

    private Term getTree2() {
        Term t0 = createTerminal(a, 0, 1, input2);
        Term t1 = createRule(r3, list(t0), input2);
        Term t2 = createTerminal(plus, 1, 2, input2);
        Term t3 = createTerminal(a, 2, 3, input2);
        Term t4 = createRule(r3, list(t3), input2);
        Term t5 = createTerminal(star, 3, 4, input2);
        Term t6 = createTerminal(a, 4, 5, input2);
        Term t7 = createRule(r3, list(t6), input2);
        Term t8 = createRule(r1, list(t4, t5, t7), input2);
        Term t9 = createRule(r2, list(t1, t2, t4), input2);
        Term t10 = createAmbiguity(list(list(t1, t2, t8), list(t9, t5, t7)));
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
        return new ParseSuccess(expectedSPPF3(registry), statistics, input3);
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

    private Term getTree3() {
        Term t0 = createTerminal(a, 0, 1, input3);
        Term t1 = createRule(r3, list(t0), input3);
        Term t2 = createTerminal(plus, 1, 2, input3);
        Term t3 = createTerminal(a, 2, 3, input3);
        Term t4 = createRule(r3, list(t3), input3);
        Term t5 = createTerminal(star, 3, 4, input3);
        Term t6 = createTerminal(a, 4, 5, input3);
        Term t7 = createRule(r3, list(t6), input3);
        Term t8 = createRule(r1, list(t4, t5, t7), input3);
        Term t9 = createTerminal(plus, 5, 6, input3);
        Term t10 = createTerminal(a, 6, 7, input3);
        Term t11 = createRule(r3, list(t10), input3);
        Term t12 = createRule(r2, list(t7, t9, t11), input3);
        Term t13 = createAmbiguity(list(list(t8, t9, t11), list(t4, t5, t12)));
        Term t14 = createTerminal(star, 7, 8, input3);
        Term t15 = createTerminal(a, 8, 9, input3);
        Term t16 = createRule(r3, list(t15), input3);
        Term t17 = createRule(r1, list(t11, t14, t16), input3);
        Term t18 = createAmbiguity(list(list(t7, t9, t17), list(t12, t14, t16)));
        Term t19 = createAmbiguity(list(list(t13, t14, t16), list(t4, t5, t18)));
        Term t20 = createAmbiguity(list(list(t19), list(t8, t9, t17)));
        Term t21 = createRule(r2, list(t1, t2, t4), input3);
        Term t22 = createAmbiguity(list(list(t1, t2, t8), list(t21, t5, t7)));
        Term t23 = createAmbiguity(list(list(t1, t2, t20), list(t22, t9, t17)));
        Term t24 = createAmbiguity(list(list(t1, t2, t13), list(t22, t9, t11)));
        Term t25 = createAmbiguity(list(list(t24), list(t21, t5, t12)));
        Term t26 = createAmbiguity(list(list(t25, t14, t16), list(t21, t5, t18)));
        Term t27 = createAmbiguity(list(list(t23), list(t26)));
        return t27;
    }

}
