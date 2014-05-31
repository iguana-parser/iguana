package org.jgll.grammar.symbol;

import java.util.Set;

import org.jgll.grammar.condition.Condition;

public class Opt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	public static Opt from(Symbol s) {
		return new Builder(s).build();
	}
	
	public Opt(Symbol s, Set<Condition> conditions, String label, Object object) {
		super(s.getName() + "?", conditions, label, object);
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	public static class Builder extends SymbolBuilder<Opt> {

		private Symbol s;

		public Builder(Symbol s) {
			this.s = s;
		}
		
		public Builder(Opt opt) {
			super(opt);
			this.s = opt.s;
		}
		
		@Override
		public Opt build() {
			return new Opt(s, conditions, label, object);
		}
	}

	@Override
	public SymbolBuilder<Opt> builder() {
		return null;
	}
}
