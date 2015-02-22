package org.jgll.grammar.symbol;

import org.jgll.datadependent.ast.Expression;
import org.jgll.traversal.ISymbolVisitor;

public class IfThenElse extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Expression expression;
	private final Symbol thenPart;
	private final Symbol elsePart;

	IfThenElse(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.thenPart = builder.thenPart;
		this.elsePart = builder.elsePart;
	}
	
	public static IfThenElse ifThenElse(Expression expression, Symbol thenPart, Symbol elsePart) {
		return builder(expression, thenPart, elsePart).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Symbol getThenPart() {
		return thenPart;
	}
	
	public Symbol getElsePart() {
		return elsePart;
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("if (%s) %s else %s", expression.toString(), thenPart.toString(), elsePart.toString());
	}
	
	@Override
	public int size() {
		return thenPart.size() + elsePart.size();
	}
	
	@Override
	public String toString(int j) {
		return String.format("if (%s) { %s } else { %s }", 
								expression.toString(), 
								thenPart.toString(j), 
								j - thenPart.size() <= 1? elsePart.toString(j - thenPart.size())
											           : elsePart.toString());
	}
	
	public static Builder builder(Expression expression, Symbol thenPart, Symbol elsePart) {
		return new Builder(expression, thenPart, elsePart);
	}
	
	public static class Builder extends SymbolBuilder<IfThenElse> {
		
		private final Expression expression;
		private final Symbol thenPart;
		private final Symbol elsePart;

		public Builder(IfThenElse ifThenElse) {
			super(ifThenElse);
			this.expression = ifThenElse.expression;
			this.thenPart = ifThenElse.thenPart;
			this.elsePart = ifThenElse.elsePart;
		}
		
		public Builder(Expression expression, Symbol thenPart, Symbol elsePart) {
			super(String.format("if (%s) %s else %s;", expression.toString(), thenPart.toString(), elsePart.toString()));
			this.expression = expression;
			this.thenPart = thenPart;
			this.elsePart = elsePart;
		}

		@Override
		public IfThenElse build() {
			return new IfThenElse(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
