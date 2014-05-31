package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

public class Group extends Nonterminal {

	private static final long serialVersionUID = 1L;

	private final List<? extends Symbol> symbols;

	public Group(List<? extends Symbol> symbols, Set<Condition> conditions, String label, Object object) {
		super("(" + CollectionsUtil.listToString(symbols) + ")", 0, false, conditions, label, object);
		this.symbols = new ArrayList<>(symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Group of(T...symbols) {
		return new Builder(Arrays.asList(symbols)).build();
	}
	
	public static <T extends Symbol> Group of(List<Symbol> symbols) {
		return new Builder(symbols).build();
	}

	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	public static class Builder extends SymbolBuilder<Group> {
		
		private List<? extends Symbol> symbols;
		
		public Builder(List<? extends Symbol> symbols) {
			this.symbols = symbols;
		}
		
		public Builder(Group group) {
			super(group);
			this.symbols = group.symbols;
		}

		@Override
		public Group build() {
			return new Group(symbols, conditions, label, object);
		}
	}
	
	@Override
	public SymbolBuilder<Group> builder() {
		return new Builder(this);
	}
	
}
