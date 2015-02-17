package org.jgll.grammar.symbol;

import org.jgll.traversal.ISymbolVisitor;
import org.jgll.util.generator.GeneratorUtil;

public class Code extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;
	private final org.jgll.datadependent.ast.Statement[] statements;
	
	Code(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
		this.statements = builder.statements;
	}
	
	public static Code code(Symbol symbol, org.jgll.datadependent.ast.Statement... statements) {
		return builder(symbol, statements).build();
	}
	
	public  org.jgll.datadependent.ast.Statement[] getStatements() {
		return statements;
	}

	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s do %s;", symbol.toString(), GeneratorUtil.listToString(statements, ";"));
	}
		
	public static Builder builder(Symbol symbol, org.jgll.datadependent.ast.Statement... statements) {
		assert statements.length != 0;
		return new Builder(symbol, statements);
	}
	
	public static class Builder extends SymbolBuilder<Code> {
		
		private final Symbol symbol;
		private final org.jgll.datadependent.ast.Statement[] statements;

		public Builder(Code code) {
			super(code);
			this.symbol = code.symbol;
			this.statements = code.statements;
		}
		
		public Builder(Symbol symbol, org.jgll.datadependent.ast.Statement... statements) {
			super(String.format("%s do %s", symbol.toString(), GeneratorUtil.listToString(statements, ";")));
			
			assert statements.length != 0;
			
			this.symbol = symbol;
			this.statements = statements;
		}
		
		@Override
		public Code build() {
			return new Code(this);
		}
		
	}
	
	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
