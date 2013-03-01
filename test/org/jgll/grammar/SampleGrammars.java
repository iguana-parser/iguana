package org.jgll.grammar;


import static java.util.Arrays.*;

public class SampleGrammars {


    // S ::= a S | A S d | epsilon
    // A ::= a
	public static Grammar gamma2() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('a'), new Nonterminal("S")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("S")).body(new Nonterminal("A"), new Nonterminal("S"), new Character('d')).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("S")).build();
		Rule r4 = new Rule.Builder().head(new Nonterminal("A")).body(new Character('a')).build();
		return Grammar.fromRules("gamma2", asList(r1, r2, r3, r4));
	}
	
}
