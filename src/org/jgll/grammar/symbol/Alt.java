package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.List;

import org.jgll.util.CollectionsUtil;

public class Alt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private List<Symbol> symbols;
	
	public Alt(Symbol...list) {
		this(Arrays.asList(list));
	}
	
	public Alt(List<Symbol> symbols) {
		super(CollectionsUtil.listToString(Arrays.asList(symbols), "|"));
		this.symbols = SymbolUtil.cloneList(symbols);
	}
	
	public List<Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public Alt clone() {
		Alt clone = (Alt) super.clone();
		clone.symbols = SymbolUtil.cloneList(symbols);
		return clone;
	}
	
}
