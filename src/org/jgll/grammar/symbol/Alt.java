package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.List;

import org.jgll.util.CollectionsUtil;

public class Alt extends AbstractSymbol {

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
	public Alt copy() {
		return null;
	}
	
}
