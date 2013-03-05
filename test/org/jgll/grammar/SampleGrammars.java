package org.jgll.grammar;


import static java.util.Arrays.*;

public class SampleGrammars {


	/**
	 *	S ::= a S | A S d | epsilon
	 * 	A ::= a
	 */
	public static Grammar gamma2() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('a'), new Nonterminal("S")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("S")).body(new Nonterminal("A"), new Nonterminal("S"), new Character('d')).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("S")).build();
		Rule r4 = new Rule.Builder().head(new Nonterminal("A")).body(new Character('a')).build();
		return Grammar.fromRules("gamma2", asList(r1, r2, r3, r4));
	}
	

	/**
	 * 	E  ::= T E1
	 * 	E1 ::= + T E1 | epsilon
	 *  T  ::= F T1
	 *  T1 ::= * F T1 |  epsilon
	 *  F  ::= (E) | a
	 */
	public static Grammar leftFactorizedArithmeticExpressions() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("E")).body(new Nonterminal("T"), new Nonterminal("E1")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("E1")).body(new Character('+'), new Nonterminal("T"), new Nonterminal("E1")).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("E1")).build();
		Rule r4 = new Rule.Builder().head(new Nonterminal("T")).body(new Nonterminal("F"), new Nonterminal("T1")).build();
		Rule r5 = new Rule.Builder().head(new Nonterminal("T1")).body(new Character('*'), new Nonterminal("F"), new Nonterminal("T1")).build();
		Rule r6 = new Rule.Builder().head(new Nonterminal("T1")).build();
		Rule r7 = new Rule.Builder().head(new Nonterminal("F")).body(new Character('('), new Nonterminal("E"), new Character(')')).build();
		Rule r8 = new Rule.Builder().head(new Nonterminal("F")).body(new Character('a')).build();
		return Grammar.fromRules("LeftFactorizedArithmeticExpressions", asList(r1, r2, r3, r4, r5, r6, r7, r8));
	}
	
}
