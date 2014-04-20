package org.jgll.regex;

import static org.jgll.regex.automaton.TransitionActionsFactory.getPostActions;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp, Set<Condition> conditions) {
		super(regexp + "*", conditions);
		this.regexp = regexp;
	}
	
	public RegexStar(RegularExpression regexp) {
		this(regexp, Collections.<Condition>emptySet());
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
		
		Automaton automaton = regexp.getAutomaton().copy();
		
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
	public RegexStar withConditions(Set<Condition> conditions) {
		conditions.addAll(this.conditions);
		return new RegexStar(regexp, conditions);
	}
	
}
