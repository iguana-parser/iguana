package org.iguana.parser.ebnf;


import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Alt;
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
 * S ::= (A|B|C)
 *
 * A ::= 'a'
 * B ::= 'b'
 * C ::= 'c'
 *
 */
public class Test6 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Nonterminal B = Nonterminal.withName("B");
    static Nonterminal C = Nonterminal.withName("C");
    static Terminal a = Terminal.from(Character.from('a'));
    static Terminal b = Terminal.from(Character.from('b'));
    static Terminal c = Terminal.from(Character.from('c'));
    static Rule r1 = Rule.withHead(S).addSymbols(Alt.from(A, B, C)).build();
    static Rule r2 = Rule.withHead(A).addSymbol(a).build();
    static Rule r3 = Rule.withHead(B).addSymbol(b).build();
    static Rule r4 = Rule.withHead(C).addSymbol(c).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();

    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("b");
    private static Input input3 = Input.fromString("c");

    @Test
    public void testParse1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParse2() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input2, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input2, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParse3() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input3, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input3, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult3(graph), result);
        assertEquals(getTree3(), result.asParseSuccess().getTree());
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
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("(A | B | C)"), registry.getSlot("(A | B | C) ::= A ."), node1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= (A | B | C) ."), node2, input1);
        return node3;
    }

    public static Term getTree1() {
        Term t0 = createTerminalTerm(a, 0, 1, input1);
        Term t1 = createNonterminalTerm(r2, list(t0), input1);
        Term t2 = createAlt(list(t1));
        Term t3 = createNonterminalTerm(r1, list(t2), input1);
        return t3;
    }

    private static ParseResult getParseResult2(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(5)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(2)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(1)
                .setIntermediateNodesCount(0)
                .setPackedNodesCount(3)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
    }

    private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("b"), 0, 1, input2);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node0, input2);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("(A | B | C)"), registry.getSlot("(A | B | C) ::= B ."), node1, input2);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= (A | B | C) ."), node2, input2);
        return node3;
    }

    public static Term getTree2() {
        Term t0 = createTerminalTerm(b, 0, 1, input2);
        Term t1 = createNonterminalTerm(r3, list(t0), input2);
        Term t2 = createAlt(list(t1));
        Term t3 = createNonterminalTerm(r1, list(t2), input2);
        return t3;
    }

    private static ParseResult getParseResult3(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(5)
                .setGSSNodesCount(3)
                .setGSSEdgesCount(2)
                .setNonterminalNodesCount(3)
                .setTerminalNodesCount(1)
                .setIntermediateNodesCount(0)
                .setPackedNodesCount(3)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(expectedSPPF3(graph), statistics, input3);
    }

    private static NonterminalNode expectedSPPF3(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("c"), 0, 1, input3);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node0, input3);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("(A | B | C)"), registry.getSlot("(A | B | C) ::= C ."), node1, input3);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= (A | B | C) ."), node2, input3);
        return node3;
    }

    public static Term getTree3() {
        Term t0 = createTerminalTerm(c, 0, 1, input3);
        Term t1 = createNonterminalTerm(r4, list(t0), input3);
        Term t2 = createAlt(list(t1));
        Term t3 = createNonterminalTerm(r1, list(t2), input3);
        return t3;
    }
}
