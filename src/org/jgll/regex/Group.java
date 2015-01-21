package org.jgll.regex;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;

public class Group<T extends Symbol> extends AbstractSymbol implements RegularExpression, Iterable<T> {

	private static final long serialVersionUID = 1L;

	private final List<T> symbols;
	
	private boolean allRegularExpression;

	private Group(Builder<T> builder) {
		super(builder);
		this.symbols = builder.symbols;
	}
	
	public static <T extends Symbol> Group<T> from(List<T> symbols) {
		return new Builder<T>().add(symbols).build();
	}
	
	@SafeVarargs
	public static <T extends Symbol> Group<T> from(T...elements) {
		return from(Arrays.asList(elements));
	}
	
	private static <T> String getName(List<T> elements) {
//		Verify.verify(elements != null, "Elements cannot be null");
//		Verify.verify(elements.size() == 0, "Elements cannot be empty.");
		return "(" + listToString(elements, " ") + ")";
	}
		
	@Override
	public Automaton getAutomaton() {
		
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable if all arguments are regular expressions");
		
		List<Automaton> automatons = new ArrayList<>();
		
		for(int i = 0; i < symbols.size(); i++) {
			automatons.add(((RegularExpression) symbols.get(i)).getAutomaton().copy());
		}
				
		Automaton result = automatons.get(0);
		State startState = result.getStartState();
		
		for (int i = 1; i < automatons.size(); i++) {
			Automaton next = automatons.get(i);
			
			for(State s : result.getFinalStates()) {
				s.setStateType(StateType.NORMAL);
				s.addTransition(Transition.epsilonTransition(next.getStartState()));
			}
			
			result = new Automaton(startState, this.name);
		}
		
		return result;
	}
	
	@Override
	public boolean isNullable() {
		return allRegularExpression && symbols.stream().allMatch(e -> ((RegularExpression)e).isNullable()); 
	}
	
	public int size() {
		return symbols.size();
	}
	
	public Symbol get(int index) {
		return symbols.get(index);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof Group))
			return false;
		
		Group<?> other = (Group<?>) obj;
		
		return symbols.equals(other.symbols);
	}
	
	@Override
	public int hashCode() {
		return symbols.hashCode();
	}

	@Override
	public Iterator<T> iterator() {
		return symbols.iterator();
	}
	
	public Stream<T> stream() {
		return StreamSupport.stream(symbols.spliterator(), false);
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable if all arguments are regular expressions");
		
		Set<CharacterRange> firstSet = new HashSet<>();
		for(Symbol e : symbols) {
			RegularExpression regex = (RegularExpression) e;
			firstSet.addAll(regex.getFirstSet());
			if(!regex.isNullable()) {
				break;
			}
		}
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}
		
	@Override
	public String getConstructorCode() {
		return Group.class.getSimpleName() + ".builder(" + getConstructorCode(symbols) + ")" + super.getConstructorCode() + ".build()";
	}
	
	@Override
	public Builder<T> copyBuilder() {
		return new Builder<T>().add(symbols);
	}

	@Override
	public Pattern getPattern() {
		throw new UnsupportedOperationException();
	}
	
	public List<T> getSymbols() {
		return symbols;
	}
	
	public static <T extends Symbol> Builder<T> builder() {
		return new Builder<>();
	}
	
	public static <T extends Symbol> Builder<T> builder(List<T> symbols) {
		return new Builder<T>().add(symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Builder<T> builder(T...symbols) {
		return builder(Arrays.asList(symbols));
	}
	
	public static class Builder<T extends Symbol> extends SymbolBuilder<Group<T>> {

		private List<T> symbols = new ArrayList<>();
		
		public Builder<T> add(T s) {
			symbols.add(s);
			return this;
		}
		
		public Builder<T> add(List<T> symbols) {
			this.symbols.addAll(symbols);
			return this;
		}
		
		@Override
		public Group<T> build() {
			this.name = getName(symbols);
			return new Group<>(this);
		}
	}
}
