package org.jgll.grammar.symbol;

import org.jgll.datadependent.ast.Expression;
import org.jgll.traversal.ISymbolVisitor;

public class While extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Expression expression;
	private final Symbol body;

	While(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.body = builder.body;
	}
	
	public static While whileLoop(Expression expression, Symbol body) {
		return builder(expression, body).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Symbol getBody() {
		return body; 
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("while (%s) %s", expression.toString(), body.toString());
	}
	
	public static Builder builder(Expression expression, Symbol body) {
		return new Builder(expression, body);
	}
	
	public static class Builder extends SymbolBuilder<While> {
		
		private final Expression expression;
		private final Symbol body;

		public Builder(While whileSymbol) {
			super(whileSymbol);
			this.expression = whileSymbol.expression;
			this.body = whileSymbol.body;
		}
		
		public Builder(Expression expression, Symbol body) {
			super(String.format("while (%s) %s", expression.toString(), body.toString()));
			this.expression = expression;
			this.body = body;
		}

		@Override
		public While build() {
			return new While(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
