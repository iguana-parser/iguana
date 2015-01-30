package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.IEvaluatorContext;


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
			public Object interpret(IEvaluatorContext ctx) {
				return true;
			}
			
			@Override
			public java.lang.String toString() {
				return "true";
			}
		};
		
		static public final Boolean FALSE = new Boolean() {
			@Override
			public Object interpret(IEvaluatorContext ctx) {
				return false;
			}
			
			@Override
			public java.lang.String toString() {
				return "false";
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
        public Object interpret(IEvaluatorContext ctx) {
			return value;
		}
		
		@Override
		public java.lang.String toString() {
			return value.toString();
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
		public Object interpret(IEvaluatorContext ctx) {
			return value;
		}
		
		@Override
		public java.lang.String toString() {
			return value.toString();
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
		public Object interpret(IEvaluatorContext ctx) {
			return value;
		}
		
		@Override
		public java.lang.String toString() {
			return value;
		}
	}
	
	static public class Name extends Expression {
		
		private final java.lang.String name;
		
		Name(java.lang.String name) {
			this.name = name;
		}

		@Override
		public Object interpret(IEvaluatorContext ctx) {
			Object value = ctx.lookupInEnvironment(name);
			if (value == null) {
				throw new RuntimeException("Undeclared variable: " + name);
			}
			return value;
		}
		
		@Override
		public java.lang.String toString() {
			return name;
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
		
		protected Object[] interpretArguments(IEvaluatorContext ctx) {
			Object[] values = new Object[arguments.length];
			
			int i = 0;
			while(i < arguments.length) {
				values[i] = arguments[i].interpret(ctx);
				i++;
			}
			
			return values;
		}
				
	}
	
	static public class Assignment extends Expression {
		
		private final java.lang.String id;
		private final Expression exp;
		
		Assignment(java.lang.String id, Expression exp) {
			this.id = id;
			this.exp = exp;
		}

		@Override
		public Object interpret(IEvaluatorContext ctx) {
			ctx.storeInEnvironment(id, exp.interpret(ctx));
			return null;
		}
		
		@Override
		public java.lang.String toString() {
			return java.lang.String.format("%s = %s", id, exp);
		}
		
	}
	
}
