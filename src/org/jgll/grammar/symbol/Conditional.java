package org.jgll.grammar.symbol;

import org.jgll.datadependent.ast.Expression;
import org.jgll.traversal.ISymbolVisitor;

public class Conditional extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;
	private final Expression expression;

	Conditional(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
		this.expression = builder.expression;
	}
	
	public static Conditional when(Symbol symbol, Expression expression) {
		return builder(symbol, expression).build();
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s when %s;", symbol.toString(), expression.toString());
	}
	
	public static Builder builder(Symbol symbol, Expression expression) {
		return new Builder(symbol, expression);
	}
	
	public static class Builder extends SymbolBuilder<Conditional> {
		
		private final Symbol symbol;
		private final Expression expression;

		public Builder(Conditional conditional) {
			super(conditional);
			this.symbol = conditional.symbol;
			this.expression = conditional.expression;
		}
		
		public Builder(Symbol symbol, Expression expression) {
			super(String.format("%s when %s;", symbol.toString(), expression.toString()));
			this.symbol = symbol;
			this.expression = expression;
		}

		@Override
		public Conditional build() {
			return new Conditional(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
