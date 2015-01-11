package org.jgll.regex;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

public class RegexAlt<T extends RegularExpression> extends AbstractRegularExpression implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	private final List<T> regularExpressions;

	public RegexAlt(Builder<T> builder) {
		super(builder);
		
		if (builder.regularExpressions == null) 
			throw new IllegalArgumentException("The list of regular expressions cannot be null.");
		
		if (builder.regularExpressions.size() == 0) 
			throw new IllegalArgumentException("The list of regular expressions cannot be empty.");

		this.regularExpressions = new ArrayList<>(builder.regularExpressions);
	}
	
	@SafeVarargs
	public static <T extends RegularExpression> RegexAlt<T> from(T...regularExpressions) {
		return from(Arrays.asList(regularExpressions));
	}
	
	public static <T extends RegularExpression> RegexAlt<T> from(List<T> regularExpressions) {
		return new Builder<>(regularExpressions).build();
	}

	
	private static <T> String getName(List<T> regularExpressions) {
		return "(" + listToString(regularExpressions, " | ") + ")";
	}
	
	public List<T> getRegularExpressions() {
		return regularExpressions;
	}

	@Override
	protected Automaton createAutomaton() {

		List<Automaton> automatons = new ArrayList<>();
		for (RegularExpression regexp : regularExpressions) {
			automatons.add(regexp.getAutomaton().copy());
		}
		
		State startState = new State();
		State finalState = new State(StateType.FINAL);
		
		for (Automaton automaton : automatons) {
			startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
			
			Set<State> finalStates = automaton.getFinalStates();
			for (State s : finalStates) {
				s.setStateType(StateType.NORMAL);
				s.addTransition(Transition.epsilonTransition(finalState));				
			}
		}
		
		return new Automaton(startState, name);
	}

	@Override
	public boolean isNullable() {
		for (RegularExpression regex : regularExpressions) {
			if (regex.isNullable()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Iterator<T> iterator() {
		return regularExpressions.iterator();
	}
	
	public int size() {
		return regularExpressions.size();
	}
	
	public T get(int index) {
		return regularExpressions.get(index);
	}
	
	@Override
	public boolean equals(Object obj) {
	
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof RegexAlt)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		RegexAlt other = (RegexAlt) obj;
		
		return other.regularExpressions.equals(regularExpressions);
	}
	
	@Override
	public int hashCode() {
		return regularExpressions.hashCode();
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		Set<CharacterRange> firstSet = new HashSet<>();
		for (T t : regularExpressions) {
			firstSet.addAll(t.getFirstSet());
		}
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	@SafeVarargs
	public static <T extends RegularExpression> Builder<T> builder(T...regularExpressions) {
		return builder(Arrays.asList(regularExpressions));
	}
	
	public static <T extends RegularExpression> Builder<T> builder(List<T> regularExpressions) {
		return new Builder<>(regularExpressions);
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder<>(this);
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		StringBuilder sb = new StringBuilder();
		
		for (RegularExpression regex : regularExpressions) {
			 sb.append(regex.getConstructorCode(registry) + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		
		return "new RegexAlt(list(" + sb.toString() + "), \"" + escape(label) + "\", new HashSet<Condition>(), null)";
	}

	public static class Builder<T extends RegularExpression> extends SymbolBuilder<RegexAlt<T>> {
		
		private List<T> regularExpressions;

		@SafeVarargs
		public Builder(T...regularExpressions) {
			this(Arrays.asList(regularExpressions));
		}
		
		public Builder(List<T> regularExpressions) {
			super(getName(regularExpressions));
			this.regularExpressions = regularExpressions;
		}
		
		public Builder(RegexAlt<T> regexAlt) {
			super(regexAlt);
			this.regularExpressions = regexAlt.regularExpressions;
		}

		@Override
		public RegexAlt<T> build() {
			return new RegexAlt<>(this);
		}
	}

}
