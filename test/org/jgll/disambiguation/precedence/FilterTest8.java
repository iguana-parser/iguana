package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E [ E ]
 * 	   | E +
 * 	   | E *
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest8 {
	
	private Nonterminal E = new Nonterminal("E");
	private Character a = new Character('a');
	private Character plus = new Character('+');
	private Character star = new Character('*');
	private Character ob = new Character('[');
	private Character cb = new Character(']');
	
	private Grammar grammar;
	private GLLParser parser;
	
	@Before
	public void init() {
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("Test8", factory);

		// E ::= E [ E ]
		Rule rule1 = new Rule(E, list(E, ob, E, cb));
		builder.addRule(rule1);
		
		// E ::= E +
		Rule rule2 = new Rule(E, list(E, plus));
		builder.addRule(rule2);
		
		// E ::= E *
		Rule rule3 = new Rule(E, list(E, star));
		builder.addRule(rule3);
		
		// E ::= E + E
		Rule rule4 = new Rule(E, list(E, plus, E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(a));
		builder.addRule(rule5);
		
		// (E, .E [ E ], E + E)
		builder.addPrecedencePattern(E, rule1, 0, rule4);
		
		// (E, .E *, E + E)
		builder.addPrecedencePattern(E, rule2, 0, rule4);
		
		// (E, .E +, E + E)
		builder.addPrecedencePattern(E, rule3, 0, rule4);

		builder.addExceptPattern(E, rule1, 0, rule1);
		builder.addExceptPattern(E, rule1, 0, rule2);
		builder.addExceptPattern(E, rule1, 0, rule3);
	
		grammar = builder.build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("a+a[a+a]");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf, grammar, input);
//		assertTrue(sppf.deepEquals(getSPPF()));
	}

	private SPPFNode getSPPF() {
		return null;
	}

}
