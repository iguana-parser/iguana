package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
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
	
	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("EBNF");
		
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		Terminal a = new Character('a');
		
		Rule rule1 = new Rule(S, list(new Plus(A)));
		
		Rule rule2 = new Rule(A, list(a));
		
		Iterable<Rule> newRules = EBNFUtil.rewrite(list(rule1, rule2));
		
		builder.addRules(newRules);
		
		grammar = builder.build();
		
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		parser.parse(Input.fromString("aaaaaa"), grammar, "S");
	}

}
