package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.IEvaluatorContext;

public abstract class Statement extends AbstractAST {
	
	static public class Assignment extends Statement {
		
		private final java.lang.String id;
		private final org.jgll.datadependent.ast.Expression exp;
		
		Assignment(java.lang.String id, org.jgll.datadependent.ast.Expression exp) {
			this.id = id;
			this.exp = exp;
		}

		@Override
		public Object interpret(IEvaluatorContext ctx) {
			ctx.storeInEnvironment(id, exp.interpret(ctx));
			return null;
		}
		
		@Override
		public String toString() {
			return String.format("%s = %s", id, exp);
		}
		
	}
	
	static public class Expression extends Statement {

		private final org.jgll.datadependent.ast.Expression exp;
		
		Expression(org.jgll.datadependent.ast.Expression exp) {
			this.exp = exp;
		}
		
		@Override
		public Object interpret(IEvaluatorContext ctx) {
			exp.interpret(ctx);
			return null;
		}
		
		@Override
		public String toString() {
			return exp.toString();
		}
		
	}

}
