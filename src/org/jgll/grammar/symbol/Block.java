package org.jgll.grammar.symbol;

import org.jgll.traversal.ISymbolVisitor;
import org.jgll.util.generator.GeneratorUtil;

public class Block extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol[] symbols;

	Block(Builder builder) {
		super(builder);
		this.symbols = builder.symbols;
	}
	
	public static Block block(Symbol... symbols) {
		return builder(symbols).build();
	}
	
	public Symbol[] getSymbols() {
		return symbols;
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		int size = 0;
		for (Symbol symbol: symbols)
			size = size + symbol.size();
		return size;
	}
	
	@Override
	public String toString() {
		return String.format("{ %s }", GeneratorUtil.listToString(symbols, " "));
	}
	
	@Override
	public String toString(int j) {
		String[] strings = new String[symbols.length];
		
		int k = 0;
		for (Symbol symbol : symbols) {
			strings[k] = j <= 1? symbol.toString(j) : symbol.toString();
			j = j - symbol.size();
			k++;
		}
		
		return String.format("{ %s }", GeneratorUtil.listToString(strings, " "));
	}
	
	public static Builder builder(Symbol... symbols) {
		return new Builder(symbols);
	}
	
	public static class Builder extends SymbolBuilder<Block> {
		
		private final Symbol[] symbols;

		public Builder(Block block) {
			super(block);
			this.symbols = block.symbols; 
		}
		
		public Builder(Symbol... symbols) {
			super(String.format("{ %s }", GeneratorUtil.listToString(symbols, " ")));
			
			assert symbols.length != 0;
			
			this.symbols = symbols;
		}

		@Override
		public Block build() {
			return new Block(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
