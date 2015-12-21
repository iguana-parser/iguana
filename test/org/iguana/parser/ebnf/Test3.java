package org.iguana.parser.ebnf;

import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Opt;
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

import static org.junit.Assert.*;
import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;
import static iguana.utils.collections.CollectionsUtil.*;

/**
 *
 * S ::= A?
 *
 * A ::= 'a'
 *
 */
public class Test3 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Character.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(Opt.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();

    private static Input input0 = Input.fromString("");
    private static Input input1 = Input.fromString("a");

    @Test
    public void testParser0() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input0, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input0, graph, S);
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
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= ."), node0, input0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? ."), node1, input0);
        return node2;
    }

    public static Term getTree0() {
        Term t0 = createEpsilon(0);
        Term t1 = createOpt(t0);
        Term t2 = createRule(r1, list(t1), input0);
        return t2;
    }

    private static ParseResult getParseResult1(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(5)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(2)
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
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A?"), registry.getSlot("A? ::= A ."), node1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A? ."), node2, input1);
        return node3;
    }

    public static Term getTree1() {
        Term t0 = createTerminal(a, 0, 1, input1);
        Term t1 = createRule(r2, list(t0), input1);
        Term t2 = createOpt(t1);
        Term t3 = createRule(r1, list(t2), input1);
        return t3;
    }
}
