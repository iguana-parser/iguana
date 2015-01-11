package org.jgll.regex;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;

public class RegexOpt extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regex;
	
	public RegexOpt(Builder builder) {
		super(builder);
		this.regex = builder.regex;
	}

	public static RegexOpt from(RegularExpression regexp) {
		return new Builder(regexp).build();
	}

	private static String getName(RegularExpression regexp) {
		return regexp.getName() + "?";
	}
	
	protected Automaton createAutomaton() {
		State startState = new State();
//		startState.addAction(getPostActions(conditions));
		
		State finalState = new State(StateType.FINAL);

		Automaton automaton = regex.getAutomaton().copy();
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
	public Set<CharacterRange> getFirstSet() {
		return regex.getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}
	
	public static Builder builder(RegularExpression regex) {
		return new Builder(regex);
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(this);
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new RegexOpt(")
		  .append(regex.getConstructorCode(registry) + ", ")
		  .append(label + ", ")
		  .append("new HashSet<>(), ")
		  .append("null")
		  .append(")")
		  ;
		return sb.toString();
	}

	public static class Builder extends SymbolBuilder<RegexOpt> {

		private RegularExpression regex;
		
		public Builder(RegularExpression regex) {
			super(getName(regex));
			this.regex = regex;
		}
		
		public Builder(RegexOpt opt) {
			super(opt);
			this.regex = opt.regex;
		}
		
		@Override
		public RegexOpt build() {
			return new RegexOpt(this);
		}
	}
	
}
