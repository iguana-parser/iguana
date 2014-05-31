package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Alt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> symbols;
	
	public static Alt from(Symbol...symbols) {
		return new Builder(Arrays.asList(symbols)).build();
	}
	
	public static Alt from(List<Symbol> symbols) {
		return new Builder(symbols).build();
	}
	
	public Alt(List<Symbol> symbols, Set<Condition> conditions, String label, Object object) {
		super("[" + CollectionsUtil.listToString(symbols, "|") + "]", conditions, label, object);
		this.symbols = new ArrayList<>(symbols);
	}
	
	public List<Symbol> getSymbols() {
		return symbols;
	}
	
	public static class Builder extends SymbolBuilder<Alt> {

		private List<Symbol> symbols;
		
		public Builder(List<Symbol> symbols) {
			this.symbols = symbols;
		}
		
		public Builder(Alt alt) {
			super(alt);
			this.symbols = alt.symbols;
		}
		
		@Override
		public Alt build() {
			return new Alt(symbols, conditions, label, symbols);
		}
	}

	@Override
	public SymbolBuilder<? extends Symbol> builder() {
		return new Builder(this);
	}

}
