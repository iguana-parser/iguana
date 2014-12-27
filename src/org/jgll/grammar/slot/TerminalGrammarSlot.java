package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.regex.RegularExpression;


public class TerminalGrammarSlot implements GrammarSlot {
	
	private RegularExpression regex;

	public TerminalGrammarSlot(RegularExpression regex) {
		this.regex = regex;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

	public RegularExpression getRegularExpression() {
		return regex;
	}

	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

}
