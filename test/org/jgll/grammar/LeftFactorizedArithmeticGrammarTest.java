package org.jgll.grammar;

import static org.jgll.util.BitSetUtil.*;
import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
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
 * 	E  ::= T E1
 * 	E1 ::= + T E1 | epsilon
 *  T  ::= F T1
 *  T1 ::= * F T1 |  epsilon
 *  F  ::= (E) | a
 *  
 */
public class LeftFactorizedArithmeticGrammarTest {
	
	private Grammar grammar;

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
	public void createGrammar() {

		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("LeftFactorizedArithmeticExpressions", factory);
		
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
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(grammar.getTokenID(openPar), grammar.getTokenID(a)), grammar.getFirstSet(grammar.getNonterminalByName("E")));
		assertEquals(set(grammar.getTokenID(plus), Epsilon.TOKEN_ID), grammar.getFirstSet(grammar.getNonterminalByName("E1")));
		assertEquals(set(grammar.getTokenID(star), Epsilon.TOKEN_ID), grammar.getFirstSet(grammar.getNonterminalByName("T1")));
		assertEquals(set(grammar.getTokenID(openPar), grammar.getTokenID(a)), grammar.getFirstSet(grammar.getNonterminalByName("T")));
		assertEquals(set(grammar.getTokenID(openPar), grammar.getTokenID(a)), grammar.getFirstSet(grammar.getNonterminalByName("F")));
	}
	
	public void testFollowSets() {
		assertEquals(from(grammar.getTokenID(closePar), EOF.TOKEN_ID), grammar.getFollowSet(grammar.getNonterminalByName("E")));
		assertEquals(from(grammar.getTokenID(closePar), EOF.TOKEN_ID), grammar.getFollowSet(grammar.getNonterminalByName("E1")));
		assertEquals(from(grammar.getTokenID(plus), grammar.getTokenID(closePar), EOF.TOKEN_ID), grammar.getFollowSet(grammar.getNonterminalByName("T1")));
		assertEquals(from(grammar.getTokenID(plus), grammar.getTokenID(closePar), EOF.TOKEN_ID), grammar.getFollowSet(grammar.getNonterminalByName("T")));
		assertEquals(from(grammar.getTokenID(plus), grammar.getTokenID(star), grammar.getTokenID(closePar), EOF.TOKEN_ID), grammar.getFollowSet(grammar.getNonterminalByName("F")));
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("a+a*a+a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
	}

}
