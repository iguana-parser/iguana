package org.jgll.regex;

import static org.jgll.regex.automaton.TransitionActionsFactory.*;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;

public class RegexOpt extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regexp;
	
	public RegexOpt(RegularExpression regexp, String label, Set<Condition> conditions, Object object) {
		super(getName(regexp), label, conditions, object);
		this.regexp = regexp.withoutConditions();
	}

	public static RegexOpt from(RegularExpression regexp) {
		return new Builder(regexp).build();
	}

	private static String getName(RegularExpression regexp) {
		return regexp.getName() + "?";
	}
	
	protected Automaton createAutomaton() {
		State startState = new State();
		startState.addAction(getPostActions(conditions));
		
		State finalState = new State(StateType.FINAL);

		Automaton automaton = regexp.getAutomaton().copy();
		startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		
		Set<State> finalStates = automaton.getFinalStates();
		for(State s : finalStates) {
			s.setStateType(StateType.NORMAL);
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
	public Set<Range> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public RegularExpression withConditions(Set<Condition> conditions) {
		return new Builder(regexp).addConditions(this.conditions).addConditions(conditions).build();
	}

	@Override
	public RegexOpt withoutConditions() {
		return RegexOpt.from(regexp);
	}
	
	public static class Builder extends SymbolBuilder<RegexOpt> {

		private RegularExpression regexp;
		
		public Builder(RegularExpression regexp) {
			this.regexp = regexp;
		}
		
		@Override
		public RegexOpt build() {
			return new RegexOpt(regexp, label, conditions, object);
		}
	}
	
}
