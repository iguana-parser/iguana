package org.jgll.grammar.symbol;

import org.jgll.grammar.condition.Condition;

public class Star extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Star(Symbol s) {
		super(s.getName() + "+");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public Star addCondition(Condition condition) {
		Star star = new Star(this.s);
		star.conditions.addAll(this.conditions);
		star.conditions.add(condition);
		return star;
	}

}
