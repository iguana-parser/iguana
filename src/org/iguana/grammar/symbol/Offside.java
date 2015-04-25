package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

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
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public String getConstructorCode() {
		return "Offside.builder(" + symbol.getConstructorCode() + ")" 
								  + super.getConstructorCode()
								  + ".build()";
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		return symbol.size();
	}
	
	@Override
	public String toString() {
		return String.format("offside %s", symbol.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format("offside %s", symbol.toString(j));
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
		return visitor.visit(this);
	}

}
