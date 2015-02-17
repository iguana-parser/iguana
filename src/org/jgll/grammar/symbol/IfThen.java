package org.jgll.grammar.symbol;

import org.jgll.datadependent.ast.Expression;
import org.jgll.traversal.ISymbolVisitor;

public class IfThen extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Expression expression;
	private final Symbol symbol;

	IfThen(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.symbol = builder.symbol;
	}
	
	public static IfThen ifThen(Expression expression, Symbol symbol) {
		return builder(expression, symbol).build();
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("if (%s) %s", expression.toString(), symbol.toString());
	}
	
	public static Builder builder(Expression expression, Symbol symbol) {
		return new Builder(expression, symbol);
	}
	
	public static class Builder extends SymbolBuilder<IfThen> {
		
		private final Expression expression;
		private final Symbol symbol;

		public Builder(IfThen ifThen) {
			super(ifThen);
			this.expression = ifThen.expression;
			this.symbol = ifThen.symbol;
		}
		
		public Builder(Expression expression, Symbol symbol) {
			super(String.format("if (%s) %s", expression.toString(), symbol.toString()));
			this.expression = expression;
			this.symbol = symbol;
		}

		@Override
		public IfThen build() {
			return new IfThen(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		// FIXME:
		return null;
	}

}
