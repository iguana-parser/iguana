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

import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.datadependent.env.intarray.MutableLong;
import org.iguana.datadependent.values.Stack;
import org.iguana.grammar.exception.AssertionFailedException;
import org.iguana.grammar.exception.UnexpectedTypeOfArgumentException;
import org.iguana.sppf.NonPackedNode;
import org.iguana.utils.input.Input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.iguana.utils.string.StringUtil.listToString;

public class AST {

    /**
     * Expressions
     */

    public static final Expression TRUE = Expression.Boolean.TRUE;
    public static final Expression FALSE = Expression.Boolean.FALSE;

    public static Expression.Integer integer(java.lang.Integer value) {
        return new Expression.Integer(value);
    }

    public static Expression real(java.lang.Float value) {
        return new Expression.Real(value);
    }

    public static Expression string(java.lang.String value) {
        return new Expression.String(value);
    }

    public static Expression.Not not(Expression exp) {
        return new Expression.Not(exp);
    }

    public static Expression tuple(Expression... args) {
        return new Expression.Tuple(args);
    }

    public static Expression intTuple2(Expression.Integer element1, Expression.Integer element2) {
        return new Expression.IntTuple2(element1, element2);
    }

    public static Expression.Name var(java.lang.String name) {
        return new Expression.Name(name);
    }

    public static Expression var(java.lang.String name, int i) {
        return new Expression.Name(name, i);
    }

    public static class Println extends Expression.Call {
        Println(Expression... arguments) {
            super("println", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            List<Object> arguments = interpretArguments(ctx, input);
            for (Object argument : arguments) {
                System.out.print(argument);
                System.out.print("; ");
            }
            System.out.println();
            return null;
        }
    }

    public static Println println(Expression... args) {
        return new Println(args);
    }

    public static class Assert extends Expression.Call {

        Assert(Expression... arguments) {
            super("assert", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            for (Expression argument : arguments) {
                Object value = argument.interpret(ctx, input);
                if (!(value instanceof java.lang.Boolean)) {
                    throw new UnexpectedTypeOfArgumentException(this);
                }
                if (!((java.lang.Boolean) value)) {
                    throw new AssertionFailedException(argument);
                }
            }
            return null;
        }
    }

    public static Assert assertion(Expression... args) {
        return new Assert(args);
    }

    public static class Set extends Expression.Call {

        Set(Expression... arguments) {
            super("set", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            List<Object> objects = interpretArguments(ctx, input);
            return new HashSet<>(objects);
        }
    }

    public static Set set(Expression... args) {
        return new Set(args);
    }

    public static class Indent extends Expression.Call {

        Indent(Expression arg) {
            super("indent", arg);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Expression arg = arguments[0];
            Object value = arg.interpret(ctx, input);
            if (!(value instanceof java.lang.Integer)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            return input.getColumnNumber((java.lang.Integer) value);
        }
    }

    public static Indent indent(Expression...args) {
        if (args.length != 1) throw new RuntimeException("args size should be one");
        return indent(args[0]);
    }

    public static Indent indent(Expression arg) {
        return new Indent(arg);
    }

    public static class PPDeclare extends Expression.Call {
        public PPDeclare(Expression variable, Expression value) {
            super("ppDeclare", variable, value);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Expression variable = arguments[0];
            Expression value = arguments[1];

            Object var = variable.interpret(ctx, input);

            if (!(var instanceof NonPackedNode))
                throw new UnexpectedTypeOfArgumentException(this);

            NonPackedNode node = (NonPackedNode) var;

            ctx.declareGlobalVariable(input.subString(node.getLeftExtent(), node.getRightExtent()), value.interpret(ctx, input));

            return null;
        }
    }

    public static PPDeclare ppDeclare(Expression variable, Expression value) {
        return new PPDeclare(variable, value);
    }

    public static class PPLookup extends Expression.Call {

        PPLookup(Expression... arguments) {
            super("ppLookup", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof NonPackedNode)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            NonPackedNode node = (NonPackedNode) value;

            java.lang.String subString = input.subString(node.getLeftExtent(), node.getRightExtent());

            if (subString.equals("true"))
                return true;
            else if (subString.equals("false"))
                return false;

            Object obj = ctx.lookupGlobalVariable(subString);
            return obj != null ? obj : false;
        }
    }

