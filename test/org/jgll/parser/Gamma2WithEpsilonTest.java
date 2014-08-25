package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 *  S ::= S S S 
 *      | S S 
 *      | b
 *      | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma2WithEpsilonTest {

    private Grammar grammar;
    
    private Nonterminal S = Nonterminal.withName("S");
    private Character b = Character.from('b');
    
    @Before
    public void init() {
        
        Grammar.Builder builder = new Grammar.Builder();
        
        builder.addRule(new Rule(S, list(S, S, S)));
        builder.addRule(new Rule(S, list(S, S)));
        builder.addRule(new Rule(S, list(b)));
        builder.addRule(new Rule(S));
        
        grammar = builder.build();
    }
    
    @Test
    public void testParsers1() {
        Input input = Input.fromString(getBs(5));
        GLLParser parser = ParserFactory.newParser(grammar, input);
        ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
        assertTrue(result.isParseSuccess());
        ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
		assertEquals(129, parseStatistics.getDescriptorsCount());
		assertEquals(21, parseStatistics.getNonterminalNodesCount());
		assertEquals(21, parseStatistics.getIntermediateNodesCount());
		assertEquals(179, parseStatistics.getPackedNodesCount());
    }
    
    @Test
    public void testParsers2() {
        Input input = Input.fromString(getBs(10));
        GLLParser parser = ParserFactory.newParser(grammar, input);
        ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
        assertTrue(result.isParseSuccess());
        ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
		assertEquals(374, parseStatistics.getDescriptorsCount());
		assertEquals(66, parseStatistics.getNonterminalNodesCount());
		assertEquals(66, parseStatistics.getIntermediateNodesCount());
		assertEquals(879, parseStatistics.getPackedNodesCount());
    }
    
    @Test
    public void testParsers3() {
        Input input = Input.fromString(getBs(100));
        GLLParser parser = ParserFactory.newParser(grammar, input);
        ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
        assertTrue(result.isParseSuccess());
        ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
		assertEquals(26159, parseStatistics.getDescriptorsCount());
		assertEquals(5151, parseStatistics.getNonterminalNodesCount());
		assertEquals(5151, parseStatistics.getIntermediateNodesCount());
		assertEquals(530754, parseStatistics.getPackedNodesCount());
    }
    
    private String getBs(int size) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i++) {
            sb.append("b");
        }
        return sb.toString();
    }
}
