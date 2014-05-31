package org.jgll.grammar.symbol;

import java.util.Set;

import org.jgll.grammar.condition.Condition;


public class Plus extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	public static Plus from(Symbol s) {
		return new Builder(s).build();
	}
	
	public Plus(Symbol s, Set<Condition> conditions, String label, Object object) {
		super(s.getName() + "+", conditions, label, object);
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	public static class Builder extends SymbolBuilder<Plus> {

		private Symbol s;
		
		public Builder(Symbol s) {
			this.s = s;
		}
		
		public Builder(Plus plus) {
			super(plus);
			this.s = plus.s;
		}
		
		@Override
		public Plus build() {
			return new Plus(s, conditions, label, object);
		}
	}
	
	@Override
	public SymbolBuilder<Plus> builder() {
		return new Builder(this);
	}
	
}
