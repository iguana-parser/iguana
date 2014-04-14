package org.jgll.regex;

import static org.jgll.regex.automaton.TransitionActionsFactory.*;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public class RegexOpt extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regexp;
	
	public RegexOpt(RegularExpression regexp) {
		super(regexp.getName() + "?");
		this.regexp = regexp.clone();
	}
	
	protected Automaton createAutomaton() {
		State startState = new State();
		startState.addAction(getPostActions(conditions));
		
		State finalState = new State(true);
		
		Automaton automaton = regexp.toAutomaton().copy();
		startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		
		Set<State> finalStates = automaton.getFinalStates();
		for(State s : finalStates) {
			s.setFinalState(false);
			
			for (State incomingState : s.getIncomingStates()) {
				for (Transition t : incomingState.getTransitions()) {
					if(t.getDestination() == s) {
						t.addTransitionAction(getPostActions(conditions));
					}
				}
			}
			
			s.addTransition(Transition.epsilonTransition(finalState));			
		}
		
		startState.addTransition(Transition.epsilonTransition(finalState));
		
		return new Automaton(startState);
	}

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public RegexOpt clone() {
		return (RegexOpt) super.clone();
	}

	@Override
	public Set<Range> getFirstSet() {
		return regexp.getFirstSet();
	}
	
}
