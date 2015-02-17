package org.jgll.grammar.symbol;

import org.jgll.traversal.ISymbolVisitor;

public class Offside extends AbstractSymbol {
	
	private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;

	Offside(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
	}
	
	public static Offside offside(Symbol symbol) {
		return builder(symbol).build();
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("offside %s", symbol.toString());
	}
	
	public static Builder builder(Symbol symbol) {
		return new Builder(symbol);
	}
	
	public static class Builder extends SymbolBuilder<Offside> {
		
		private final Symbol symbol;

		public Builder(Offside offside) {
			super(offside);
			this.symbol = offside.symbol;
		}
		
		public Builder(Symbol symbol) {
			super(String.format("offside %s", symbol.toString()));
			this.symbol = symbol;
		}

		@Override
		public Offside build() {
			return new Offside(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		// FIXME:
		return null;
	}

}
