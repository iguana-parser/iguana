package org.jgll.grammar.symbol;

import java.util.Set;

import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;

public class Terminal extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	public static Terminal from(RegularExpression regex) {
		return builder(regex).build();
	}
	
	public Terminal(Builder builder) {
		super(builder);
		this.regex = builder.regex;
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(regex);
	}
	
	public RegularExpression getRegularExpression() {
		return regex;
	}
	
	public static Builder builder(RegularExpression regex) {
		return new Builder(regex);
	}
	
	public static class Builder extends SymbolBuilder<Terminal> {
		
		private RegularExpression regex;

		public Builder(RegularExpression regex) {
			super(regex.getName());
			this.regex = regex;
		}
		
		@Override
		public Terminal build() {
			return new Terminal(this);
		}
	}

	@Override
	public boolean isNullable() {
		return regex.isNullable();
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		return regex.getFirstSet();
	}

	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return regex.getNotFollowSet();
	}

	@Override
	public String getPattern() {
		return regex.getPattern();
	}
	
	@Override
	protected Automaton createAutomaton() {
		return regex.getAutomaton();
	}

}
