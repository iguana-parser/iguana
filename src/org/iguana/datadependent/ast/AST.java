/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.datadependent.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.datadependent.values.Stack;
import org.iguana.grammar.exception.UnexpectedTypeOfArgumentException;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.generator.GeneratorUtil;

public class AST {
	
	/**
	 * 		Expressions
	 */
	
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
	
	static public Expression tuple(Expression... args) {
		return new Expression.Tuple(args);
	}
	
	static public Expression var(java.lang.String name) {
		return new Expression.Name(name);
	}
	
	static public Expression println(Expression... args) {
		return new Expression.Call("println", args) {
					
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object[] arguments = interpretArguments(ctx);
						for (Object argument : arguments) {
							System.out.print(argument);
							System.out.print("; ");
						}
						System.out.println();
						return null;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						java.lang.String[] codes = new java.lang.String[args.length];
						
						int j = 0;
						for (Expression arg : args) {
							codes[j] = arg.getConstructorCode();
							j++;
						}
						
						return "AST.println(" + GeneratorUtil.listToString(codes, ",") + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("%s(%s)", "println", GeneratorUtil.listToString(args, ","));
					}
		};
	}
	
	static public Expression indent(Expression arg) {
		return new Expression.Call("indent", arg) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg.interpret(ctx);
						if (!(value instanceof java.lang.Integer)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						return ctx.getInput().getColumnNumber((java.lang.Integer) value);
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.indent(" + arg.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("indent(%s)", arg);
					}
		};
	}
	
	static public Expression ppDeclare(Expression variable, Expression value) {
		return new Expression.Call("ppDeclare", variable, value) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						
						Object var = variable.interpret(ctx);
						
						if (!(var instanceof NonPackedNode))
							throw new UnexpectedTypeOfArgumentException(this);
						
						NonPackedNode node = (NonPackedNode) var;
						
						ctx.declareGlobalVariable(ctx.getInput().subString(node.getLeftExtent(), node.getRightExtent()), 
								                  value.interpret(ctx));
						
						return null;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.ppDeclare(" + variable.getConstructorCode() + ", " + value.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("ppDeclare(%s,%s)", variable, value);
					}
		};
	}
	
	static public Expression ppLookup(Expression arg) {
		return new Expression.Call("ppLookup", arg) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg.interpret(ctx);
						if (!(value instanceof NonPackedNode)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						NonPackedNode node = (NonPackedNode) value;
						
						java.lang.String subString = ctx.getInput().subString(node.getLeftExtent(), node.getRightExtent());
						
						if (subString.equals("true"))
							return true;
						else if (subString.equals("false"))
							return false;
						
						Object obj = ctx.lookupGlobalVariable(subString);
						return obj != null? obj : false;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.ppLookup(" + arg.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("ppLookup(%s)", arg);
					}
		};
	}
	
	static public Expression endsWith(Expression index, Expression character) {
		return new Expression.Call("endsWith", index, character) {
			
					private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object i = index.interpret(ctx);
						if (!(i instanceof java.lang.Integer)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						int j = (java.lang.Integer) i;
						
						Object c = character.interpret(ctx);
						
						if (!(c instanceof java.lang.String)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						Object obj = ctx.getInput().subString(j - 1, j);
						return obj.equals(c);
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.endsWith(" + index.getConstructorCode() + "," + character.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("endsWith(%s,\"%s\")", index, character);
					}
		};
	}
	
	static public Expression startsWith(Expression... args) {
		return new Expression.Call("startsWith", args) {
			
					private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object i = args[0].interpret(ctx);
						if (!(i instanceof java.lang.Integer)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						int j = (java.lang.Integer) i;
						
						for (int k = 1; k < args.length; k++) {
							Object str = args[k].interpret(ctx);
							
							if (!(str instanceof java.lang.String)) {
								throw new UnexpectedTypeOfArgumentException(this);
							}
							
							int len = j + ((java.lang.String) str).length();
							if (len < ctx.getInput().length()) {
								Object obj = ctx.getInput().subString(j, len);
								if (obj.equals(str))
									return true;
							}
						}
						return false;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						List<java.lang.String> code = new ArrayList<>();
						for (Expression arg : args)
							code.add(arg.getConstructorCode());
						return "AST.startsWith(" + GeneratorUtil.listToString(code, ",") + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return "startsWith(" + GeneratorUtil.listToString(args, ",") + ")";
					}
		};
	}
	
	static public Expression not(Expression arg) {
		return new Expression.Call("not", arg) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg.interpret(ctx);
						if (!(value instanceof java.lang.Boolean)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						return !((java.lang.Boolean) value);
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.not(" + arg.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("not(%s)", arg);
					}
		};
	}
	
	static public Expression len(Expression arg) {
		return new Expression.Call("len", arg) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg.interpret(ctx);
						if (!(value instanceof NonPackedNode)) {
							throw new UnexpectedTypeOfArgumentException(this);
						}
						
						NonPackedNode node = (NonPackedNode) value;
						
						return node.getRightExtent() - node.getLeftExtent();
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.len(" + arg.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("len(%s)", arg);
					}
		};
	}
	
	static public Expression pr1(Expression arg1, Expression arg2, Expression arg3) {
		return new Expression.Call("pr1", arg1, arg2, arg3) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						int v = (java.lang.Integer) arg1.interpret(ctx);
						int curr = (java.lang.Integer) arg2.interpret(ctx);
						
						if (v >= curr)
							return v;
						
						int prev = (java.lang.Integer) arg3.interpret(ctx); // prev is actually previous plus one
						
						if (v >= prev)
							return curr;
						
						return 0;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.pr1(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + "," + arg3.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("pr1(%s,%s,%s)", arg1, arg2, arg3);
					}
		};
	}
	
	static public Expression pr2(Expression arg1, Expression arg2, Expression[] arg3) {
		Expression[] args = new Expression[arg3.length + 2];
		args[0] = arg1; args[1] = arg2;
		int i = 2;
		for (Expression arg : arg3)
			args[i++] = arg;
		
		return new Expression.Call("pr2", args) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						
						int v = (java.lang.Integer) arg1.interpret(ctx);
						int curr = (java.lang.Integer) arg2.interpret(ctx);
						
						if (v >= curr)
							return v;
						
						int prev = (java.lang.Integer) arg3[0].interpret(ctx);
						
						if (v >= prev)
							return curr;
						
						for (int i = 1; i < arg3.length; i++) {
							prev = (java.lang.Integer) arg3[i].interpret(ctx);
							
							if (v >= prev)
								return prev;
						}
						
						return 0;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						GeneratorUtil.listToString(Arrays.stream(arg3).map(arg -> arg.getConstructorCode()).collect(Collectors.toList()), ",");
						return "AST.pr1(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + "," 
										  + "new Expression[] {" + GeneratorUtil.listToString(Arrays.stream(arg3).map(arg -> arg.getConstructorCode()).collect(Collectors.toList()), ",") + "}" + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("pr1(%s,%s,%s)", arg1, arg2, GeneratorUtil.listToString(arg3, ","));
					}
		};
	}
	
	static public Expression map() {
		return new Expression.Call("map") {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						return new HashMap<Object, Object>();
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.map()";
					}
					
					@Override
					public java.lang.String toString() {
						return "map()";
					}
		};
	}
	
	static public Expression put(Expression arg1, Expression arg2) {
		return new Expression.Call("put", arg1, arg2) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg1.interpret(ctx);
						if (!(value instanceof Set<?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Set<Object> s = (Set<Object>) value;
						
						value = arg2.interpret(ctx);
						if (!s.contains(value)) {
							s = new HashSet<Object>(s);
							s.add(value);
						}
						
						return s;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.put(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("put(%s,%s)", arg1, arg2);
					}
		};
	}
	
	static public Expression put(Expression arg1, Expression arg2, Expression arg3) {
		return new Expression.Call("put", arg1, arg2, arg3) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg1.interpret(ctx);
						if (!(value instanceof Map<?,?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Map<Object, Object> m = (Map<Object, Object>) value;
						
						m = new HashMap<Object, Object>(m);
						m.put(arg2.interpret(ctx), arg3.interpret(ctx));
						
						return m;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.put(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + "," + arg3.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("put(%s,%s,%s)", arg1, arg2, arg3);
					}
		};
	}
	
	static public Expression contains(Expression arg1, Expression arg2) {
		return new Expression.Call("contains", arg1, arg2) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg1.interpret(ctx);
						if (!(value instanceof Set<?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Set<Object> s = (Set<Object>) value;
						
						value = arg2.interpret(ctx);
						
						return s.contains(value);
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.contains(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("contains(%s,%s)", arg1, arg2);
					}
		};
	}
	
	static public Expression push(Expression arg1, Expression arg2) {
		return new Expression.Call("push", arg1, arg2) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg1.interpret(ctx);
						
						if (value == null)
							return Stack.from(arg2.interpret(ctx));
						
						if (!(value instanceof Stack<?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Stack<Object> s = (Stack<Object>) value;
						
						return s.push(arg2.interpret(ctx));
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.push(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("push(%s,%s)", arg1, arg2);
					}
		};
	}
	
	static public Expression pop(Expression arg) {
		return new Expression.Call("pop", arg) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg.interpret(ctx);
						if (!(value instanceof Stack<?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Stack<Object> s = (Stack<Object>) value;
						
						return s.pop();
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.pop(" + arg.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("pop(%s)", arg);
					}
		};
	}
	
	static public Expression top(Expression arg) {
		return new Expression.Call("top", arg) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg.interpret(ctx);
						if (!(value instanceof Stack<?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Stack<Object> s = (Stack<Object>) value;
						
						return s.top();
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.top(" + arg.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("top(%s)", arg);
					}
		};
	}
	
	static public Expression find(Expression arg1, Expression arg2) {
		return new Expression.Call("find", arg1, arg2) {
			
			private static final long serialVersionUID = 1L;

					@Override
					public Object interpret(IEvaluatorContext ctx) {
						Object value = arg1.interpret(ctx);
						if (!(value instanceof Stack<?>))
							throw new UnexpectedTypeOfArgumentException(this);
						
						@SuppressWarnings("unchecked")
						Stack<Map<java.lang.String, java.lang.Boolean>> s = (Stack<Map<java.lang.String, java.lang.Boolean>>) value;
						
						java.lang.String key = (java.lang.String) arg2.interpret(ctx);
						
						java.lang.Boolean hit;
						while (s != null) {
							hit = s.top().get(key);
							if (hit != null)
								return hit;
							s = s.pop();
						}
						
						return false;
					}
					
					@Override
					public java.lang.String getConstructorCode() {
						return "AST.find(" + arg1.getConstructorCode() + "," + arg2.getConstructorCode() + ")";
					}
					
					@Override
					public java.lang.String toString() {
						return java.lang.String.format("find(%s,%s)", arg1, arg2);
					}
		};
	}
	
	static public Expression lShiftANDEqZero(Expression lhs, Expression rhs) {
		return new Expression.LShiftANDEqZero(lhs, rhs);
	}
	
	static public Expression orIndent(Expression index, Expression ind, Expression first, Expression lExt) {
		return new Expression.OrIndent(index, ind, first, lExt);
	}
	
	static public Expression andIndent(Expression index, Expression first, Expression lExt) {
		return new Expression.AndIndent(index, first, lExt);
	}
	
	static public Expression andIndent(Expression index, Expression first, Expression lExt, boolean returnIndex) {
		return new Expression.AndIndent(index, first, lExt, returnIndex);
	}
	
	static public Expression or(Expression lhs, Expression rhs) {
		return new Expression.Or(lhs, rhs);
	}
	
	static public Expression and(Expression lhs, Expression rhs) {
		return new Expression.And(lhs, rhs);
	}
	
	static public Expression less(Expression lhs, Expression rhs) {
		return new Expression.Less(lhs, rhs);
	}
	
	static public Expression lessEq(Expression lhs, Expression rhs) {
		return new Expression.LessThanEqual(lhs, rhs);
	}
	
	static public Expression greater(Expression lhs, Expression rhs) {
		return new Expression.Greater(lhs, rhs);
	}
	
	static public Expression greaterEq(Expression lhs, Expression rhs) {
		return new Expression.GreaterThanEqual(lhs, rhs);
	}
	
	static public Expression equal(Expression lhs, Expression rhs) {
		return new Expression.Equal(lhs, rhs);
	}
	
	static public Expression notEqual(Expression lhs, Expression rhs) {
		return new Expression.NotEqual(lhs, rhs);
	}
	
	static public Expression lExt(String label) {
		return new Expression.LeftExtent(label);
	}
	
	static public Expression rExt(String label) {
		return new Expression.RightExtent(label);
	}
	
	static public Expression yield(String label) {
		return new Expression.Yield(label);
	}
	
	static public Expression val(String label) {
		return new Expression.Val(label);
	}
	
	static public Expression endOfFile(Expression index) {
		return new Expression.EndOfFile(index);
	}
	
	/**
	 * 
	 * 		Statements
	 */
	
	static public Expression assign(java.lang.String id, Expression exp) {
		return new Expression.Assignment(id, exp);
	}
	
	static public Statement stat(Expression exp) {
		return new Statement.Expression(exp);
	}
	
	static public Statement varDeclStat(String name) {
		return new Statement.VariableDeclaration(new VariableDeclaration(name));
	}
	
	static public Statement varDeclStat(String name, Expression exp) {
		return new Statement.VariableDeclaration(new VariableDeclaration(name, exp));
	}
	
	static public Statement varDeclStat(VariableDeclaration varDecl) {
		return new Statement.VariableDeclaration(varDecl);
	}
	
	static public VariableDeclaration varDecl(String name) {
		return new VariableDeclaration(name);
	}
	
	static public VariableDeclaration varDecl(String name, Expression exp) {
		return new VariableDeclaration(name, exp);
	}

}
