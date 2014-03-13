package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgll.util.CollectionsUtil;

public class Group extends Nonterminal {

	private static final long serialVersionUID = 1L;

	private List<? extends Symbol> symbols;

	public Group(List<? extends Symbol> symbols) {
		super("(" + CollectionsUtil.listToString(symbols) + ")");
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
	public Symbol copy() {
		List<Symbol> list = new ArrayList<>();
		for(Symbol s : symbols) {
			list.add(s.copy());
		}
		return new Group(list);
	}
	
}
