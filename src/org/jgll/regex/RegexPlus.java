package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;

public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	public static RegexPlus from(RegularExpression regex) {
		return new Builder(regex).build();
	}
	
	public RegexPlus(RegularExpression regex, String label, Set<Condition> conditions, Object object) {
		super(getName(regex), label, conditions, object);
		this.regex = regex;
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
	public Set<Range> getFirstSet() {
		return regex.getFirstSet();
	}
	
	@Override
	public Set<Range> getNotFollowSet() {
		return regex.getFirstSet();
	}

	public static class Builder extends SymbolBuilder<RegexPlus> {

		private RegularExpression regex;
		
		public Builder(RegularExpression regex) {
			this.regex = regex;
		}
		
		@Override
		public RegexPlus build() {
			return new RegexPlus(regex, label, conditions, regex);
		}
	}

	@Override
	public SymbolBuilder<RegexPlus> builder() {
		return new Builder(this);
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
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
