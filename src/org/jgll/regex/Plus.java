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

import com.google.common.collect.ImmutableList;

public class Plus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private final List<Symbol> separators;
	
	private final boolean allRegularExpression;
	
	public static Plus from(Symbol s) {
		return builder(s).build();
	}
	
	private Plus(Builder builder) {
		super(builder);
		this.s = builder.s;
		this.separators = ImmutableList.copyOf(builder.separators);
		this.allRegularExpression = s instanceof RegularExpression;
	}
	
	private static String getName(Symbol s) {
//		Verify.verifyNotNull(s);
		return s.getName() + "+";
	}
	
	@Override
	protected Automaton createAutomaton() {
		
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return Sequence.from(s, Star.from(s)).getAutomaton();		
	}
	
	@Override
	public boolean isNullable() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return ((RegularExpression) s).isNullable();
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
	
	public List<Symbol> getSeparators() {
		return separators;
	}
	
	public boolean isAllRegularExpression() {
		return allRegularExpression;
	}
	
	@Override
	public String getConstructorCode() {
		return Plus.class.getSimpleName() + 
			   ".builder(" + s.getConstructorCode() + ")" + 
			   super.getConstructorCode() + 
			   (separators.isEmpty() ? "" : ".addSeparators(" + asList(separators) + ")") +
			   ".build()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return builder(s);
	}

	@Override
	public String getPattern() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");
		
		return ((RegularExpression) s).getPattern() + "+";
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}

	public static class Builder extends SymbolBuilder<Plus> {

		private Symbol s;
		
		private final List<Symbol> separators = new ArrayList<>();

		public Builder(Symbol s) {
			super(getName(s));
			this.s = s;
		}
		
		public Builder(Plus plus) {
			super(plus);
			this.s = plus.s;
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
		public Plus build() {
			return new Plus(this);
		}
	}
	
}
