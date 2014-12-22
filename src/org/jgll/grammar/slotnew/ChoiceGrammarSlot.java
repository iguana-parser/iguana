package org.jgll.grammar.slotnew;

import java.util.List;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.util.Input;


public class ChoiceGrammarSlot implements GrammarSlot {

	private List<Transition> transitions;

	public ChoiceGrammarSlot(List<Transition> transitions) {
		this.transitions = transitions;
	}
	
	@Override
	public GrammarSlot execute(GLLParser parser, Input input, int i) {
		transitions.forEach(t -> t.execute(parser, input, i));
		return null;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		// TODO Auto-generated method stub
		return null;
	}

}
