package org.jgll.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;

import com.google.common.collect.ImmutableList;

public class Plus extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private final List<Symbol> separators;
	
	private final boolean isRegularExpression;
	
	public static Plus from(Symbol s) {
		return builder(s).build();
	}
	
	private Plus(Builder builder) {
		super(builder);
		this.s = builder.s;
		this.separators = ImmutableList.copyOf(builder.separators);
		this.isRegularExpression = s instanceof RegularExpression;
	}
	
	private static String getName(Symbol s) {
//		Verify.verifyNotNull(s);
		return s.getName() + "+";
	}
	
	@Override
	public Automaton getAutomaton() {
		throw new RuntimeException("Not completed.");
	}
	
	@Override
	public boolean isNullable() {
		if (!isRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return ((RegularExpression) s).isNullable();
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
	
	public List<Symbol> getSeparators() {
		return separators;
	}
	
	@Override
	public String getConstructorCode() {
		return Plus.class.getSimpleName() + ".builder(" + s.getConstructorCode() + ")" + super.getConstructorCode() + ".build()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return builder(s);
	}

	@Override
	public String getPattern() {
		if (!isRegularExpression)
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
			this.s = s;
			this.name = getName(s);
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
