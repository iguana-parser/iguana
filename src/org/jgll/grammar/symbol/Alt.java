package org.jgll.grammar.symbol;

import java.util.Arrays;
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
	
	
	@Override
	public Alt addCondition(Condition condition) {
		Alt star = new Alt(this.list);
		star.conditions.addAll(this.conditions);
		star.conditions.add(condition);
		return star;
	}

}
