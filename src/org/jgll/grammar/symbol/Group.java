package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.List;

import org.jgll.util.CollectionsUtil;

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
	
}
