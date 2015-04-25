package org.iguana.grammar.symbol;

import java.util.Set;

import org.iguana.regex.RegularExpression;
import org.iguana.regex.automaton.Automaton;
import org.iguana.traversal.ISymbolVisitor;

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
		return new Builder(this);
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
		
		public Builder(Terminal terminal) {
			super(terminal);
			this.regex = terminal.regex;
		}
		
		@Override
		public Terminal build() {
			return new Terminal(this);
		}
	}

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
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	public String getConstructorCode() {
		return Terminal.class.getSimpleName() + ".builder(" + regex.getConstructorCode() + ")"
											  + super.getConstructorCode() 
											  + ".build()";
	}

}
