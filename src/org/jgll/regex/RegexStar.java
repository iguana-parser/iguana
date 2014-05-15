package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public static RegexStar from(RegularExpression regexp) {
		return new Builder(regexp).build();
	}

	public RegexStar(RegularExpression regexp, String label, Set<Condition> conditions, Object object) {
		super(getName(regexp), label, conditions, object);
		this.regexp = regexp.withoutConditions();
	}
	
	private static String getName(RegularExpression regexp) {
		return regexp + "*";
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
		return new Builder(regexp).addConditions(this.conditions).addConditions(conditions).build();
	}
	
	@Override
	public RegexStar withoutConditions() {
		return RegexStar.from(regexp);
	}
	
	public static class Builder extends SymbolBuilder<RegexStar> {

		private RegularExpression regex;

		public Builder(RegularExpression regex) {
			this.regex = regex;
		}
		
		@Override
		public RegexStar build() {
			return new RegexStar(regex, label, conditions, object);
		}
	}
	
}
