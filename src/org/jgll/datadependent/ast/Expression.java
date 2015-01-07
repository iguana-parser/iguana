package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.EvaluatorContext;

public abstract class Expression extends AbstractAST {
	
	public boolean isBoolean() {
		return false;
	}
	
	static public abstract class Boolean extends Expression {
		
		public boolean isBoolean() {
			return true;
		}
		
		static public final Boolean TRUE = new Boolean() {		
			@Override
			public Object interpret(EvaluatorContext ctx) {
				return true;
			}
		};
		
		static public final Boolean FALSE = new Boolean() {
			@Override
			public Object interpret(EvaluatorContext ctx) {
				return false;
			}
		};
		
	}
	
	public boolean isInteger() {
		return false;
	}
	
	static public class Integer extends Expression {

		private final java.lang.Integer value;
		
		Integer(java.lang.Integer value) {
			this.value = value;
		}
		
		public boolean isInteger() {
			return true;
		}
		
		@Override
        public Object interpret(EvaluatorContext ctx) {
			return value;
		}
		
	}
	
	public boolean isReal() {
		return false;
	}
	
	static public class Real extends Expression {
        
		private final java.lang.Float value;
		
		Real(java.lang.Float value) {
			this.value = value;
		}
		
		public boolean isReal() {
			return true;
		}
		
		@Override
		public Object interpret(EvaluatorContext ctx) {
			return value;
		}
		
	}
	
	public boolean isString() {
		return false;
	}
	
	static public class String extends Expression {
		
		private final java.lang.String value;
		
		String(java.lang.String value) {
			this.value = value;
		}
		
		public boolean isString() {
			return true;
		}

		@Override
		public Object interpret(EvaluatorContext ctx) {
			return value;
		}
	}
	
	static public class Name extends Expression {
		
		private final java.lang.String name;
		
		Name(java.lang.String name) {
			this.name = name;
		}

		@Override
		public Object interpret(EvaluatorContext ctx) {
			Object value = ctx.getCurrentEnv().lookupVariable(name);
			if (value == null) {
				throw new RuntimeException("Undeclared variable: " + name);
			}
			return value;
		}
		
	}
	
	static public abstract class Call extends Expression {
		
		@SuppressWarnings("unused")
		private final java.lang.String fun;
		private final Expression[] arguments;
		
		Call(java.lang.String fun, Expression... arguments) {
			this.fun = fun;
			this.arguments = arguments;
		}
		
		protected Object[] interpretArguments(EvaluatorContext ctx) {
			Object[] values = new Object[arguments.length];
			
			int i = 0;
			while(i < arguments.length) {
				values[i] = arguments[i].interpret(ctx);
				i++;
			}
			
			return values;
		}
				
	}
	

}
