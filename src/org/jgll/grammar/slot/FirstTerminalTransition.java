package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;

public class FirstTerminalTransition extends AbstractTerminalTransition {

	public FirstTerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, 
			GrammarSlot dest, Set<Condition> preConditions, Set<Condition> postConditions) {
		super(slot, origin, dest);
	}
	
	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i + length, cr);		
	}

}
