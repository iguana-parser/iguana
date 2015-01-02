package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;


/**
 * 
 * Corresponds to grammar slots of the form A ::= b .
 * 
 * @author Ali Afroozeh
 *
 */
public class FirstAndLastTerminalTransition extends AbstractTerminalTransition {

	public FirstAndLastTerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, Set<Condition> preConditions, Set<Condition> postConditions) {
		super(slot, origin, dest, preConditions, postConditions);
	}

	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i + length, parser.getNonterminalNode((EndGrammarSlot) dest, cr));
	}
}
