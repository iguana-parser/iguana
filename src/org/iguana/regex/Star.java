package org.jgll.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.traversal.ISymbolVisitor;

import com.google.common.collect.ImmutableList;


public class Star extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private final boolean allRegularExpression;
	
	private final List<Symbol> separators;
	
	public static Star from(Symbol s) {
		return builder(s).build();
	}

	private Star(Builder builder) {
		super(builder);
		this.s = builder.s;
		this.separators = ImmutableList.copyOf(builder.separators);
		this.allRegularExpression = s instanceof RegularExpression;
	}
	
	private static String getName(Symbol s) {
		return s + "*";
	}
	
	@Override
	protected Automaton createAutomaton() {
		
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		//TODO: add separators to the DFA
		State startState = new State();
		State finalState = new State(StateType.FINAL);
		
		Automaton automaton = ((RegularExpression) s).getAutomaton().copy();
		
		startState.addEpsilonTransition(automaton.getStartState());
		
		Set<State> finalStates = automaton.getFinalStates();
		
		for(State s : finalStates) {
			s.setStateType(StateType.NORMAL);
			s.addEpsilonTransition(finalState);
			s.addEpsilonTransition(automaton.getStartState());
		}
		
		startState.addEpsilonTransition(finalState);
		
		return Automaton.builder(startState).build();
	}
	
	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getFirstSet();
	}
	
	@Override
	public String getConstructorCode() {
		return Star.class.getSimpleName() + 
			   ".builder(" + s.getConstructorCode() + ")" + 
			   super.getConstructorCode() +
			   (separators.isEmpty() ? "" : ".addSeparators(" + asList(separators) + ")") +
			   ".build()";
	}
	
	public List<Symbol> getSeparators() {
		return separators;
	}

	@Override
	public Builder copyBuilder() {
		return builder(s);
	}

	@Override
	public String getPattern() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getPattern() + "*"; 
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Star))
			return false;
		
		Star other = (Star) obj;
		return s.equals(other.s) && separators.equals(other.separators);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}
	
	public static class Builder extends SymbolBuilder<Star> {

		private Symbol s;
		private List<Symbol> separators = new ArrayList<>();
		
		public Builder(Symbol s) {
			super(getName(s));
			this.s = s;
		}
		
		public Builder(Star star) {
			super(star);
			this.s = star.s;
		}
		
		public Builder addSeparator(Symbol symbol) {
			separators.add(symbol);
			return this;
		}
		
		public Builder addSeparators(List<Symbol> symbols) {
			separators.addAll(symbols);
			return this;
		}
		
		public Builder addSeparators(Symbol...symbols) {
			separators.addAll(Arrays.asList(symbols));
			return this;
		}
		
		@Override
		public Star build() {
			return new Star(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
