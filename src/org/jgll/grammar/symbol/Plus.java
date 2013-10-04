package org.jgll.grammar.symbol;

import org.jgll.grammar.condition.Condition;

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
	public Plus addCondition(Condition condition) {
		Plus plus = new Plus(this.s);
		plus.conditions.addAll(this.conditions);
		plus.conditions.add(condition);
		return plus;
	}

}
