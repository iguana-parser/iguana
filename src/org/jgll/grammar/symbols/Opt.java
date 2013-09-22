package org.jgll.grammar.symbols;

import org.jgll.grammar.condition.Condition;

public class Opt extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Opt(Symbol s) {
		super(s.getName() + "?");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public Opt addCondition(Condition condition) {
		Opt opt = new Opt(this.s);
		opt.conditions.addAll(this.conditions);
		opt.conditions.add(condition);
		return opt;
	}

}
