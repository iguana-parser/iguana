package org.jgll.grammar.slotnew;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


public class SingleGrammarSlot implements GrammarSlot {

	private Transition transition;

	public SingleGrammarSlot(Transition transition) {
		this.transition = transition;
	}
	
	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		transition.execute(parser, input, node);
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

}
