package org.iguana.parser.ebnf;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Tree;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.regex.Character;
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
 * A ::= 'a'*
 */
public class Test1 {

    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Character.from('a'));
    static Rule r1 = Rule.withHead(A).addSymbols(Star.from(a)).build();

    private static Grammar grammar = Grammar.builder().addRule(r1).build();
    private static Input input0 = Input.fromString("");
    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("aa");
    private static Input input3 = Input.fromString("aaa");
    private static Input input4 = Input.fromString("aaaaaaaaaaa");

    @Test
    public void testParser0() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input0, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input0, graph, A);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult0(graph), result);
        assertEquals(getTree0(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input1, graph, A);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser2() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input2, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input2, graph, A);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser3() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input3, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input3, graph, A);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
        assertEquals(getTree3(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser4() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input4, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input4, graph, A);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult4(graph), result);
        assertEquals(getTree4(), result.asParseSuccess().getTree());
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
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("a*"), registry.getSlot("a* ::= ."), node0, input0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a* ."), node1, input0);
        return node2;
    }

    public static Tree getTree0() {
        Tree t1 = createStar(list());
        Tree t2 = createRule(r1, list(t1), input0);
        return t2;
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
        return new ParseSuccess(expectedSPPF1(graph), statistics, input0);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a ."), node0, input1);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("a*"), registry.getSlot("a* ::= a+ ."), node1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a* ."), node2, input1);
        return node3;
    }

    public static Tree getTree1() {
        Tree t0 = createTerminal(0, 1, input1);
        Tree t1 = createStar(list(t0));
        Tree t2 = createRule(r1, list(t1), input1);
        return t2;
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
        return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a ."), node0, input2);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 1, 2, input2);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node1, node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node3, input2);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("a*"), registry.getSlot("a* ::= a+ ."), node4, input2);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a* ."), node5, input2);
        return node6;
    }

    public static Tree getTree2() {
        Tree t0 = createTerminal(0, 1, input2);
        Tree t1 = createTerminal(1, 2, input2);
        Tree t2 = createStar(list(t0, t1));
        Tree t3 = createRule(r1, list(t2), input2);
        return t3;
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
        return new ParseSuccess(expectedSPPF3(graph), statistics, input2);
    }

    private static NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input3);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a ."), node0, input3);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 1, 2, input3);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node1, node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node3, input3);
        TerminalNode node5 = createTerminalNode(registry.getSlot("a"), 2, 3, input3);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node4, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node6, input3);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("a*"), registry.getSlot("a* ::= a+ ."), node7, input3);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a* ."), node8, input3);
        return node9;
    }

    public static Tree getTree3() {
        Tree t0 = createTerminal(0, 1, input3);
        Tree t1 = createTerminal(1, 2, input3);
        Tree t2 = createTerminal(2, 3, input3);
        Tree t3 = createStar(list(t0, t1, t2));
        Tree t4 = createRule(r1, list(t3), input3);
        return t4;
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
        return new ParseSuccess(expectedSPPF4(graph), statistics, input0);
    }

    private static NonterminalNode expectedSPPF4(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input4);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a ."), node0, input4);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 1, 2, input4);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node1, node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node3, input4);
        TerminalNode node5 = createTerminalNode(registry.getSlot("a"), 2, 3, input4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node4, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node6, input4);
        TerminalNode node8 = createTerminalNode(registry.getSlot("a"), 3, 4, input4);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node7, node8);
        NonterminalNode node10 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node9, input4);
        TerminalNode node11 = createTerminalNode(registry.getSlot("a"), 4, 5, input4);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node10, node11);
        NonterminalNode node13 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node12, input4);
        TerminalNode node14 = createTerminalNode(registry.getSlot("a"), 5, 6, input4);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node13, node14);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node15, input4);
        TerminalNode node17 = createTerminalNode(registry.getSlot("a"), 6, 7, input4);
        IntermediateNode node18 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node16, node17);
        NonterminalNode node19 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node18, input4);
        TerminalNode node20 = createTerminalNode(registry.getSlot("a"), 7, 8, input4);
        IntermediateNode node21 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node19, node20);
        NonterminalNode node22 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node21, input4);
        TerminalNode node23 = createTerminalNode(registry.getSlot("a"), 8, 9, input4);
        IntermediateNode node24 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node22, node23);
        NonterminalNode node25 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node24, input4);
        TerminalNode node26 = createTerminalNode(registry.getSlot("a"), 9, 10, input4);
        IntermediateNode node27 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node25, node26);
        NonterminalNode node28 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node27, input4);
        TerminalNode node29 = createTerminalNode(registry.getSlot("a"), 10, 11, input4);
        IntermediateNode node30 = createIntermediateNode(registry.getSlot("a+ ::= a+ a ."), node28, node29);
        NonterminalNode node31 = createNonterminalNode(registry.getSlot("a+"), registry.getSlot("a+ ::= a+ a ."), node30, input4);
        NonterminalNode node32 = createNonterminalNode(registry.getSlot("a*"), registry.getSlot("a* ::= a+ ."), node31, input4);
        NonterminalNode node33 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a* ."), node32, input4);
        return node33;
    }

    public static Tree getTree4() {
        Tree t0 = createTerminal(0, 1, input4);
        Tree t1 = createTerminal(1, 2, input4);
        Tree t2 = createTerminal(2, 3, input4);
        Tree t3 = createTerminal(3, 4, input4);
        Tree t4 = createTerminal(4, 5, input4);
        Tree t5 = createTerminal(5, 6, input4);
        Tree t6 = createTerminal(6, 7, input4);
        Tree t7 = createTerminal(7, 8, input4);
        Tree t8 = createTerminal(8, 9, input4);
        Tree t9 = createTerminal(9, 10, input4);
        Tree t10 = createTerminal(10, 11, input4);
        Tree t11 = createStar(list(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10));
        Tree t12 = createRule(r1, list(t11), input4);
        return t12;
    }
}