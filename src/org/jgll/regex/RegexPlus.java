package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.automaton.Automaton;

public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	private final RegularExpression plus;
	
	public static RegexPlus from(RegularExpression regexp) {
		return new Builder(regexp).build();
	}
	
	public RegexPlus(RegularExpression regex, String label, Set<Condition> conditions, Object object) {
		super(getName(regex), label, conditions, object);
		this.regex = regex;
		List<RegularExpression> list = new ArrayList<>();
		list.add(RegexStar.from(regex.withoutConditions()));
		list.add(regex.withoutConditions());
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

	@Override
	public RegexPlus withConditions(Set<Condition> conditions) {
		return new Builder(regex).addConditions(this.conditions).addConditions(this.conditions).build();
	}

	@Override
	public RegexPlus withoutConditions() {
		return RegexPlus.from(regex);
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

}
