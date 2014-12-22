package org.jgll.grammar.slotnew;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.util.Input;


public class SingleGrammarSlot implements GrammarSlot {

	private Transition transition;

	public SingleGrammarSlot(Transition transition) {
		this.transition = transition;
	}
	
	@Override
	public GrammarSlot execute(GLLParser parser, Input input, int i) {
		return transition.execute(parser, input, i);
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

}
