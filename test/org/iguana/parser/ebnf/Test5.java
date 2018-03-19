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
 * S ::= (A)
 *
 * A ::= 'a'
 *
 */
public class Test5 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Char.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(Sequence.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a).build();
    static Start start = Start.from(S);
    private static Grammar grammar = new DesugarStartSymbol().transform(EBNFToBNF.convert(Grammar.builder().addRules(r1, r2).setStartSymbol(start).build()));

    private static Input input1 = Input.fromString("a");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "ebnf").toAbsolutePath().toString();
        TestRunner.record(grammar, input1, 1, path + "/Test5");
    }

    @Test
    public void testParse1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
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
        NonterminalNode node2 = factory.createNonterminalNode("(A)", "(A) ::= A .", node1);
        NonterminalNode node3 = factory.createNonterminalNode("S", "S ::= (A) .", node2);
        return node3;
    }

}
