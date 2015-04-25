package org.jgll.parser.ambiguous;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
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
        
        Rule r1 = Rule.withHead(S).addSymbols(S, S, S).build();
        Rule r2 = Rule.withHead(S).addSymbols(S, S).build();
        Rule r3 = Rule.withHead(S).addSymbols(b).build();
        Rule r4 = Rule.withHead(S).build();
        
        grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();
    }
    
    @Test
    public void testParsers1() {
        Input input = Input.fromString(getBs(5));
        GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
        ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
        assertTrue(result.isParseSuccess());
        ParseStatistics parseStatistics = result.asParseSuccess().getStatistics();
		assertEquals(129, parseStatistics.getDescriptorsCount());
		assertEquals(21, parseStatistics.getNonterminalNodesCount());
		assertEquals(21, parseStatistics.getIntermediateNodesCount());
		assertEquals(11, parseStatistics.getTerminalNodesCount());
		assertEquals(179, parseStatistics.getPackedNodesCount());
    }
    
    @Test
    public void testParsers2() {
        Input input = Input.fromString(getBs(10));
        GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
        ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
        assertTrue(result.isParseSuccess());
        ParseStatistics parseStatistics = result.asParseSuccess().getStatistics();
		assertEquals(374, parseStatistics.getDescriptorsCount());
		assertEquals(66, parseStatistics.getNonterminalNodesCount());
		assertEquals(66, parseStatistics.getIntermediateNodesCount());
		assertEquals(21, parseStatistics.getTerminalNodesCount());
		assertEquals(879, parseStatistics.getPackedNodesCount());
    }
    
    @Test
    public void testParsers3() {
        Input input = Input.fromString(getBs(100));
        GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
        ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
        assertTrue(result.isParseSuccess());
        ParseStatistics parseStatistics = result.asParseSuccess().getStatistics();
		assertEquals(26159, parseStatistics.getDescriptorsCount());
		assertEquals(5151, parseStatistics.getNonterminalNodesCount());
		assertEquals(5151, parseStatistics.getIntermediateNodesCount());
		assertEquals(201, parseStatistics.getTerminalNodesCount());
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
