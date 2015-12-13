package org.iguana.parser.ebnf;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Tree;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static org.junit.Assert.*;
import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;
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
        assertEquals(getTree0(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser2() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input2, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input2, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser3() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input3, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input3, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
        assertEquals(getTree3(), result.asParseSuccess().getTree());
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
        return new ParseSuccess(expectedSPPF0(graph), statistics, input0);
    }

    private static NonterminalNode expectedSPPF0(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("epsilon"), 0, 0, input0);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A*"), registry.getSlot("A* ::= ."), node0, input0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A* ."), node1, input0);
        return node2;
    }

    public static Tree getTree0() {
        Tree t1 = createStar(list());
        Tree t2 = createRule(r1, list(t1), input0);
        return t2;
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
        return new ParseSuccess(expectedSPPF1(graph), statistics, input0);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input1);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A ."), node1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("A*"), registry.getSlot("A* ::= A+ ."), node2, input1);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A* ."), node3, input1);
        return node4;
    }

    public static Tree getTree1() {
        Tree t0 = createTerminal(a, 0, 1, input1);
        Tree t1 = createRule(r2, list(t0), input1);
        Tree t2 = createStar(list(t1));
        Tree t3 = createRule(r1, list(t2), input1);
        return t3;
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
        return new ParseSuccess(expectedSPPF2(graph), statistics, input0);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input2);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A ."), node1, input2);
        TerminalNode node3 = createTerminalNode(registry.getSlot("a"), 1, 2, input2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node3, input2);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node2, node4);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node5, input2);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("A*"), registry.getSlot("A* ::= A+ ."), node6, input2);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A* ."), node7, input2);
        return node8;
    }

    public static Tree getTree2() {
        Tree t0 = createTerminal(a, 0, 1, input2);
        Tree t1 = createRule(r2, list(t0), input2);
        Tree t2 = createTerminal(a, 1, 2, input2);
        Tree t3 = createRule(r2, list(t2), input2);
        Tree t4 = createStar(list(t1, t3));
        Tree t5 = createRule(r1, list(t4), input2);
        return t5;
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
        return new ParseSuccess(expectedSPPF3(graph), statistics, input0);
    }

    private static NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input3);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input3);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A ."), node1, input3);
        TerminalNode node3 = createTerminalNode(registry.getSlot("a"), 1, 2, input3);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node3, input3);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node2, node4);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node5, input3);
        TerminalNode node7 = createTerminalNode(registry.getSlot("a"), 2, 3, input3);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node7, input3);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node6, node8);
        NonterminalNode node10 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node9, input3);
        TerminalNode node11 = createTerminalNode(registry.getSlot("a"), 3, 4, input3);
        NonterminalNode node12 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node11, input3);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node10, node12);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node13, input3);
        TerminalNode node15 = createTerminalNode(registry.getSlot("a"), 4, 5, input3);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node15, input3);
        IntermediateNode node17 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node14, node16);
        NonterminalNode node18 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node17, input3);
        TerminalNode node19 = createTerminalNode(registry.getSlot("a"), 5, 6, input3);
        NonterminalNode node20 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node19, input3);
        IntermediateNode node21 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node18, node20);
        NonterminalNode node22 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node21, input3);
        TerminalNode node23 = createTerminalNode(registry.getSlot("a"), 6, 7, input3);
        NonterminalNode node24 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node23, input3);
        IntermediateNode node25 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node22, node24);
        NonterminalNode node26 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node25, input3);
        TerminalNode node27 = createTerminalNode(registry.getSlot("a"), 7, 8, input3);
        NonterminalNode node28 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node27, input3);
        IntermediateNode node29 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node26, node28);
        NonterminalNode node30 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node29, input3);
        TerminalNode node31 = createTerminalNode(registry.getSlot("a"), 8, 9, input3);
        NonterminalNode node32 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node31, input3);
        IntermediateNode node33 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node30, node32);
        NonterminalNode node34 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node33, input3);
        TerminalNode node35 = createTerminalNode(registry.getSlot("a"), 9, 10, input3);
        NonterminalNode node36 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node35, input3);
        IntermediateNode node37 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node34, node36);
        NonterminalNode node38 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node37, input3);
        TerminalNode node39 = createTerminalNode(registry.getSlot("a"), 10, 11, input3);
        NonterminalNode node40 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node39, input3);
        IntermediateNode node41 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node38, node40);
        NonterminalNode node42 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node41, input3);
        NonterminalNode node43 = createNonterminalNode(registry.getSlot("A*"), registry.getSlot("A* ::= A+ ."), node42, input3);
        NonterminalNode node44 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A* ."), node43, input3);
        return node44;
    }

    public static Tree getTree3() {
        Tree t0 = createTerminal(a, 0, 1, input3);
        Tree t1 = createRule(r2 , list(t0), input3);
        Tree t2 = createTerminal(a, 1, 2, input3);
        Tree t3 = createRule(r2, list(t2), input3);
        Tree t4 = createTerminal(a, 2, 3, input3);
        Tree t5 = createRule(r2, list(t4), input3);
        Tree t6 = createTerminal(a, 3, 4, input3);
        Tree t7 = createRule(r2, list(t6), input3);
        Tree t8 = createTerminal(a, 4, 5, input3);
        Tree t9 = createRule(r2, list(t8), input3);
        Tree t10 = createTerminal(a, 5, 6, input3);
        Tree t11 = createRule(r2, list(t10), input3);
        Tree t12 = createTerminal(a, 6, 7, input3);
        Tree t13 = createRule(r2, list(t12), input3);
        Tree t14 = createTerminal(a, 7, 8, input3);
        Tree t15 = createRule(r2, list(t14), input3);
        Tree t16 = createTerminal(a, 8, 9, input3);
        Tree t17 = createRule(r2, list(t16), input3);
        Tree t18 = createTerminal(a, 9, 10, input3);
        Tree t19 = createRule(r2, list(t18), input3);
        Tree t20 = createTerminal(a, 10, 11, input3);
        Tree t21 = createRule(r2, list(t20), input3);
        Tree t22 = createStar(list(t1, t3, t5, t7, t9, t11, t13, t15, t17, t19, t21));
        Tree t23 = createRule(r1, list(t22), input3);
        return t23;
    }

}
