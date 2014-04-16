package org.jgll.regex;

import static org.jgll.regex.automaton.TransitionActionsFactory.getPostActions;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp) {
		super(regexp + "*");
		this.regexp = regexp.clone();
	}
	
	@Override
	protected Automaton createAutomaton() {
		
		/*
		 * Kleene star is a different beast. We cannot simply decide on 
		 * internal transitions to execute actions. We should execute the actions
		 * only when one loop has been done.
		 */
		
		State startState = new State();
		startState.addAction(getPostActions(conditions));
		
		State finalState = new State(true);
		finalState.addAction(getPostActions(conditions));
		
		Automaton automaton = regexp.toAutomaton();
		
		if (!conditions.isEmpty()) {
			automaton.determinize();
		}
		
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
		RegexStar clone = (RegexStar) super.clone();
		clone.regexp = regexp.clone();
		return clone;
	}
	
}
