package org.jgll.regex;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;
import org.jgll.util.CollectionsUtil;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp, Set<Condition> conditions) {
		super(regexp + "*", conditions);
		this.regexp = regexp.withoutConditions();
	}
	
	public RegexStar(RegularExpression regexp) {
		this(regexp, Collections.<Condition>emptySet());
	}
	
	@Override
	protected Automaton createAutomaton() {
		
		State startState = new State();
		
		State finalState = new State(StateType.FINAL);
		
		Automaton automaton = regexp.getAutomaton().copy();
		
		if (!conditions.isEmpty()) {
			automaton.determinize();
		}
		
		startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		
		Set<State> finalStates = automaton.getFinalStates();
		
		for(State s : finalStates) {
			s.setStateType(StateType.NORMAL);
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
	public Set<Range> getNotFollowSet() {
		return regexp.getFirstSet();
	}

	@Override
	public RegexStar withConditions(Set<Condition> conditions) {
		return new RegexStar(regexp, CollectionsUtil.union(conditions, this.conditions));
	}
	
	@Override
	public RegexStar withoutConditions() {
		return new RegexStar(regexp);
	}
	
}
