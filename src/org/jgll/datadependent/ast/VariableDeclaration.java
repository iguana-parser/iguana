package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.IEvaluatorContext;

public class VariableDeclaration extends AbstractAST {
	
	static public Object defaultValue = new Object() {};
	
	private final String name;
	private final Expression expression;
	
	VariableDeclaration(String name, Expression expression) {
		this.name = name;
		this.expression = expression;
	}
	
	VariableDeclaration(String name) {
		this(name, null);
	}

	@Override
	public Object interpret(IEvaluatorContext ctx) {
		Object value = defaultValue;
		if (expression != null) {
			value = expression.interpret(ctx);
		}
		ctx.storeInEnvironment(name, value);
		return null;
	}
	
	@Override
	public String toString() {
		return expression != null? String.format( "var %s = %s", name, expression) 
				: String.format("var %s", name);
	}

}
