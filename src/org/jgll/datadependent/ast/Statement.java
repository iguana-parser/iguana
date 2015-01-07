package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.Environment;
import org.jgll.datadependent.env.EvaluatorContext;

public abstract class Statement extends AbstractAST {
	
	static public class Assignment extends Statement {
		
		private final java.lang.String id;
		private final org.jgll.datadependent.ast.Expression exp;
		
		Assignment(java.lang.String id, org.jgll.datadependent.ast.Expression exp) {
			this.id = id;
			this.exp = exp;
		}

		@Override
		public Object interpret(EvaluatorContext ctx) {
			Environment env = ctx.getCurrentEnv().storeVariableLocally(id, exp.interpret(ctx));
			ctx.setCurrentEnv(env);
			return null;
		}
		
	}
	
	static public class Expression extends Statement {

		private final org.jgll.datadependent.ast.Expression exp;
		
		Expression(org.jgll.datadependent.ast.Expression exp) {
			this.exp = exp;
		}
		
		@Override
		public Object interpret(EvaluatorContext ctx) {
			exp.interpret(ctx);
			return null;
		}
		
	}

}
