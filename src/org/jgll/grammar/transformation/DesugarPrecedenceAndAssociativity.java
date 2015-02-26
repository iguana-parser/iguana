package org.jgll.grammar.transformation;

import org.jgll.grammar.Grammar;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * Description:
 * 
 * 		- Two parameters, l and r, are introduced to pass a precedence level,
 *        and each rule gets a precedence level. 
 * 		  We need two parameters to account for left (l) and right (r) recursion.
 *      		- for left recursive rules, symbols on the left should pass as the 1st argument either 
 *              (a) the precedence level of the rule if the rule is left-associative; or 
 *              (b) the precedence level plus one if it is right-associative or non-associative, or
 *                  the first rule in the current group starting from the top.
 *          - for right recursive rules, symbols on the right should pass as the 2nd argument either
 *              (a) the precedence level of the rule if the rule is left-associative; or
 *              (b) the precedence level of the rule if it is right-associative, or
 *                  the first rule in the current group starting from the top.
 *          - The 2nd argument to left recursive symbols or the 1st argument to right recursive symbols
 *            is defined as follows:
 *            (a) the precedence level is propagated if there are rules with lower precedence that
 *                can be affected (this is to account for deep cases), i.e.,
 *                l if right, or left and right associative rules are below, otherwise 0
 *                r if left, or left and right associative rules are below, otherwise 0
 *      - Conditions are added to the rules as follows:
 *          (a) if left recursive, the precedence level (or the highest in the current group of rules) >= r
 *          (b) if right recursive, the precedence level (or the highest in the current group of rules) >= l
 *          (c) if the rule is part of a group but not the 1st in the group:
 *                - if left associative, the precedence level != r
 *                - if right associative, the precedence level != l
 *          (d) if the rule is part of a group which is a left associative with respect to each other,
 *                - if left associative, { the precedence level of a rule in the group != r}
 *                  (the lowest precedence level minus 1???)
 *                - if right associative, { the precedence level of a rule in the group != l}
 *                  (the lowest precedence level minus 1???)
 *        
 */

public class DesugarPrecedenceAndAssociativity implements GrammarTransformation {
	
	@Override
	public Grammar transform(Grammar grammar) {
		return grammar;
	}

}
