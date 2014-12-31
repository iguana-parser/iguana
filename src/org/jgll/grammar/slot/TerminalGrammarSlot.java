package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;


public class TerminalGrammarSlot implements GrammarSlot {
	
	private RegularExpression regex;
	private Matcher matcher;

	public TerminalGrammarSlot(RegularExpression regex) {
		this.regex = regex;
		matcher = regex.getMatcher();
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

	public RegularExpression getRegularExpression() {
		return regex;
	}
	
	public int match(Input input, int i) {
		return matcher.match(input, i);
	}
	
	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

	@Override
	public String toString() {
		return regex.toString();
	}

	@Override
	public void reset() {
	}

}