    public static PPLookup ppLookup(Expression arg) {
        return new PPLookup(arg);
    }

    public static class EndsWith extends Expression.Call {

        EndsWith(Expression... arguments) {
            super("endsWith", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Expression index = arguments[0];
            Expression character = arguments[1];

            Object i = index.interpret(ctx, input);
            if (!(i instanceof java.lang.Integer)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            int j = (java.lang.Integer) i;

            Object c = character.interpret(ctx, input);

            if (!(c instanceof java.lang.String)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            Object obj = input.subString(j - 1, j);
            return obj.equals(c);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("endsWith(%s,\"%s\")", arguments[0], arguments[1]);
        }

    }

    public static EndsWith endsWith(Expression index, Expression character) {
        return new EndsWith(index, character);
    }

    public static class StartsWith extends Expression.Call {

        StartsWith(Expression... arguments) {
            super("startsWith", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object i = arguments[0].interpret(ctx, input);
            if (!(i instanceof java.lang.Integer)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            int j = (java.lang.Integer) i;

            for (int k = 1; k < arguments.length; k++) {
                Object str = arguments[k].interpret(ctx, input);

                if (!(str instanceof java.lang.String)) {
                    throw new UnexpectedTypeOfArgumentException(this);
                }

                int len = j + ((java.lang.String) str).length();
                if (len < input.length()) {
                    Object obj = input.subString(j, len);
                    if (obj.equals(str))
                        return true;
                }
            }
            return false;
        }
    }

    public static StartsWith startsWith(Expression... args) {
        return new StartsWith(args);
    }

    public static class Neg extends Expression.Call {
        Neg(Expression... arguments) {
            super("neg", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof java.lang.Integer)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }
            int v = (java.lang.Integer) value;
            return -v;
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("-(%s)", arguments[0]);
        }
    }

    public static Neg neg(Expression arg) {
        return new Neg(arg);
    }

    public static class Len extends Expression.Call {
        Len(Expression... arguments) {
            super("len", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof NonPackedNode)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            NonPackedNode node = (NonPackedNode) value;

            return node.getRightExtent() - node.getLeftExtent();
        }
    }

    public static Len len(Expression arg) {
        return new Len(arg);
    }

    public static class Pr1 extends Expression.Call {
        Pr1(Expression... arguments) {
            super("pr1", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Expression arg1 = arguments[0];
            Expression arg2 = arguments[0];
            Expression arg3 = arguments[0];
            int v = (java.lang.Integer) arg1.interpret(ctx, input);
            int curr = (java.lang.Integer) arg2.interpret(ctx, input);

            if (v >= curr)
                return v;

            int prev = (java.lang.Integer) arg3.interpret(ctx, input); // prev is actually previous plus one

            if (v >= prev)
                return curr;

            return 0;
        }
    }

    public static Pr1 pr1(Expression arg1, Expression arg2, Expression arg3) {
       return new Pr1(arg1, arg2, arg3);
    }

    public static class Pr2 extends Expression.Call {
        private final Expression arg1;
        private final Expression arg2;
        private final Expression[] arg3;

        Pr2(Expression arg1, Expression arg2, Expression[] arg3) {
            super("pr2", getArgs(arg1, arg2, arg3));
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }

        private static Expression[] getArgs(Expression arg1, Expression arg2, Expression[] arg3) {
            Expression[] args = new Expression[arg3.length + 2];
            args[0] = arg1;
            args[1] = arg2;
            int i = 2;
            for (Expression arg : arg3)
                args[i++] = arg;

            return args;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            int v = (java.lang.Integer) arg1.interpret(ctx, input);
            int curr = (java.lang.Integer) arg2.interpret(ctx, input);

            if (v >= curr)
                return v;

            int prev = (java.lang.Integer) arg3[0].interpret(ctx, input);

            if (v >= prev)
                return curr;

            for (int i = 1; i < arg3.length; i++) {
                prev = (java.lang.Integer) arg3[i].interpret(ctx, input);

                if (v >= prev)
                    return prev;
            }

            return 0;
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("pr1(%s,%s,%s)", arg1, arg2, listToString(arg3, ","));
        }
    }

    public static Pr2 pr2(Expression arg1, Expression arg2, Expression[] arg3) {
        return new Pr2(arg1, arg2, arg3);
    }

    public static class Pr3 extends Expression.Call {
        Pr3(Expression... arguments) {
            super("pr3", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {

            int v1 = (java.lang.Integer) arguments[0].interpret(ctx, input);
            int v2 = (java.lang.Integer) arguments[1].interpret(ctx, input);

            if (v1 == 0)
                return v2;

            if (v2 == 0)
                return v1;

            return java.lang.Integer.min(v1, v2);
        }
    }

    public static Pr3 pr3(Expression arg1, Expression arg2) {
        return new Pr3(arg1, arg2);
    }

    public static class Min extends Expression.Call {
        Min(Expression... arguments) {
            super("min", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            int v1 = (java.lang.Integer) arguments[0].interpret(ctx, input);
            int v2 = (java.lang.Integer) arguments[1].interpret(ctx, input);
            return java.lang.Integer.min(v1, v2);
        }
    }

    public static Min min(Expression arg1, Expression arg2) {
        return new Min(arg1, arg2);
    }

    public static class Map extends Expression.Call {
        Map(Expression... arguments) {
            super("map", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            return new HashMap<>();
        }
    }

    public static Map map() {
        return new Map();
    }

    public static class Put extends Expression.Call {

        Put(Expression... arguments) {
            super("put", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof java.util.Set<?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            java.util.Set<Object> s = (java.util.Set<Object>) value;

            value = arguments[1].interpret(ctx, input);
            if (!s.contains(value)) {
                s = new HashSet<>(s);
                s.add(value);
            }

            return s;
        }
    }

    public static Put put(Expression... args) {
        if (args.length != 2) throw new RuntimeException("args size should be two");
        return put(args[0], args[1]);
    }

    public static Put put(Expression arg1, Expression arg2) {
        return new Put(arg1, arg2);
    }

    public static class Put3 extends Expression.Call {
        Put3(Expression... arguments) {
            super("put", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof java.util.Map<?, ?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            java.util.Map<Object, Object> m = (java.util.Map<Object, Object>) value;

            m = new HashMap<>(m);
            m.put(arguments[1].interpret(ctx, input), arguments[2].interpret(ctx, input));

            return m;
        }
    }

    public static Put3 put(Expression arg1, Expression arg2, Expression arg3) {
        return new Put3(arg1, arg2, arg3);
    }

    public static class Contains extends Expression.Call {

        Contains(Expression... arguments) {
            super("contains", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof java.util.Set<?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            java.util.Set<Object> s = (java.util.Set<Object>) value;

            value = arguments[1].interpret(ctx, input);

            return s.contains(value);
        }
    }

    public static Contains contains(Expression...args) {
        if (args.length != 2) throw new RuntimeException("args size should be two");
        return contains(args[0], args[1]);
    }

    public static Contains contains(Expression arg1, Expression arg2) {
        return new Contains(arg1, arg2);
    }

    public static class Push extends Expression.Call {

        Push(Expression... arguments) {
            super("push", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Expression arg1 = arguments[0];
            Expression arg2 = arguments[1];
            Object value = arg1.interpret(ctx, input);

            if (value == null)
                return Stack.from(arg2.interpret(ctx, input));

            if (!(value instanceof Stack<?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            Stack<Object> s = (Stack<Object>) value;

            return s.push(arg2.interpret(ctx, input));
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("push(%s,%s)", arguments[0], arguments[1]);
        }
    }

    public static Push push(Expression arg1, Expression arg2) {
        return new Push(arg1, arg2);
    }

    public static class Pop extends Expression.Call {
        Pop(Expression... arguments) {
            super("pop", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof Stack<?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            Stack<Object> s = (Stack<Object>) value;

            return s.pop();
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("pop(%s)", arguments[0]);
        }
    }

    public static Pop pop(Expression arg) {
        return new Pop(arg);
    }

    public static class Top extends Expression.Call {
        Top(Expression... arguments) {
            super("top", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof Stack<?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            Stack<Object> s = (Stack<Object>) value;

            return s.top();
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("top(%s)", arguments[0]);
        }
    }

    public static Top top(Expression arg) {
        return new Top(arg);
    }

    public static class Find extends Expression.Call {
        Find(Expression... arguments) {
            super("find", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = arguments[0].interpret(ctx, input);
            if (!(value instanceof Stack<?>))
                throw new UnexpectedTypeOfArgumentException(this);

            @SuppressWarnings("unchecked")
            Stack<java.util.Map<java.lang.String, java.lang.Boolean>> s = (Stack<java.util.Map<java.lang.String, java.lang.Boolean>>) value;

            java.lang.String key = (java.lang.String) arguments[0].interpret(ctx, input);

            java.lang.Boolean hit;
            while (s != null) {
                hit = s.top().get(key);
                if (hit != null)
                    return hit;
                s = s.pop();
            }

            return false;
        }
    }

    public static Find find(Expression arg1, Expression arg2) {
        return new Find(arg1, arg2);
    }

    public static class Get extends Expression.Call {
        private final Expression arg1;
        private final int arg2;

        Get(Expression arg1, int arg2) {
            super("get", arg1, AST.integer(arg2));
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object result = arg1.interpret(ctx, input);
            if (result instanceof MutableLong) {
                MutableLong value = (MutableLong) result;
                if (arg2 == 0) {
                    return value.getHigherOrderInt();
                }
                return value.getLowerOrderInt();
            }
            if (result instanceof Long) {
                long value = (Long) result;
                if (arg2 == 0) {
                    return (int) (value >> 32);
                }
                return (int) (value & 0xffffffffL);
            }
            Object[] value = (Object[]) result;
            return value[arg2];
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s.%s", arg1, arg2);
        }
    }

    public static Get get(Expression arg1, int arg2) {
        return new Get(arg1, arg2);
    }

    public static class Get2 extends Expression.Call {

        Get2(Expression... arguments) {
            super("get", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Expression arg1 = arguments[0];
            Expression arg2 = arguments[1];
            Object result = arg1.interpret(ctx, input);
            if (result instanceof MutableLong) {
                MutableLong value = (MutableLong) result;
                int i = (java.lang.Integer) arg2.interpret(ctx, input);
                if (i == 0) {
                    return value.getHigherOrderInt();
                }
                return value.getLowerOrderInt();
            }
            if (result instanceof Long) {
                long value = (Long) result;
                int i = (java.lang.Integer) arg2.interpret(ctx, input);
                if (i == 0) {
                    return (int) (value >> 32);
                }
                return (int) (value & 0xffffffffL);
            }
            Object[] value = (Object[]) result;
            int i = (java.lang.Integer) arg2.interpret(ctx, input);
            return value[i];
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s.%s", arguments[0], arguments[1]);
        }
    }

    public static Get2 get(Expression arg1, Expression arg2) {
        return new Get2(arg1, arg2);
    }

    static final Object UNDEF = new Object() {
        public String toString() {
            return "UNDEF";
        }
    };

    public static class Shift extends Expression.Call {

        Shift(Expression... arguments) {
            super("shift", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            int i = (java.lang.Integer) arguments[0].interpret(ctx, input);
            if (i == 0)
                return 0;

            int j = (java.lang.Integer) arguments[1].interpret(ctx, input);
            return i & j;
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s<<%s", arguments[0], arguments[1]);
        }
    }

    public static Shift shift(Expression arg1, Expression arg2) {
        return new Shift(arg1, arg2);
    }

    public static class Undef extends Expression.Call {
        Undef(Expression... arguments) {
            super("undef", arguments);
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            return UNDEF;
        }

        @Override
        public java.lang.String toString() {
            return UNDEF.toString();
        }
    }

    public static Undef undef() {
        return new Undef();
    }

    public static Expression lShiftANDEqZero(Expression lhs, Expression rhs) {
        return new Expression.LShiftANDEqZero(lhs, rhs);
    }

    public static Expression orIndent(Expression index, Expression ind, Expression first, Expression lExt) {
        return new Expression.OrIndent(index, ind, first, lExt);
    }

    public static Expression andIndent(Expression index, Expression first, Expression lExt) {
        return new Expression.AndIndent(index, first, lExt);
    }

    public static Expression andIndent(Expression index, Expression first, Expression lExt, boolean returnIndex) {
        return new Expression.AndIndent(index, first, lExt, returnIndex);
    }

    public static Expression.Or or(Expression lhs, Expression rhs) {
        return new Expression.Or(lhs, rhs);
    }

    public static Expression.And and(Expression lhs, Expression rhs) {
        return new Expression.And(lhs, rhs);
    }

    public static Expression.Less less(Expression lhs, Expression rhs) {
        return new Expression.Less(lhs, rhs);
    }

    public static Expression.LessThanEqual lessEq(Expression lhs, Expression rhs) {
        return new Expression.LessThanEqual(lhs, rhs);
    }

    public static Expression.Greater greater(Expression lhs, Expression rhs) {
        return new Expression.Greater(lhs, rhs);
    }

    public static Expression.GreaterThanEqual greaterEq(Expression lhs, Expression rhs) {
        return new Expression.GreaterThanEqual(lhs, rhs);
    }

    public static Expression.Equal equal(Expression lhs, Expression rhs) {
        return new Expression.Equal(lhs, rhs);
    }

    public static Expression.NotEqual notEqual(Expression lhs, Expression rhs) {
        return new Expression.NotEqual(lhs, rhs);
    }

    public static Expression.LeftExtent lExt(String label) {
        return new Expression.LeftExtent(label);
    }

    public static Expression.RightExtent rExt(String label) {
        return new Expression.RightExtent(label);
    }

    public static Expression.Yield yield(String label) {
        return new Expression.Yield(label);
    }

    public static Expression.Yield yield(String label, int i) {
        return new Expression.Yield(label, i);
    }

    public static Expression.Val val(String label) {
        return new Expression.Val(label);
    }

    public static Expression.EndOfFile endOfFile(Expression index) {
        return new Expression.EndOfFile(index);
    }

    public static Expression ifThenElse(Expression condition, Expression thenPart, Expression elsePart) {
        return new Expression.IfThenElse(condition, thenPart, elsePart);
    }

    public static Expression.Assignment assign(java.lang.String id, Expression exp) {
        return new Expression.Assignment(id, exp);
    }

    public static Expression.Assignment assign(java.lang.String id, int i, Expression exp) {
        return new Expression.Assignment(id, i, exp);
    }

    //
    // Statements
    //
    public static Statement stat(Expression exp) {
        return new Statement.Expression(exp);
    }

    public static Statement varDeclStat(String name) {
        return new Statement.VariableDeclaration(new VariableDeclaration(name));
    }

    public static Statement varDeclStat(String name, Expression exp) {
        return new Statement.VariableDeclaration(new VariableDeclaration(name, exp));
    }

    public static Statement varDeclStat(String name, int i) {
        return new Statement.VariableDeclaration(new VariableDeclaration(name, i));
    }

    public static Statement varDeclStat(String name, int i, Expression exp) {
        return new Statement.VariableDeclaration(new VariableDeclaration(name, i, exp));
    }

    public static Statement varDeclStat(VariableDeclaration varDecl) {
        return new Statement.VariableDeclaration(varDecl);
    }

    public static VariableDeclaration varDecl(String name) {
        return new VariableDeclaration(name);
    }

    public static VariableDeclaration varDecl(String name, Expression exp) {
        return new VariableDeclaration(name, exp);
    }

    public static VariableDeclaration varDecl(String name, int i) {
        return new VariableDeclaration(name, i);
    }

    public static VariableDeclaration varDecl(String name, int i, Expression exp) {
        return new VariableDeclaration(name, i, exp);
    }

    public static Expression.Add add(Expression lhs, Expression rhs) {
        return new Expression.Add(lhs, rhs);
    }

    public static Expression.Subtract subtract(Expression lhs, Expression rhs) {
        return new Expression.Subtract(lhs, rhs);
    }

    public static Expression.Multiply multiply(Expression lhs, Expression rhs) {
        return new Expression.Multiply(lhs, rhs);
    }

    public static Expression.Divide divide(Expression lhs, Expression rhs) {
        return new Expression.Divide(lhs, rhs);
    }
}
