package org.jgll.grammar.slotnew;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;


public class TerminalTransition extends AbstractTransition {

	private TerminalGrammarSlot slot;

	public TerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest, Set<Condition> preConditions, Set<Condition> postConditions) {
		super(origin, dest, preConditions, postConditions);
		this.slot = slot;
	}

	@Override
	public GrammarSlot execute(GLLParser parser, Input input, int i) {
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), i))) 
			return null;

		int length = slot.getRegularExpression().matcher().match(input, i);
		
		if (length < 0) {
			parser.recordParseError(origin);
			return null;
		}

		if (postConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), i + length))) 
			return null;
	
		TerminalNode cr = parser.getSPPFLookup().getTerminalNode(slot, i, i + length);
		
		parser.setCurrentInputIndex(i + length);
		
		NonPackedNode node = nodeCreator.create(parser, next, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		return next;		
	}

}
