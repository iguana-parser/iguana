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
 * A ::= B A c
 * 
 * B ::= b | epsilon
 * 
 * A ::= a
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
		
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		
		Rule r1 = new Rule(A, list(B, A, c));
		Rule r4 = new Rule(A, list(a));
		Rule r2 = new Rule(B, list(b));
		Rule r3 = new Rule(B);

		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
														 .addRule(r2)
														 .addRule(r3)
														 .addRule(r4);
			
		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("bac"), grammar, "A");
		Visualization.generateGSSGraph("/Users/ali/output", ((GLLParserImpl) rdParser).getLookupTable().getGSSNodes());
	}
	
}