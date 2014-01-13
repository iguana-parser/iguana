package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;

import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarBank {

	
	/**
	 *  E ::= E ^ E
     * 	    | - E
     *      | E +
     *      | E * E
     *      | E / E
     *      | E + E
     *      | E - E
     *      | ( E )
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
		
		Rule rule7 = new Rule(E, list(new Character('a')));
		builder.addRule(rule7);
		
		Rule rule8 = new Rule(E, list(E, new Character('-'), E, new Character(';')));
		builder.addRule(rule8);
				
		Rule rule9 = new Rule(E, list(E, new Character('+')));
		builder.addRule(rule9);

		Rule rule10 = new Rule(E, list(new Character('('), E, new Character(')')));
		builder.addRule(rule10);
		
		return builder;
	}
	
	/**
	 * 
	 * S ::= a S b S
	 *     | a S
	 *     | s
	 *      
	 */
	public static GrammarBuilder danglingElse() {
		
		GrammarBuilder builder = new GrammarBuilder("DanglingElse");
		
		Nonterminal S = new Nonterminal("S");
		Character s = new Character('s');
		Character a = new Character('a');
		Character b = new Character('b');

		Rule rule1 = new Rule(S, list(a, S));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(Group.of(a, S, b, S)));
		builder.addRules(EBNFUtil.rewrite(rule2));
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);

		return builder;
	}
	
}
