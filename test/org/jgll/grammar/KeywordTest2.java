package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbols.Character;
import org.jgll.grammar.symbols.Keyword;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= "if" B
 * 
 * B ::= [b]
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest2 {
	
	private Grammar grammar;
	private GLLParser rdParser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Keyword iff = new Keyword("if", new int[] {'i', 'f'});
		
		Rule r1 = new Rule(A, list(iff, B));
		Rule r2 = new Rule(B, new Character('b'));
		
		GrammarBuilder builder = new GrammarBuilder();
		builder.addRule(r1);
		builder.addRule(r2);
		builder.addRule(GrammarBuilder.fromKeyword(iff));
		
		grammar = builder.build();
		rdParser = ParserFactory.recursiveDescentParser(grammar);
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(new Character('i')), grammar.getNonterminalByName("A").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(2, grammar.getLongestTerminalChain());
	}

	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("ifb"), grammar, "A");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf);
	}
	
}
