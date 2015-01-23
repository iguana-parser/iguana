package org.jgll.regex;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;

public class Opt extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private final Symbol s;
	
	private final boolean isRegularExpression;
	
	private Automaton automaton;
	
	private Opt(Builder builder) {
		super(builder);
		this.s = builder.s;
		this.isRegularExpression = s instanceof RegularExpression;
	}

	public static Opt from(Symbol s) {
		return builder(s).build();
	}
	
	public Symbol getSymbol() {
		return s;
	}

	private static String getName(Symbol s) {
//		Verify.verifyNotNull(s);
		return s.getName() + "?";
	}
	
	@Override
	public Automaton getAutomaton() {
		
		if (automaton != null)
			return automaton;
		
		if (!isRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");		
		
		State startState = new State();
		
		State finalState = new State(StateType.FINAL);

		Automaton automaton = ((RegularExpression) s).getAutomaton().copy();
		startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
		
		Set<State> finalStates = automaton.getFinalStates();
		for(State s : finalStates) {
			s.setStateType(StateType.NORMAL);
			s.addTransition(Transition.epsilonTransition(finalState));			
		}
		
		startState.addTransition(Transition.epsilonTransition(finalState));
		
		automaton = new Automaton(startState, name);
		
		return automaton;
	}

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		if (!isRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public String getConstructorCode() {
		return Opt.class.getName() + ".builder(" + s.getConstructorCode() + ")" + super.getConstructorCode() + ".build()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(s);
	}

	@Override
	public String getPattern() {
		if (!isRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getPattern() + "?";
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
	
	public static class Builder extends SymbolBuilder<Opt> {

		private Symbol s;

		public Builder(Symbol s) {
			this.s = s;
		}
		
		@Override
		public Opt build() {
			this.name = getName(s);
			return new Opt(this);
		}
		
	}
	
}
