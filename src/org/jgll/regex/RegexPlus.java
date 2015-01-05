package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;

public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	public static RegexPlus from(RegularExpression regex) {
		return new Builder(regex).build();
	}
	
	private RegexPlus(Builder builder) {
		super(builder);
		this.regex = builder.regex;
	}
	
	private static String getName(RegularExpression regexp) {
		return regexp.getName() + "+";
	}
	
	@Override
	protected Automaton createAutomaton() {
		return regex.getAutomaton().setName(name);
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
		return regex.getFirstSet();
	}

	public static class Builder extends SymbolBuilder<RegexPlus> {

		private RegularExpression regex;
		
		public Builder(RegularExpression regex) {
			super(getName(regex));
			this.regex = regex;
		}
		
		@Override
		public RegexPlus build() {
			return new RegexPlus(this);
		}
	}

	public static Builder builder(RegularExpression regex) {
		return new Builder(regex);
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new RegexPlus(")
		  .append(regex.getConstructorCode(registry) + ", ")
		  .append(label + ", ")
		  .append("new HashSet<>(), ")
		  .append("null")
		  .append(")")
		  ;
		return sb.toString();
	}

}
