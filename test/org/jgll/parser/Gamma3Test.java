package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 *  S ::= S S S S S
 *      | S S S S
 *      | S S S
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma3Test {
	
	private GrammarGraph grammarGraph;
	private Nonterminal S = Nonterminal.withName("S");
	private Character b = Character.from('b');
	
	@Before
	public void init() {
		
		Grammar grammar = new Grammar();
		
		Rule rule1 = new Rule(S, list(S, S, S, S, S));
		grammar.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(S, S, S, S));
		grammar.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(S, S, S));
		grammar.addRule(rule3);
		
		Rule rule4 = new Rule(S, list(b));
		grammar.addRule(rule4);
		
		grammarGraph = grammar.toGrammarGraph();
	}

	
	@Test
	public void test100bs() {
		Input input = Input.fromString(get100b());		
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		parser.parse(input, grammarGraph, "S");
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 100; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	

}
