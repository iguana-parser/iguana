package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.IEvaluatorContext;
import org.jgll.datadependent.traversal.IAbstractASTVisitor;

public abstract class Statement extends AbstractAST {
	
	private static final long serialVersionUID = 1L;

	static public class Expression extends Statement {

		private static final long serialVersionUID = 1L;
		
		private final org.jgll.datadependent.ast.Expression exp;
		
		Expression(org.jgll.datadependent.ast.Expression exp) {
			this.exp = exp;
		}
		
		public org.jgll.datadependent.ast.Expression getExpression() {
			return exp;
		}
		
		@Override
		public Object interpret(IEvaluatorContext ctx) {
			exp.interpret(ctx);
			return null;
		}
		
		@Override
		public String getConstructorCode() {
			return "AST.stat(" + exp.getConstructorCode() + ")";
		}
		
		@Override
		public String toString() {
			return exp.toString();
		}

		@Override
		public <T> T accept(IAbstractASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
	}
	
	static public class VariableDeclaration extends Statement {
		
		private static final long serialVersionUID = 1L;
		
		private final org.jgll.datadependent.ast.VariableDeclaration decl;
		
		VariableDeclaration(org.jgll.datadependent.ast.VariableDeclaration decl) {
			this.decl = decl;
		}
		
		public org.jgll.datadependent.ast.VariableDeclaration getDeclaration() {
			return decl;
		}

		@Override
		public Object interpret(IEvaluatorContext ctx) {
			decl.interpret(ctx);
			return null;
		}
		
		@Override
		public String getConstructorCode() {
			return "AST.varDeclStat(" + decl.getConstructorCode() + ")";
		}
		
		@Override
		public String toString() {
			return decl.toString();
		}

		@Override
		public <T> T accept(IAbstractASTVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
	}

}
