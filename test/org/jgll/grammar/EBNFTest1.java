package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= A+
 *      
 * A ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class EBNFTest1 {
	
	private GrammarGraph grammarGraph;

	@Before
	public void init() {
		
		Grammar grammar = new Grammar();
		
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		Character a = new Character('a');
		
		Rule rule1 = new Rule(S, list(new Plus(A)));
		
		Rule rule2 = new Rule(A, list(a));
		
		Iterable<Rule> newRules = EBNFUtil.rewrite(list(rule1, rule2));
		
		grammar.addRules(newRules);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("aaaaaa");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		parser.parse(input, grammarGraph, "S");
	}

}
