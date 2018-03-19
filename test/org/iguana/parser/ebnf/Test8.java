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
import org.iguana.util.TestRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 *
 * S ::= A*
 * A ::= 'a'*
 *
 */
public class Test8 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Char.from('a'));

    static Rule r1 = Rule.withHead(S).addSymbols(Star.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(Star.from(a)).build();

    static Start start = Start.from(S);
    private static Grammar grammar = EBNFToBNF.convert(new DesugarStartSymbol().transform(Grammar.builder().addRules(r1, r2).setStartSymbol(start).build()));

    static Input input0 = Input.empty();
    static Input input1 = Input.fromString("a");
    static Input input2 = Input.fromString("aa");
    static Input input3 = Input.fromString("aaa");
    static Input input4 = Input.fromString("aaaaaaaaa");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "ebnf").toAbsolutePath().toString();
        TestRunner.record(grammar, input0, 1, path + "/Test8");
        TestRunner.record(grammar, input1, 2, path + "/Test8");
        TestRunner.record(grammar, input2, 3, path + "/Test8");
        TestRunner.record(grammar, input3, 4, path + "/Test8");
        TestRunner.record(grammar, input4, 5, path + "/Test8");
    }

    @Test
    public void testParse0() {
        GrammarGraph graph = GrammarGraph.from(grammar, input0);
        ParseResult result = Iguana.parse(input0, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse1() {
        GrammarGraph graph = GrammarGraph.from(grammar, input1);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse2() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse3() {
        GrammarGraph graph = GrammarGraph.from(grammar, input3);
        ParseResult result = Iguana.parse(input3, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse4() {
        GrammarGraph graph = GrammarGraph.from(grammar, input4);
        ParseResult result = Iguana.parse(input4, graph, S);
        assertTrue(result.isParseSuccess());
    }



}
