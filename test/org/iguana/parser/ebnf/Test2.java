package org.iguana.parser.ebnf;

import iguana.parsetrees.sppf.SPPFVisualization;
import iguana.parsetrees.tree.Tree;
import iguana.parsetrees.tree.TreeVisualization;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Plus;
import org.iguana.regex.Star;
import org.iguana.util.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * S ::= A*
 * A ::= 'a'*
 */
public class Test2 {

    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Character a = Character.from('a');

    static Rule r1 = Rule.withHead(S).addSymbols(Plus.from(A)).build();
    static Rule r2 = Rule.withHead(A).addSymbols(Plus.from(a)).build();

    private static Grammar grammar = Grammar.builder().addRules(r1, r2).build();
    private static Input input = Input.fromString("aaa");

    @Test
    public void testParser() {
        grammar = EBNFToBNF.convert(grammar);
        GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input, graph, S);
        assertTrue(result.isParseSuccess());
        System.out.println(result.asParseSuccess().getTree());
        SPPFVisualization.generate(result.asParseSuccess().getSPPFNode(), "/Users/afroozeh/output", "sppf", input);
        TreeVisualization.generate((Tree) result.asParseSuccess().getTree(), "/Users/afroozeh/output", "tree", input);

//        assertEquals(getParseResult(graph), result);
    }

}
