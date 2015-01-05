package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	public static RegexStar from(RegularExpression regexp) {
		return new Builder(regexp).build();
	}

	public RegexStar(Builder builder) {
		super(builder);
		this.regex = builder.regex;
	}
	
	private static String getName(RegularExpression regexp) {
		return regexp + "*";
	}
	
	@Override
	protected Automaton createAutomaton() {
		
		State startState = new State();
		
		State finalState = new State(StateType.FINAL);
		
		Automaton automaton = regex.getAutomaton().copy();
		
		if (!preConditions.isEmpty()) {
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
	public Set<CharacterRange> getFirstSet() {
		return regex.getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return regex.getFirstSet();
	}

	public static class Builder extends SymbolBuilder<RegexStar> {

		private RegularExpression regex;

		public Builder(RegularExpression regex) {
			super(getName(regex));
			this.regex = regex;
		}
		
		public Builder(RegexStar regexStar) {
			super(regexStar);
			this.regex = regexStar.regex;
		}
		
		@Override
		public RegexStar build() {
			return new RegexStar(this);
		}
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new RegexStar(")
		  .append(regex.getConstructorCode(registry) + ", ")
		  .append(label + ", ")
		  .append("new HashSet<>(), ")
		  .append("null")
		  .append(")")
		  ;
		return sb.toString();
	}

}
