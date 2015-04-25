package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;

public class FirstTerminalTransition extends AbstractTerminalTransition {

	public FirstTerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, Set<Condition> preConditions, Set<Condition> postConditions) {
		super(slot, origin, dest, preConditions, postConditions);
	}

	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i + length, cr);
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		dest.execute(parser, u, i + length, cr, env);		
	}

}
