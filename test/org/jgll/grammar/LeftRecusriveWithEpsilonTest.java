package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;

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
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class LeftRecusriveWithEpsilonTest {
	
	private GrammarBuilder builder;
	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(A);
		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
													  .addRule(r2)
													  .addRule(r3);
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("a"), grammar, "A");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf, Input.fromString("a"));
		Visualization.generateGSSGraph("/Users/aliafroozeh/output", 
				((GLLParserImpl)parser).getLookupTable().getGSSNodes(),
				((GLLParserImpl)parser).getLookupTable().getEdgesMap());

	}

}
