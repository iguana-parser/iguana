package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

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
	public static Grammar arithmeticExpressions() {
		
		Grammar grammar = new Grammar();

		Nonterminal E = new Nonterminal("E");

		Rule rule1 = new Rule(E, list(E, new Character('^'), E));
		grammar.addRule(rule1);
		
		Rule rule2 = new Rule(E, list(new Character('-'), E));
		grammar.addRule(rule2);

		Rule rule3 = new Rule(E, list(E, new Character('*'), E));
		grammar.addRule(rule3);
		
		Rule rule4 = new Rule(E, list(E, new Character('/'), E));
		grammar.addRule(rule4);
		
		Rule rule5 = new Rule(E, list(E, new Character('+'), E));
		grammar.addRule(rule5);
		
		Rule rule6 = new Rule(E, list(E, new Character('-'), E));
		grammar.addRule(rule6);
		
		Rule rule7 = new Rule(E, list(new Character('a')));
		grammar.addRule(rule7);
		
		Rule rule8 = new Rule(E, list(E, new Character('-'), E, new Character(';')));
		grammar.addRule(rule8);
				
		Rule rule9 = new Rule(E, list(E, new Character('+')));
		grammar.addRule(rule9);

		Rule rule10 = new Rule(E, list(new Character('('), E, new Character(')')));
		grammar.addRule(rule10);
		
		return grammar;
	}
	
	/**
	 * 
	 * S ::= a S b S
	 *     | a S
	 *     | s
	 *      
	 */
	public static Grammar danglingElse() {
		
		Grammar grammar = new Grammar();
		
		Nonterminal S = new Nonterminal("S");
		Character s = new Character('s');
		Character a = new Character('a');
		Character b = new Character('b');

		Rule rule1 = new Rule(S, list(a, S));
		grammar.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(Group.of(a, S, b, S)));
		grammar.addRules(EBNFUtil.rewrite(rule2));
		
		Rule rule3 = new Rule(S, list(s));
		grammar.addRule(rule3);

		return grammar;
	}
	
}
