package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

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
	public Opt addConditions(Collection<Condition> conditions) {
		Opt opt = new Opt(this.s);
		opt.conditions.addAll(this.conditions);
		opt.conditions.addAll(conditions);
		return opt;
	}
	
	@Override
	public Opt addCondition(Condition condition) {
		return addConditions(CollectionsUtil.list(condition));
	}

}
