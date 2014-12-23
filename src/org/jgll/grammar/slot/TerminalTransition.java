package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;


public class TerminalTransition extends AbstractTransition {

	private TerminalGrammarSlot slot;

	public TerminalTransition(TerminalGrammarSlot slot, 
							  GrammarSlot origin, 
							  GrammarSlot dest, 
							  Set<Condition> preConditions, 
							  Set<Condition> postConditions,
							  NodeCreator nodeCreator) {
		super(origin, dest, preConditions, postConditions, nodeCreator);
		this.slot = slot;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		
		int ci = node.getRightExtent();
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(input, parser.getCurrentGSSNode(), ci))) 
			return;

		int length = slot.getRegularExpression().getMatcher().match(input, ci);
		
		if (length < 0) {
			parser.recordParseError(origin);
			return;
		}

		if (postConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci + length))) 
			return;
	
		TerminalNode cr = parser.getSPPFLookup().getTerminalNode(slot, ci, ci + length);
		
		NonPackedNode newNode = nodeCreator.create(parser, dest, node, cr);
		
		dest.execute(parser, input, newNode);		
	}

}
