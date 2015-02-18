package org.jgll.grammar.symbol;

import org.jgll.traversal.ISymbolVisitor;

public class Align extends AbstractSymbol {
	
	private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;

	Align(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
	}
	
	public static Align align(Symbol symbol) {
		return builder(symbol).build();
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("align %s", symbol.toString());
	}
	
	public static Builder builder(Symbol symbol) {
		return new Builder(symbol);
	}
	
	public static class Builder extends SymbolBuilder<Align> {
		
		private final Symbol symbol;

		public Builder(Align align) {
			super(align);
			this.symbol = align.symbol;
		}
		
		public Builder(Symbol symbol) {
			super(String.format("align %s", symbol.toString()));
			this.symbol = symbol;
		}

		@Override
		public Align build() {
			return new Align(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
