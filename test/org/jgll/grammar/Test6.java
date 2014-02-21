package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A B C
 *     | A B D
 *     
 * A ::= 'a'
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test6 {

	private Grammar grammar;
	
	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Nonterminal D = new Nonterminal("D");
		
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		
		Rule r1 = new Rule(S, list(A, B, C));
		Rule r2 = new Rule(S, list(A, B, D));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(B, list(b));
		Rule r5 = new Rule(C, list(c));
		Rule r6 = new Rule(D, list(c));
		
		grammar = new GrammarBuilder("test5").addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf1 = parser.parse(input, grammar, "S");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf1, input);
	}	
}
	