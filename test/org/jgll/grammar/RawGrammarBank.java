package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class RawGrammarBank {

	
	/**
	 *  E ::= E ^ E
     * 	    | - E
     *      | E * E
     *      | E / E
     *      | E + E
     *      | E - E
     *    	| a
	 */
	public static GrammarBuilder arithmeticExpressions() {
		
		GrammarBuilder builder = new GrammarBuilder("ArithmeticExpressions");

		Nonterminal E = new Nonterminal("E");

		Rule rule1 = new Rule(E, list(E, new Character('^'), E));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule2);

		Rule rule3 = new Rule(E, list(E, new Character('*'), E));
		builder.addRule(rule3);
		
		Rule rule4 = new Rule(E, list(E, new Character('/'), E));
		builder.addRule(rule4);
		
		Rule rule5 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule5);
		
		Rule rule6 = new Rule(E, list(E, new Character('-'), E));
		builder.addRule(rule6);
		
		Rule rule8 = new Rule(E, list(E, new Character('-'), E, new Character(';')));
		builder.addRule(rule8);
		
		Rule rule7 = new Rule(E, list(new Character('a')));
		builder.addRule(rule7);
		
		return builder;
	}
	
}
