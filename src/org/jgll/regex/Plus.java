package org.jgll.regex;

import java.util.Set;
import java.util.regex.Pattern;

import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;

public class Plus extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private final boolean isRegularExpression;
	
	public static Plus from(Symbol s) {
		return builder(s).build();
	}
	
	private Plus(Builder builder) {
		super(builder);
		this.s = builder.s;
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
	
	@Override
	public String getConstructorCode() {
		return Plus.class.getName() + ".builder(" + s.getConstructorCode() + ")" + super.getConstructorCode() + ".build()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return builder(s);
	}

	@Override
	public Pattern getPattern() {
		throw new UnsupportedOperationException();
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}

	public static class Builder extends SymbolBuilder<Plus> {

		private Symbol s;

		public Builder(Symbol s) {
			this.s = s;
			this.name = getName(s);
		}
		
		@Override
		public Plus build() {
			return new Plus(this);
		}
	}
	
}
