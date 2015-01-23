package org.jgll.regex;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Constants;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;

public class Alt<T extends Symbol> extends AbstractSymbol implements RegularExpression, Iterable<T> {

	private static final long serialVersionUID = 1L;

	protected final List<T> symbols;
	
	private boolean allRegularExpression;

	public Alt(Builder<T> builder) {
		super(builder);
		this.symbols = builder.symbols;
		if (symbols.stream().allMatch(x -> x instanceof RegularExpression))
			allRegularExpression = true;
	}	
	
	@SafeVarargs
	public static <T extends Symbol> Alt<T> from(T...symbols) {
		return from(Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> Alt<T> from(List<T> list) {
		return new Builder<T>().add(list).build();
	}
	
	private static <T> String getName(List<T> elements) {
//		Verify.verify(elements != null, "Elements cannot be null");
//		Verify.verify(elements.size() == 0, "Elements cannot be empty.");
		return "(" + listToString(elements, " | ") + ")";
	}
	
	private Automaton automaton;

	@Override
	public Automaton getAutomaton() {
		
		if (automaton != null)
			return automaton;

		if (!allRegularExpression)
			throw new RuntimeException("Only applicable if all arguments are regular expressions");
		
		List<Automaton> automatons = new ArrayList<>();
				
		for (Symbol e : symbols) {
			RegularExpression regexp = (RegularExpression) e;
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
		
		automaton = new Automaton(startState, name); 
		
		return automaton;
	}

	@Override
	public boolean isNullable() {
		return allRegularExpression && symbols.stream().anyMatch(e -> ((RegularExpression)e).isNullable()); 
	}
	
	@Override
	public Iterator<T> iterator() {
		return symbols.iterator();
	}
	
	public int size() {
		return symbols.size();
	}
	
	public T get(int index) {
		return symbols.get(index);
	}
	
	@Override
	public boolean equals(Object obj) {
	
		if(obj == this)
			return true;
		
		if(!(obj instanceof Alt))
			return false;
		
		Alt<?> other = (Alt<?>) obj;
		
		return other.symbols.equals(symbols);
	}
	
	@Override
	public int hashCode() {
		return symbols.hashCode();
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable if all arguments are regular expressions");
		
		return symbols.stream().flatMap(x -> ((RegularExpression)x).getFirstSet().stream()).collect(Collectors.toSet());
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public String getConstructorCode() {
		return Alt.class.getSimpleName() + ".builder(" + asArray(symbols) + ")" + super.getConstructorCode() + ".build()";
	}
	
	@Override
	public Builder<T> copyBuilder() {
		return new Builder<T>().add(symbols);
	}
	
	@Override
	public String getPattern() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable if all arguments are regular expressions");
		
		return "(" +  symbols.stream().map(s -> ((RegularExpression)s).getPattern()).collect(Collectors.joining("|")) + ")";
	}

	public List<T> getSymbols() {
		return symbols;
	}

	public static Alt<CharacterRange> not(Character...chars) {
		List<CharacterRange> ranges = Arrays.stream(chars).map(c -> CharacterRange.in(c.getValue(), c.getValue())).collect(Collectors.toList());
		return not(ranges);
	}
	
	public static Alt<CharacterRange> not(CharacterRange...ranges) {
		return not(Arrays.asList(ranges));
	}
	
	public static Alt<CharacterRange> not(Alt<CharacterRange> alt) {
		return not(alt.symbols);
	}
	
	public static Alt<CharacterRange> not(List<CharacterRange> ranges) {
		List<CharacterRange> newRanges = new ArrayList<>();
		
		int i = 0;
		
		Collections.sort(ranges);
		
		if(ranges.get(i).getStart() >= 1) {
			newRanges.add(CharacterRange.in(1, ranges.get(i).getStart() - 1));
		}
		
		for(; i < ranges.size() - 1; i++) {
			CharacterRange r1 = ranges.get(i);
			CharacterRange r2 = ranges.get(i + i);
			
			if(r2.getStart() > r1.getEnd() + 1) {
				newRanges.add(CharacterRange.in(r1.getEnd() + 1, r2.getStart() - 1));
			}
		}
		
		if(ranges.get(i).getEnd() < Constants.MAX_UTF32_VAL) {
			newRanges.add(CharacterRange.in(ranges.get(i).getEnd() + 1, Constants.MAX_UTF32_VAL));
		}
		
		return builder(newRanges).build();
	}
	
	public static <T extends Symbol> Builder<T> builder() {
		return new Builder<>();
	}
	
	public static <T extends Symbol> Builder<T> builder(List<T> symbols) {
		return new Builder<T>().add(symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Builder<T> builder(T...symbols) {
		return new Builder<T>().add(Arrays.asList(symbols));
	}
	
	public static class Builder<T extends Symbol> extends SymbolBuilder<Alt<T>> {

		List<T> symbols = new ArrayList<>();
		
		public Builder<T> add(T symbol) {
			symbols.add(symbol);
			return this;
		}
				
		public Builder<T> add(List<T> l) {
			symbols.addAll(l);
			return this;
		}

		@Override
		public Alt<T> build() {
//			Verify.verify(elements.size() > 2, "The number of elements in an alternative should be at least two");
			this.name = getName(symbols);
			return new Alt<>(this);
		}
	}
	
}
