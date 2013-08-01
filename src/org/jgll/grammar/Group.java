package org.jgll.grammar;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.collections.CollectionsUtil;

public class Group extends Nonterminal {

	private static final long serialVersionUID = 1L;

	private List<? extends Symbol> symbols;

	public Group(List<? extends Symbol> symbols) {
		super("(" + CollectionsUtil.listToString(symbols) + ")", false);
		this.symbols = symbols;
	}
	
	@SafeVarargs
	public static <T extends Symbol> Group of(T...symbols) {
		return new Group(Arrays.asList(symbols));
	}

	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public Group addCondition(Condition condition) {
		Group group = new Group(this.symbols);
		group.conditions.addAll(this.conditions);
		group.conditions.add(condition);
		return group;
	}
	
}
