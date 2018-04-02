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
import org.iguana.result.ParserResultOps;
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
    static Terminal a = Terminal.from(Char.from('a'));
    static Terminal b = Terminal.from(Char.from('b'));
    static Terminal c = Terminal.from(Char.from('c'));
    static Rule r1 = Rule.withHead(S).addSymbols(Alt.from(A, B, C)).build();
    static Rule r2 = Rule.withHead(A).addSymbol(a).build();
    static Rule r3 = Rule.withHead(B).addSymbol(b).build();
    static Rule r4 = Rule.withHead(C).addSymbol(c).build();
    static Start start = Start.from(S);
    private static Grammar grammar = new DesugarStartSymbol().transform(EBNFToBNF.convert(Grammar.builder().addRules(r1, r2, r3, r4).setStartSymbol(start).build()));

    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.fromString("b");
    private static Input input3 = Input.fromString("c");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "ebnf").toAbsolutePath().toString();
        TestRunner.record(grammar, input1, 1, path + "/Test6");
        TestRunner.record(grammar, input2, 2, path + "/Test6");
        TestRunner.record(grammar, input3, 3, path + "/Test6");
    }

    @Test
    public void testParse1() {
        grammar = EBNFToBNF.convert(grammar);
        ParseResult result = Iguana.parse(input1, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar, Configuration.DEFAULT);
        assertEquals(getParseResult1(graph), result);
    }

    @Test
    public void testParse2() {
        grammar = EBNFToBNF.convert(grammar);
        ParseResult result = Iguana.parse(input2, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar, Configuration.DEFAULT);
        assertEquals(getParseResult2(graph), result);
    }

    @Test
    public void testParse3() {
        grammar = EBNFToBNF.convert(grammar);
        ParseResult result = Iguana.parse(input3, grammar, S);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar, Configuration.DEFAULT);
        assertEquals(getParseResult3(graph), result);
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
        return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input1);
    }

    private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("(A | B | C)", "(A | B | C) ::= A .", node1);
        NonterminalNode node3 = factory.createNonterminalNode("S", "S ::= (A | B | C) .", node2);
        return node3;
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
        return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics, input2);
    }

    private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("b", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("B", "B ::= b .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("(A | B | C)", "(A | B | C) ::= B .", node1);
        NonterminalNode node3 = factory.createNonterminalNode("S", "S ::= (A | B | C) .", node2);
        return node3;
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
        return new ParseSuccess(expectedSPPF3(new SPPFNodeFactory(graph)), statistics, input3);
    }

    private static NonterminalNode expectedSPPF3(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("c", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("C", "C ::= c .", node0);
        NonterminalNode node2 = factory.createNonterminalNode("(A | B | C)", "(A | B | C) ::= C .", node1);
        NonterminalNode node3 = factory.createNonterminalNode("S", "S ::= (A | B | C) .", node2);
        return node3;
    }

}
