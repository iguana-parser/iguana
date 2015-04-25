package org.iguana.grammar.slot;

import java.util.Set;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Condition;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.TerminalNode;


/**
 * 
 * Corresponds to grammar slots of the form A ::= alpha . b beta, 
 * where |alpha| > 2 or |alpha| = 1 and |beta| > 0.
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalTransition extends AbstractTerminalTransition {

	public TerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, 
							  Set<Condition> preConditions, Set<Condition> postConditions) {
		super(slot, origin, dest, preConditions, postConditions);
	}

	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i + length, parser.getIntermediateNode(dest, node, cr));
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		dest.execute(parser, u, i + length, parser.getIntermediateNode(dest, node, cr, env), env);
	}
	
}
