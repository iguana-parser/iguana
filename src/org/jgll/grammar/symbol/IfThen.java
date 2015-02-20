package org.jgll.grammar.symbol;

import org.jgll.datadependent.ast.Expression;
import org.jgll.traversal.ISymbolVisitor;

public class IfThen extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Expression expression;
	private final Symbol thenPart;

	IfThen(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.thenPart = builder.thenPart;
	}
	
	public static IfThen ifThen(Expression expression, Symbol thenPart) {
		return builder(expression, thenPart).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Symbol getThenPart() {
		return thenPart;
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		return thenPart.size();
	}
	
	@Override
	public String toString() {
		return String.format("if (%s) %s", expression.toString(), thenPart.toString());
	}
	
	@Override
	public String toString(int j) {
		if (j - thenPart.size() <= 0){
			return String.format("if (%s) { %s }", expression.toString(), thenPart.toString(j - thenPart.size()));
		} else {
			return String.format("if (%s) %s", expression.toString(), thenPart.toString());
		}
	}
	
	public static Builder builder(Expression expression, Symbol symbol) {
		return new Builder(expression, symbol);
	}
	
	public static class Builder extends SymbolBuilder<IfThen> {
		
		private final Expression expression;
		private final Symbol thenPart;

		public Builder(IfThen ifThen) {
			super(ifThen);
			this.expression = ifThen.expression;
			this.thenPart = ifThen.thenPart;
		}
		
		public Builder(Expression expression, Symbol thenPart) {
			super(String.format("if (%s) %s", expression.toString(), thenPart.toString()));
			this.expression = expression;
			this.thenPart = thenPart;
		}

		@Override
		public IfThen build() {
			return new IfThen(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
