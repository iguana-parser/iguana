package org.jgll.grammar.slot;

import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;


public class TerminalTransition extends AbstractTransition {

	private TerminalGrammarSlot slot;

	public TerminalTransition(TerminalGrammarSlot slot, 
							  GrammarSlot origin, 
							  GrammarSlot dest) {
		super(origin, dest);
		this.slot = slot;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		
		int ci = node.getRightExtent();
		
		if (origin.getPreConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, parser.getCurrentGSSNode(), ci))) 
			return;

		int length = slot.getRegularExpression().getMatcher().match(input, ci);
		
		if (length < 0) {
			parser.recordParseError(origin);
			return;
		}

		if (origin.getPostConditions().stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci + length))) 
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
