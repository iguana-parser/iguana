package org.jgll.grammar.symbol;

import java.util.Set;

import org.jgll.grammar.condition.Condition;


public class Star extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	public Star(Symbol s, Set<Condition> conditions, String label, Object object) {
		super(s.getName() + "*", conditions, label, object);
		this.s = s;
	}
	
	public Star from(Symbol s) {
		return new Builder(s).build();
	}
	
	public Symbol getSymbol() {
		return s;
	}

	@Override
	public SymbolBuilder<? extends Symbol> builder() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class Builder extends SymbolBuilder<Star> {

		private Symbol s;

		public Builder(Symbol s) {
			this.s = s;
		}
		
		public Builder(Star star) {
			super(star);
			this.s = star.s;
		}
		
		@Override
		public Star build() {
			return new Star(s, conditions, label, object);
		}
		
	}

}
