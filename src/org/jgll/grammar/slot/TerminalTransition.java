package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;


public class TerminalTransition extends AbstractTransition {

	private final TerminalGrammarSlot slot;
	
	private final Set<Condition> preConditions;
	
	private final Set<Condition> postConditions;

	public TerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest) {
		this(slot, origin, dest, Collections.emptySet(), Collections.emptySet());
	}
	
	public TerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest, 
							  Set<Condition> preConditions, Set<Condition> postConditions) {
		super(origin, dest);
		this.slot = slot;
		this.preConditions = preConditions;
		this.postConditions = postConditions;
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
		
		NonPackedNode newNode;
		
		if (origin.isFirst()) {
			newNode = cr; 
		} else {
			newNode = parser.getSPPFLookup().getIntermediateNode(dest, node, cr);
		}
		
		dest.execute(parser, input, newNode);
	}

}
