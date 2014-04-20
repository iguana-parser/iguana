package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Alt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> symbols;
	
	public Alt(Symbol...list) {
		this(Arrays.asList(list));
	}
	
	public Alt(List<Symbol> symbols, Set<Condition> conditions) {
		super(CollectionsUtil.listToString(Arrays.asList(symbols), "|"), conditions);
		this.symbols = new ArrayList<>(symbols);
	}
	
	public Alt(List<Symbol> symbols) {
		this(symbols, Collections.<Condition>emptySet());
	}
	
	public List<Symbol> getSymbols() {
		return symbols;
	}

	@Override
	public Alt withConditions(Set<Condition> conditions) {
		return new Alt(symbols, CollectionsUtil.union(conditions, this.conditions));
	}
	
}
