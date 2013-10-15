package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Alt extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private List<Symbol> list;
	
	public Alt(Symbol...list) {
		super(CollectionsUtil.listToString(Arrays.asList(list), "|"));
		this.list = Arrays.asList(list);
	}
	
	public Alt(List<Symbol> list) {
		super(CollectionsUtil.listToString(Arrays.asList(list)));
		this.list = list;
	}
	
	public List<Symbol> getSymbols() {
		return list;
	}
	
	@Override
	public Alt addConditions(Collection<Condition> conditions) {
		Alt alt = new Alt(this.list);
		alt.conditions.addAll(this.conditions);
		alt.conditions.addAll(conditions);
		return alt;
	}
	
	@Override
	public Alt addCondition(Condition condition) {
		return addConditions(CollectionsUtil.list(condition));
	}
	
}
