package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;

public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression plus;
	
	public static RegexPlus from(RegularExpression regexp) {
		return new Builder(regexp).build();
	}
	
	public RegexPlus(RegularExpression regex, String label, Set<Condition> conditions, Object object) {
		super(getName(regex), label, conditions, object);
		List<RegularExpression> list = new ArrayList<>();
		list.add(RegexStar.from(regex));
		list.add(regex);
		this.plus = Sequence.from(list);
	}
	
	private static String getName(RegularExpression regexp) {
		return regexp.getName() + "+";
	}
	
	@Override
	protected Automaton createAutomaton() {
		return plus.getAutomaton().setName(name);
	}
	
	@Override
	public boolean isNullable() {
		return plus.isNullable();
	}
	
	@Override
	public Set<Range> getFirstSet() {
		return plus.getFirstSet();
	}
	
	@Override
	public Set<Range> getNotFollowSet() {
		return plus.getFirstSet();
	}

	public static class Builder extends SymbolBuilder<RegexPlus> {

		private RegularExpression regex;
		
		public Builder(RegularExpression regex) {
			this.regex = regex;
		}
		
		public Builder(RegexPlus regexPlus) {
			super(regexPlus);
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
		  .append(plus.getConstructorCode(registry) + ", ")
		  .append(label + ", ")
		  .append("new HashSet<>(), ")
		  .append("null")
		  .append(")")
		  ;
		return sb.toString();
	}

}
