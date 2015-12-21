package org.iguana.parser.ebnf;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Terminal;
import iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;
import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

/**
 *
 * S ::= A+
 *
 * A ::= 'a'
 *
 */
public class Test4 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Character.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(Plus.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();

    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("aa");
    private static Input input3 = Input.fromString("aaa");


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
        return new ParseSuccess(expectedSPPF1(graph), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input1);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A ."), node1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A+ ."), node2, input1);
        return node3;
    }

    public static Term getTree1() {
        Term t0 = createTerminal(a, 0, 1, input1);
        Term t1 = createRule(r2, list(t0), input1);
        Term t2 = createPlus(list(t1));
        Term t3 = createRule(r1, list(t2), input1);
        return t3;
    }

    private static ParseResult getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(9)
                .setGSSNodesCount(4)
                .setGSSEdgesCount(4)
                .setNonterminalNodesCount(5)
                .setTerminalNodesCount(2)
                .setIntermediateNodesCount(1)
                .setPackedNodesCount(6)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input2);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A ."), node1, input2);
        TerminalNode node3 = createTerminalNode(registry.getSlot("a"), 1, 2, input2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node3, input2);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("A+ ::= A+ A ."), node2, node4);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("A+"), registry.getSlot("A+ ::= A+ A ."), node5, input2);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A+ ."), node6, input2);
        return node7;
    }

    public static Term getTree2() {
        Term t0 = createTerminal(a, 0, 1, input2);
        Term t1 = createRule(r2, list(t0), input2);
        Term t2 = createTerminal(a, 1, 2, input2);
        Term t3 = createRule(r2, list(t2), input2);
        Term t4 = createPlus(list(t1, t3));
        Term t5 = createRule(r1, list(t4), input2);
        return t5;
    }

    private static ParseResult getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(12)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(5)
                .setNonterminalNodesCount(7)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(2)
                .setPackedNodesCount(9)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF3(graph), statistics, input2);
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
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A+ ."), node10, input3);
        return node11;
    }

    public static Term getTree3() {
        Term t0 = createTerminal(a, 0, 1, input3);
        Term t1 = createRule(r2, list(t0), input3);
        Term t2 = createTerminal(a, 1, 2, input3);
        Term t3 = createRule(r2, list(t2), input3);
        Term t4 = createTerminal(a, 2, 3, input3);
        Term t5 = createRule(r2, list(t4), input3);
        Term t6 = createPlus(list(t1, t3, t5));
        Term t7 = createRule(r1, list(t6), input3);
        return t7;
    }
}
