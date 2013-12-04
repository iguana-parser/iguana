package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Plus extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Plus(Symbol s) {
		super(s.getName() + "+");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public Plus addConditions(Collection<Condition> conditions) {
		Plus plus = new Plus(this.s);
		plus.conditions.addAll(this.conditions);
		plus.conditions.addAll(conditions);
		return plus;
	}
	
	@Override
	public Plus addCondition(Condition condition) {
		return addConditions(CollectionsUtil.list(condition));
	}

}
