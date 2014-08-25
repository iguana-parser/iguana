package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Input;
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
        assertEquals(129, result.asParseSuccess().getParseStatistics().getDescriptorsCount());
    }
    
    @Test
    public void testParsers2() {
        Input input = Input.fromString(getBs(10));
        GLLParser parser = ParserFactory.newParser(grammar, input);
        ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
        assertTrue(result.isParseSuccess());
        assertEquals(374, result.asParseSuccess().getParseStatistics().getDescriptorsCount());
    }
    
    @Test
    public void testParsers3() {
        Input input = Input.fromString(getBs(100));
        GLLParser parser = ParserFactory.newParser(grammar, input);
        ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
        assertTrue(result.isParseSuccess());
        assertEquals(26159, result.asParseSuccess().getParseStatistics().getDescriptorsCount());
    }
    
    private String getBs(int size) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i++) {
            sb.append("b");
        }
        return sb.toString();
    }
}
