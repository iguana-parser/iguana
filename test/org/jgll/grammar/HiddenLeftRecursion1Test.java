package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.GLLParserImpl;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B A a
 *     | D A b
 *     | c
 * 
 * B ::= x | epsilon
 * 
 * D ::= y | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class HiddenLeftRecursion1Test {

	private GrammarBuilder builder;
	private Grammar grammar;
	private GLLParser rdParser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal D = new Nonterminal("D");
		
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		Character x = new Character('x');
		Character y = new Character('y');
		
		Rule r1 = new Rule(A, list(B, A, a));
		Rule r2 = new Rule(A, list(D, A, b));
		Rule r3 = new Rule(A, list(c));
		Rule r4 = new Rule(B, list(x));
		Rule r5 = new Rule(B);
		Rule r6 = new Rule(D, list(y));
		Rule r7 = new Rule(D);
		

		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
														 .addRule(r2)
														 .addRule(r3)
														 .addRule(r4)
														 .addRule(r5)
														 .addRule(r6)
														 .addRule(r7);
			
		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("xca"), grammar, "A");
		NonterminalSymbolNode sppf2 = rdParser.parse(Input.fromString("ycb"), grammar, "A");
		NonterminalSymbolNode sppf3 = rdParser.parse(Input.fromString("cababaab"), grammar, "A");
		NonterminalSymbolNode sppf4 = rdParser.parse(Input.fromString("xcabbbbb"), grammar, "A");
		NonterminalSymbolNode sppf5 = rdParser.parse(Input.fromString("ycaaaabaaaa"), grammar, "A");
	}
	
}