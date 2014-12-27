package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;


public class BodyGrammarSlot extends AbstractGrammarSlot {

	public BodyGrammarSlot() {
		super();
	}
	
	public BodyGrammarSlot(Set<Transition> transitions) {
		super(transitions);
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

}
