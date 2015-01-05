package org.jgll.datadependent.ast;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.env.EvalContext;

public class AST {
	
	static public final Expression TRUE = Expression.Boolean.TRUE;
	static public final Expression FALSE = Expression.Boolean.FALSE;
	
	static public Expression integer(java.lang.Integer value) {
		return new Expression.Integer(value);
	}
	
	static public Expression real(java.lang.Float value) {
		return new Expression.Real(value);
	}
	
	static public Expression string(java.lang.String value) {
		return new Expression.String(value);
	}
	
	static public Expression assign(java.lang.String id, Expression exp) {
		return new Expression.Assignment(id, exp);
	}
	
	static public Expression println(Expression... args) {
		return new Expression.Call("", args) {
					@Override
					public Object interpret(EvalContext ctx) {
						Object[] arguments = interpretArguments(ctx);
						for (Object argument : arguments) {
							System.out.print(argument);
							System.out.print("; ");
						}
						System.out.println();
						return null;
					}
		};
	}

}
