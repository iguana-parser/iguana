package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

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
 * A ::= B c
 *     | C d
 *     | e
 * 
 * B ::= A f
 *     | A g
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion3Test {

	private GrammarBuilder builder;
	
	private Grammar grammar;
	private GLLParser rdParser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		
		Character c = new Character('c');
		Character d = new Character('d');
		Character e = new Character('e');
		Character f = new Character('f');
		Character g = new Character('g');
		
		Rule r1 = new Rule(A, list(B, c));
		Rule r2 = new Rule(A, list(C, d));
		Rule r3 = new Rule(A, list(e));
		Rule r4 = new Rule(B, list(A, f));
		Rule r5 = new Rule(C, list(A, g));
		
		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
													     .addRule(r2)
													     .addRule(r3)
													     .addRule(r4)
													     .addRule(r5);
		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("A").isNullable());
		assertFalse(grammar.getNonterminalByName("B").isNullable());
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("efcfc"), grammar, "A");
		NonterminalSymbolNode sppf2 = rdParser.parse(Input.fromString("egdgdgd"), grammar, "A");
		NonterminalSymbolNode sppf3 = rdParser.parse(Input.fromString("egdfcgd"), grammar, "A");
	}

	
}