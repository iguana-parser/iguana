package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.*;
import org.jgll.grammar.ebnf.*;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.util.*;
import org.junit.*;

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
		Character a = Character.from('a');
		
		Rule rule1 = new Rule(S, list(new Plus(A)));
		
		Rule rule2 = new Rule(A, list(a));
		
		Iterable<Rule> newRules = EBNFUtil.rewrite(list(rule1, rule2));
		
		grammar.addRules(newRules);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aaaaaa");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		parser.parse(input, grammarGraph, "S");
	}

}
