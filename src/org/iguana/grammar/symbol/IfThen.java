package org.iguana.grammar.symbol;

import org.iguana.datadependent.ast.Expression;
import org.iguana.traversal.ISymbolVisitor;

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
	public String getConstructorCode() {
		return "IfThen.builder(" + expression.getConstructorCode() + "," + thenPart.getConstructorCode() + ")" 
								 + super.getConstructorCode()
								 + ".build()";
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		return thenPart.size() + 1;
	}
	
	@Override
	public String toString() {
		return String.format("if (%s) %s", expression.toString(), thenPart.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format("if (%s) { %s } else %s", 
								expression.toString(), 
								thenPart.toString(j), 
								j - thenPart.size() <= 1? Epsilon.getInstance().toString(j - thenPart.size()) 
														  : Epsilon.getInstance().toString());
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
