package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.grammar.symbol.TerminalFactory;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= "if" L S L "then" L S 
 *     | s
 *     
 * L ::= " "    
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest3 {
	
	private Grammar grammar;
	private GLLParser rdParser;
	private GLLParser levelParser;

	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Keyword iff = new Keyword("if", new int[] {'i', 'f'});
		Keyword then = new Keyword("then", new int[] {'t', 'h', 'e', 'n'});
		Nonterminal L = new Nonterminal("L");
		Terminal s = new Character('s');
		Terminal ws = new Character(' ');
		
		Rule r1 = new Rule(S, iff, L, S, L, then, L, S);
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		
		grammar = new GrammarBuilder().addRule(r1)
								   .addRule(r2)
								   .addRule(r3)
								   .addRule(GrammarBuilder.fromKeyword(iff))
								   .addRule(GrammarBuilder.fromKeyword(then)).build();
		
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
		levelParser = ParserFactory.createLevelParser(grammar);
	}
	
	
	@Test
	public void testFirstSet() {
		assertEquals(set(new Character('i'), TerminalFactory.from('s')), grammar.getNonterminalByName("S").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(4, grammar.getLongestTerminalChain());
	}

	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("if s then s");
		NonterminalSymbolNode sppf1 = rdParser.parse(input, grammar, "S");
		NonterminalSymbolNode sppf2 = levelParser.parse(input, grammar, "S");
//		Visualization.generateSPPFGraph("/Users/ali/output", sppf1, input);
		assertTrue(sppf1.deepEquals(sppf2));
	}
	
}
