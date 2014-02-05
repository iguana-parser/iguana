package org.jgll.grammar;

import static org.jgll.util.BitSetUtil.*;
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
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;



/**
 * 	E  ::= T E1
 * 	E1 ::= + T E1 | epsilon
 *  T  ::= F T1
 *  T1 ::= * F T1 |  epsilon
 *  F  ::= (E) | a
 *  
 */
public class LeftFactorizedArithmeticGrammarTest {
	
	private static final int EPSILON = 0;
	private static final int EOF = 0;

	private Grammar grammar;
	private GLLParser parser;

	Nonterminal E = new Nonterminal("E");
	Nonterminal T = new Nonterminal("T");
	Nonterminal E1 = new Nonterminal("E1");
	Nonterminal F = new Nonterminal("F");
	Nonterminal T1 = new Nonterminal("T1");
	Character plus = new Character('+');
	Character star = new Character('*');
	Character a = new Character('a');
	Character openPar = new Character('(');
	Character closePar = new Character(')');

	@Before
	public void init() {

		GrammarBuilder builder = new GrammarBuilder("LeftFactorizedArithmeticExpressions");
		
		Rule r1 = new Rule(E, list(T, E1));
		Rule r2 = new Rule(E1, list(plus, T, E1));
		Rule r3 = new Rule(E1);
		Rule r4 = new Rule(T, list(F, T1));
		Rule r5 = new Rule(T1, list(star, F, T1));
		Rule r6 = new Rule(T1);
		Rule r7 = new Rule(F, list(openPar, E, closePar));
		Rule r8 = new Rule(F, list(a));
		
		
		builder.addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).addRule(r7).addRule(r8);
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(from(grammar.getTokenID(openPar), grammar.getTokenID(a)), grammar.getNonterminalByName("E").getFirstSet());
		assertEquals(from(grammar.getTokenID(plus), EPSILON), grammar.getNonterminalByName("E1").getFirstSet());
		assertEquals(from(grammar.getTokenID(star), EPSILON), grammar.getNonterminalByName("T1").getFirstSet());
		assertEquals(from(grammar.getTokenID(openPar), grammar.getTokenID(a)), grammar.getNonterminalByName("T").getFirstSet());
		assertEquals(from(grammar.getTokenID(openPar), grammar.getTokenID(a)), grammar.getNonterminalByName("F").getFirstSet());
	}
	
	public void testFollowSets() {
		assertEquals(from(grammar.getTokenID(closePar), EOF), grammar.getNonterminalByName("E").getFollowSet());
		assertEquals(from(grammar.getTokenID(closePar), EOF), grammar.getNonterminalByName("E1").getFollowSet());
		assertEquals(from(grammar.getTokenID(plus), grammar.getTokenID(closePar), EOF), grammar.getNonterminalByName("T1").getFollowSet());
		assertEquals(from(grammar.getTokenID(plus), grammar.getTokenID(closePar), EOF), grammar.getNonterminalByName("T").getFollowSet());
		assertEquals(from(grammar.getTokenID(plus), grammar.getTokenID(star), grammar.getTokenID(closePar), EOF), grammar.getNonterminalByName("F").getFollowSet());
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("a+a*a+a"), grammar, "E");
		Visualization.generateSPPFGraphWithoutIntermeiateNodes("/Users/aliafroozeh/output", sppf, Input.fromString("a+a*a+a"));
	}


}
