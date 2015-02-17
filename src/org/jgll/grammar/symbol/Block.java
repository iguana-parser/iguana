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

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("{ %s }", GeneratorUtil.listToString(symbols, " "));
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
		// FIXME
		return null;
	}

}
