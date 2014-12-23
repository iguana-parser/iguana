package org.jgll.grammar.slot;

import java.util.List;


public class NonterminalGrammarSlot extends ChoiceGrammarSlot {

	public NonterminalGrammarSlot(List<Transition> transitions) {
		super(transitions);
	}
	
	public boolean test(int v)  {
		return false;
	}

}
