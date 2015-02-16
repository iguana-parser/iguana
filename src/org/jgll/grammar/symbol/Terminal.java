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
	
	@Override
	public int hashCode() {
		return regex.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == this)
			return true;
		
		if (!(obj instanceof Terminal))
			return false;
		
		Terminal other = (Terminal) obj;
		
		return regex.equals(other.regex);
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
	
	@Override
	public String getConstructorCode() {
		return Terminal.class.getSimpleName() + ".builder(" + regex.getConstructorCode() + ")"
											  + super.getConstructorCode() 
											  + ".build()";
	}

}
