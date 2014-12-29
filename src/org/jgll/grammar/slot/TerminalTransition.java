package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;


/**
 * 
 * Corresponds to grammar slots of the form A ::= alpha . b beta, 
 * where |alpha| > 2 or |alpha| = 1 and |beta| > 0.
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalTransition extends AbstractTerminalTransition {

	public TerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest, 
							  Set<Condition> preConditions, Set<Condition> postConditions) {
		super(slot, origin, dest, preConditions, postConditions);
	}
	
	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, int i, NonPackedNode node) {
		dest.execute(parser, i + length, parser.getIntermediateNode(dest, node, cr));
	}
	
}
