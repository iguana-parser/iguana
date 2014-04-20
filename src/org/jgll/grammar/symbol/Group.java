package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Group extends Nonterminal {

	private static final long serialVersionUID = 1L;

	private List<? extends Symbol> symbols;

	public Group(List<? extends Symbol> symbols, Set<Condition> conditions) {
		super("(" + CollectionsUtil.listToString(symbols) + ")", conditions);
		this.symbols = new ArrayList<>(symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Group of(T...symbols) {
		return new Group(Arrays.asList(symbols), Collections.<Condition>emptySet());
	}

	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public Nonterminal withConditions(Set<Condition> conditions) {
		conditions.addAll(this.conditions);
		return new Group(symbols, conditions);
	}
	
}
