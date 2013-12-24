package org.jgll.regex;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Star extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Star(Symbol s) {
		super(s.getName() + "*");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public Star addConditions(Collection<Condition> conditions) {
		Star star = new Star(this.s);
		star.conditions.addAll(this.conditions);
		star.conditions.addAll(conditions);
		return star;
	}
	
	@Override
	public Star addCondition(Condition condition) {
		return addConditions(CollectionsUtil.list(condition));
	}

}
