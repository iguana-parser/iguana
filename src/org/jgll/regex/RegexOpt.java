package org.jgll.regex;

import static org.jgll.regex.automaton.TransitionActionsFactory.*;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public class RegexOpt extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regexp;
	
	public RegexOpt(RegularExpression regexp) {
		this(regexp, Collections.<Condition>emptySet());
	}
	
	public RegexOpt(RegularExpression regexp, Set<Condition> conditions) {
		super(regexp.getName() + "?", conditions);
		this.regexp = regexp.withConditions(conditions);
	}
	
	protected Automaton createAutomaton() {
		State startState = new State();
		startState.addAction(getPostActions(conditions));
		
		State finalState = new State(true);

		Automaton automaton = regexp.getAutomaton();
		startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		
		Set<State> finalStates = automaton.getFinalStates();
		for(State s : finalStates) {
			s.setFinalState(false);
			s.addTransition(Transition.epsilonTransition(finalState));			
		}
		
		startState.addTransition(Transition.epsilonTransition(finalState));
		
		return new Automaton(startState, name);
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
	public RegularExpression withConditions(Set<Condition> conditions) {
		return new RegexOpt(regexp, conditions);
	}
	
}
