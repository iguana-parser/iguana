package org.iguana.parser.ebnf;

import iguana.regex.Character;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.junit.Test;

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
    static Terminal a = Terminal.from(Character.from('a'));

    static Rule r1 = Rule.withHead(S).addSymbols(Star.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(Star.from(a)).build();
    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();

    static Input input0 = Input.empty();
    static Input input1 = Input.fromString("a");
    static Input input2 = Input.fromString("aa");
    static Input input3 = Input.fromString("aaa");
    static Input input4 = Input.fromString("aaaaaaaaa");

    @Test
    public void testParse0() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input0);
        ParseResult result = Iguana.parse(input0, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse1() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input1);
        ParseResult result = Iguana.parse(input1, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse2() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse3() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input3);
        ParseResult result = Iguana.parse(input3, graph, S);
        assertTrue(result.isParseSuccess());
    }

    @Test
    public void testParse4() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = GrammarGraph.from(grammar, input4);
        ParseResult result = Iguana.parse(input4, graph, S);
        assertTrue(result.isParseSuccess());
    }



}
