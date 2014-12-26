package org.jgll.grammar.slot;

import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


public class NonterminalTransition extends AbstractTransition {

	private NonterminalGrammarSlot slot;

	public NonterminalTransition(NonterminalGrammarSlot slot, 
								 GrammarSlot origin, GrammarSlot dest) {
		super(origin, dest);
		this.slot = slot;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		int ci = node.getRightExtent();
		
		if (!slot.test(input.charAt(ci))) {
			parser.recordParseError(origin);
			return;
		}
		
		if (origin.getPreConditions().stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci)))
			return;
		
		NonterminalGrammarSlot nonterminal = parser.create(dest, slot); 
		if (nonterminal != null) 
			nonterminal.execute(parser, input, node);
	}

}
