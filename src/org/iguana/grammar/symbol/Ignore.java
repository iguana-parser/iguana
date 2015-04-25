package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

public class Ignore extends AbstractSymbol {
	
private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;

	Ignore(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
	}
	
	public static Ignore ignore(Symbol symbol) {
		return builder(symbol).build();
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public String getConstructorCode() {
		return "Ignore.builder(" + symbol.getConstructorCode() + ")" 
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
		return String.format("ignore %s", symbol.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format("ignore %s", symbol.toString(j));
	}
	
	public static Builder builder(Symbol symbol) {
		return new Builder(symbol);
	}
	
	public static class Builder extends SymbolBuilder<Ignore> {
		
		private final Symbol symbol;

		public Builder(Ignore ignore) {
			super(ignore);
			this.symbol = ignore.symbol;
		}
		
		public Builder(Symbol symbol) {
			super(String.format("ignore %s", symbol.toString()));
			this.symbol = symbol;
		}

		@Override
		public Ignore build() {
			return new Ignore(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
