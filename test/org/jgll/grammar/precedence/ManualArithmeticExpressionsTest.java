package org.jgll.grammar.precedence;

import static org.jgll.util.CollectionsUtil.list;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E + T
 *     | T
 * 
 * T ::= T * F
 *     | F
 *     
 * F ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class ManualArithmeticExpressionsTest {

	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {

		GrammarBuilder builder = new GrammarBuilder("ArithmeticExpressions");

		Nonterminal E = new Nonterminal("E");
		Nonterminal T = new Nonterminal("T");
		Nonterminal F = new Nonterminal("F");

		// E ::= E + T
		Rule rule1 = new Rule(E, list(E, new Character('+'), T));
		builder.addRule(rule1);
		
		// E ::= T
		Rule rule2 = new Rule(E, list(T));
		builder.addRule(rule2);
		
		// T ::= T * F
		Rule rule3 = new Rule(T, list(T, new Character('*'), F));
		builder.addRule(rule3);
		
		// T ::= F
		Rule rule4 = new Rule(T, list(F));
		builder.addRule(rule4);		
		
		
		// F ::= a
		Rule rule5 = new Rule(F, list(new Character('a')));
		builder.addRule(rule5);

		
		builder.rewritePrecedencePatterns();
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}

	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("a+a*a+a"), grammar, "E");
	}
	
}
