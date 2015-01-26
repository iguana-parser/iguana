package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;


public class Star extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private final boolean isRegularExpression;
	
	private List<Symbol> separators;
	
	private Automaton automaton;
	
	public static Star from(Symbol s) {
		return builder(s).build();
	}

	private Star(Builder builder) {
		super(builder);
		this.s = builder.s;
		this.isRegularExpression = s instanceof RegularExpression;
	}
	
	private static String getName(Symbol s) {
		return s + "*";
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
			s.addTransition(Transition.epsilonTransition(automaton.getStartState()));
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
		if (!isRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getFirstSet();
	}
	
	@Override
	public String getConstructorCode() {
		return Star.class.getSimpleName() + ".builder(" + s.getConstructorCode() + ")" + super.getConstructorCode() + ".build()";
	}
	

	@Override
	public Builder copyBuilder() {
		return builder(s);
	}

	@Override
	public String getPattern() {
		if (!isRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getPattern() + "*"; 
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
	
	public static class Builder extends SymbolBuilder<Star> {

		private Symbol s;
		private List<Symbol> separators = new ArrayList<>();
		
		public Builder(Symbol s) {
			this.name = getName(s);
			this.s = s;
		}
		
		@Override
		public Star build() {
			return new Star(this);
		}
		
		public Builder addSeparator(Symbol symbol) {
			separators.add(symbol);
			return this;
		}
		
	}
}
