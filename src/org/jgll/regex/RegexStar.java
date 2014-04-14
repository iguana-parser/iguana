package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp) {
		super(regexp + "*");
		this.regexp = regexp.clone();
	}
	
	@Override
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(true);
		
		Automaton automaton = regexp.toAutomaton().copy();
		
		startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		
		Set<State> finalStates = automaton.getFinalStates();
		for(State s : finalStates) {
			s.setFinalState(false);
			s.addTransition(Transition.epsilonTransition(finalState));
			s.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		}
		
		startState.addTransition(Transition.epsilonTransition(finalState));
		
		return new Automaton(startState);
	}
	
	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public Set<Range> getFirstSet() {
		return regexp.getFirstSet();
	}
	
	@Override
	public RegexStar clone() {
		return (RegexStar) super.clone();
	}
	
}
