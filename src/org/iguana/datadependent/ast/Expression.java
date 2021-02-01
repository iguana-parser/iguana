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

import iguana.utils.input.Input;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.datadependent.traversal.IAbstractASTVisitor;
import org.iguana.grammar.exception.UndeclaredVariableException;
import org.iguana.grammar.exception.UnexpectedTypeOfArgumentException;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNodeWithValue;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static iguana.utils.string.StringUtil.listToString;
import static java.lang.Integer.toUnsignedLong;

public abstract class Expression extends AbstractAST {

    private static final long serialVersionUID = 1L;

    public boolean isBoolean() {
        return false;
    }

    public static abstract class Boolean extends Expression {

        private static final long serialVersionUID = 1L;

        public boolean isBoolean() {
            return true;
        }

        static final Boolean TRUE = new Boolean() {

            private static final long serialVersionUID = 1L;

            @Override
            public Object interpret(IEvaluatorContext ctx, Input input) {
                return true;
            }

            @Override
            public java.lang.String toString() {
                return "true";
            }

            @Override
            public <T> T accept(IAbstractASTVisitor<T> visitor) {
                return visitor.visit(this);
            }
        };

        static final Boolean FALSE = new Boolean() {

            private static final long serialVersionUID = 1L;

            @Override
            public Object interpret(IEvaluatorContext ctx, Input input) {
                return false;
            }

            @Override
            public java.lang.String toString() {
                return "false";
            }

            @Override
            public <T> T accept(IAbstractASTVisitor<T> visitor) {
                return visitor.visit(this);
            }

        };

    }

    public boolean isInteger() {
        return false;
    }

    public static class Integer extends Expression {

        private static final long serialVersionUID = 1L;

        private final java.lang.Integer value;

        Integer(java.lang.Integer value) {
            this.value = value;
        }

        public boolean isInteger() {
            return true;
        }

        public int getValue() {
            return value;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Integer)) return false;
            Integer other = (Integer) obj;
            return this.value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.valueOf(value);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Real extends Expression {

        private static final long serialVersionUID = 1L;

        private final java.lang.Float value;

        Real(java.lang.Float value) {
            this.value = value;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Real)) return false;
            Real other = (Real) obj;
            return this.value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public boolean isString() {
        return false;
    }

    public static class String extends Expression {

        private static final long serialVersionUID = 1L;

        private final java.lang.String value;

        String(java.lang.String value) {
            this.value = value;
        }

        public boolean isString() {
            return true;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof String)) return false;
            String other = (String) obj;
            return this.value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Tuple extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression[] elements;
        private final int length;

        Tuple(Expression... elements) {
            this.elements = elements;
            for (Expression element : elements)
                if (element == null)
                    throw new RuntimeException("Expressions of a tuple should not be null.");
            this.length = elements.length;
        }

        public Expression[] getElements() {
            return elements;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            if (length == 1)
                return elements[0].interpret(ctx, input);

            Object[] values = new Object[elements.length];
            for (int i = 0; i < elements.length; i++) {
                values[i] = elements[i].interpret(ctx, input);
            }

            return values;
        }

        @Override
        public java.lang.String toString() {
            return "(" + listToString(Arrays.stream(elements).map(Object::toString).collect(Collectors.toList()), ",") + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Tuple)) return false;
            Tuple other = (Tuple) obj;
            return this.length == other.length && Arrays.equals(this.elements, other.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.elements, this.length);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class IntTuple2 extends Expression {

        private static final long serialVersionUID = 1L;
        private final Integer element1;
        private final Integer element2;
        private final Long longValue;

        IntTuple2(Integer element1, Integer element2) {
            this.element1 = element1;
            this.element2 = element2;
            this.longValue = toUnsignedLong(element1.value) << 32 | toUnsignedLong(element2.value);
        }

        public Integer getElement1() {
            return element1;
        }

        public Integer getElement2() {
            return element2;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            return longValue;
        }

        @Override
        public java.lang.String toString() {
            return "(" + element1 + ", " + element2 + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof IntTuple2)) return false;
            IntTuple2 other = (IntTuple2) obj;
            return element1.equals(other.element1) && element2.equals(other.element2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element1, element2);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Name extends Expression {

        private static final long serialVersionUID = 1L;

        private final java.lang.String name;
        private final int i;

        Name(java.lang.String name) {
            this(name, -1);
        }

        Name(java.lang.String name, int i) {
            this.name = name;
            this.i = i;
        }

        public java.lang.String getName() {
            return name;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = i != -1 ? ctx.lookupVariable(i) : ctx.lookupVariable(name);
            if (value == null) {
                throw new UndeclaredVariableException(name);
            }
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Name)) return false;
            Name other = (Name) obj;
            return this.name.equals(other.name) && this.i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.i);
        }

        @Override
        public java.lang.String toString() {
            return name + (i != -1 ? ":" + i : "");
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static abstract class Call extends Expression {

        private static final long serialVersionUID = 1L;

        private final java.lang.String fun;
        private final Expression[] arguments;

        Call(java.lang.String fun, Expression... arguments) {
            this.fun = fun;
            this.arguments = arguments;
        }

        public java.lang.String getFunName() {
            return fun;
        }

        public Expression[] getArguments() {
            return this.arguments;
        }

        protected Object[] interpretArguments(IEvaluatorContext ctx, Input input) {
            Object[] values = new Object[arguments.length];

            int i = 0;
            while (i < arguments.length) {
                values[i] = arguments[i].interpret(ctx, input);
                i++;
            }

            return values;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Call)) return false;
            Call other = (Call) obj;
            return Arrays.equals(this.arguments, other.arguments) && this.fun.equals(other.fun);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.arguments, this.fun);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Assignment extends Expression {

        private static final long serialVersionUID = 1L;

        private final java.lang.String id;
        private final int i;
        private final Expression exp;

        Assignment(java.lang.String id, Expression exp) {
            this(id, -1, exp);
        }

        Assignment(java.lang.String id, int i, Expression exp) {
            this.id = id;
            this.i = i;
            this.exp = exp;
        }

        public java.lang.String getId() {
            return id;
        }

        public Expression getExpression() {
            return exp;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            if (i != -1)
                ctx.storeVariable(i, exp.interpret(ctx, input));
            else
                ctx.storeVariable(id, exp.interpret(ctx, input));
            return null;
        }

        @Override
        public java.lang.String toString() {
            return i != -1 ? java.lang.String.format("%s:%s = %s", id, i, exp) : java.lang.String.format("%s = %s", id, exp);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Assignment)) return false;
            Assignment other = (Assignment) obj;
            return this.id.equals(other.id) && this.i == other.i && this.exp.equals(other.exp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.id, this.i, this.exp);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class LShiftANDEqZero extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        LShiftANDEqZero(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer)
                return (((java.lang.Integer) lhs) & (1 << ((java.lang.Integer) rhs))) == 0;

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s&(1<<%s) == 0", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LShiftANDEqZero)) return false;
            LShiftANDEqZero other = (LShiftANDEqZero) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }


        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class OrIndent extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression index;
        private final Expression ind;
        private final Expression first;
        private final Expression lExt;

        OrIndent(Expression index, Expression ind, Expression first, Expression lExt) {
            this.index = index;
            this.ind = ind;
            this.first = first;
            this.lExt = lExt;
        }

        public Expression getIndex() {
            return index;
        }

        public Expression getIndent() {
            return ind;
        }

        public Expression getFirst() {
            return first;
        }

        public Expression getLExt() {
            return lExt;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {

            int ind = (java.lang.Integer) this.ind.interpret(ctx, input);

            if (ind == 0)
                return true;

            int first = (java.lang.Integer) this.first.interpret(ctx, input);
            int lExt;
            if (first == 1) {

                int index = (java.lang.Integer) this.index.interpret(ctx, input);
                lExt = (java.lang.Integer) this.lExt.interpret(ctx, input);

                if (lExt - index == 0)
                    return true;
                else {
                    int indent = input.getColumnNumber(lExt);
                    return indent > ind;
                }

            } else {
                lExt = (java.lang.Integer) this.lExt.interpret(ctx, input);
                int indent = input.getColumnNumber(lExt);
                return indent > ind;
            }

        }

        @Override
        public java.lang.String toString() {
            // return ind + " == 0 || (" + first + " && " + lExt + " - " + index + " == 0) || indent(" + lExt + ") > " + ind;
            return java.lang.String.format("f(%s,%s,%s,%s)", index, ind, first, lExt);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof OrIndent)) return false;
            OrIndent other = (OrIndent) obj;
            return this.index.equals(other.index) &&
                    this.ind.equals(other.ind) &&
                    this.first.equals(other.first) &&
                    this.lExt.equals(other.lExt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.index, this.ind, this.first, this.lExt);
        }


        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class AndIndent extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression index;
        private final Expression first;
        private final Expression lExt;

        private final boolean returnIndex;

        AndIndent(Expression index, Expression first, Expression lExt) {
            this.index = index;
            this.first = first;
            this.lExt = lExt;
            this.returnIndex = false;
        }

        AndIndent(Expression index, Expression first, Expression lExt, boolean returnIndex) {
            this.index = index;
            this.first = first;
            this.lExt = lExt;
            this.returnIndex = returnIndex;
        }

        public Expression getIndex() {
            return index;
        }

        public Expression getFirst() {
            return first;
        }

        public Expression getLExt() {
            return lExt;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            int first = (java.lang.Integer) this.first.interpret(ctx, input);
            if (first == 1) {

                int index = (java.lang.Integer) this.index.interpret(ctx, input);
                int lExt = (java.lang.Integer) this.lExt.interpret(ctx, input);

                if (lExt - index == 0)
                    return returnIndex ? index : 1;
            }

            return 0;
        }

        @Override
        public java.lang.String toString() {
//			return returnIndex? "(" +first + " && " + lExt + " - " + index + " == 0)?" + index
//					          : first + " && " + lExt + " - " + index + " == 0";
            return returnIndex ? java.lang.String.format("g(%s,%s,%s,%s)", index, first, lExt, 1)
                    : java.lang.String.format("g(%s,%s,%s,%s)", index, first, lExt, 0);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof AndIndent)) return false;
            AndIndent other = (AndIndent) obj;
            return this.index.equals(other.index) &&
                    this.first.equals(other.first) &&
                    this.lExt.equals(other.lExt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.index, this.first, this.lExt);
        }


        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Or extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        Or(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            boolean lhs = (java.lang.Boolean) this.lhs.interpret(ctx, input);
            if (lhs) return true;

            return this.rhs.interpret(ctx, input);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s || %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Or)) return false;
            Or other = (Or) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class And extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        And(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            boolean lhs = (java.lang.Boolean) this.lhs.interpret(ctx, input);
            if (!lhs) return false;

            return this.rhs.interpret(ctx, input);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s && %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof And)) return false;
            And other = (And) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Less extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        Less(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer) {
                return ((java.lang.Integer) lhs) < ((java.lang.Integer) rhs);
            }

            if (lhs instanceof java.lang.Float && rhs instanceof java.lang.Float) {
                return ((java.lang.Float) lhs) < ((java.lang.Float) rhs);
            }

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Less)) return false;
            Less other = (Less) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s < %s", lhs, rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class LessThanEqual extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        LessThanEqual(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer) {
                return ((java.lang.Integer) lhs) <= ((java.lang.Integer) rhs);
            }

            if (lhs instanceof java.lang.Float && rhs instanceof java.lang.Float) {
                return ((java.lang.Float) lhs) <= ((java.lang.Float) rhs);
            }

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s <= %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LessThanEqual)) return false;
            LessThanEqual other = (LessThanEqual) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Greater extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        Greater(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer) {
                return ((java.lang.Integer) lhs) > ((java.lang.Integer) rhs);
            }

            if (lhs instanceof java.lang.Float && rhs instanceof java.lang.Float) {
                return ((java.lang.Float) lhs) > ((java.lang.Float) rhs);
            }

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s > %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Greater)) return false;
            Greater other = (Greater) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class GreaterThanEqual extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        GreaterThanEqual(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer) {
                return ((java.lang.Integer) lhs) >= ((java.lang.Integer) rhs);
            }

            if (lhs instanceof java.lang.Float && rhs instanceof java.lang.Float) {
                return ((java.lang.Float) lhs) >= ((java.lang.Float) rhs);
            }

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s >= %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof GreaterThanEqual)) return false;
            GreaterThanEqual other = (GreaterThanEqual) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Equal extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        Equal(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs == AST.UNDEF || rhs == AST.UNDEF) {
                return lhs == rhs;
            }

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer) {
                return lhs.equals(rhs);
            }

            if (lhs instanceof java.lang.Float && rhs instanceof java.lang.Float) {
                return lhs.equals(rhs);
            }

            if (lhs instanceof java.lang.String && rhs instanceof java.lang.String) {
                return lhs.equals(rhs);
            }

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s == %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Equal)) return false;
            Equal other = (Equal) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class NotEqual extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression lhs;
        private final Expression rhs;

        NotEqual(Expression lhs, Expression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object lhs = this.lhs.interpret(ctx, input);
            Object rhs = this.rhs.interpret(ctx, input);

            if (lhs instanceof java.lang.Integer && rhs instanceof java.lang.Integer) {
                return lhs != rhs;
            }

            if (lhs instanceof java.lang.Float && rhs instanceof java.lang.Float) {
                return lhs != rhs;
            }

            throw new UnexpectedTypeOfArgumentException(this);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s != %s", lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof NotEqual)) return false;
            NotEqual other = (NotEqual) obj;
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lhs, this.rhs);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class LeftExtent extends Expression {

        private static final long serialVersionUID = 1L;

        public static java.lang.String format = "%s.lExt";

        private final java.lang.String label;

        LeftExtent(java.lang.String label) {
            this.label = label;
        }

        public java.lang.String getLabel() {
            return label;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = ctx.lookupVariable(java.lang.String.format(format, label));
            if (value == null) {
                throw new UndeclaredVariableException(label + "." + "lExt");
            }
            return value;
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s.lExt", label);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LeftExtent)) return false;
            LeftExtent other = (LeftExtent) obj;
            return this.label.equals(other.label);
        }

        @Override
        public int hashCode() {
            return this.label.hashCode();
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class RightExtent extends Expression {

        private static final long serialVersionUID = 1L;

        public static java.lang.String format = "%s.rExt";

        private final java.lang.String label;

        RightExtent(java.lang.String label) {
            this.label = label;
        }

        public java.lang.String getLabel() {
            return label;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = ctx.lookupVariable(label);
            if (value == null) {
                throw new UndeclaredVariableException(label);
            }

            if (!(value instanceof NonPackedNode)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            return ((NonPackedNode) value).getRightExtent();
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s.rExt", label);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof RightExtent)) return false;
            RightExtent other = (RightExtent) obj;
            return this.label.equals(other.label);
        }

        @Override
        public int hashCode() {
            return this.label.hashCode();
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Yield extends Expression {
        private static final long serialVersionUID = 1L;

        public static java.lang.String format = "%s.yield";

        private final java.lang.String label;
        private final int i;

        Yield(java.lang.String label) {
            this(label, -1);
        }

        Yield(java.lang.String label, int i) {
            this.label = label;
            this.i = i;
        }

        public java.lang.String getLabel() {
            return label;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = i == -1 ? ctx.lookupVariable(label) : ctx.lookupVariable(i);
            if (value == null) {
                throw new UndeclaredVariableException(label);
            }

//            if (!(value instanceof NonPackedNode)) {
//                throw new UnexpectedTypeOfArgumentException(this);
//            }
//
            NonPackedNode node = (NonPackedNode) value;
            return input.subString(node.getLeftExtent(), node.getRightExtent());
        }

        @Override
        public java.lang.String toString() {
            return i == -1 ? java.lang.String.format("%s.yield", label) : java.lang.String.format("%s:%d.yield", label, i);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Yield)) return false;
            Yield other = (Yield) obj;
            return this.label.equals(other.label) && this.i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.label, this.i);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Val extends Expression {
        private static final long serialVersionUID = 1L;

        public static java.lang.String format = "%s.val";

        private final java.lang.String label;

        Val(java.lang.String label) {
            this.label = label;
        }

        public java.lang.String getLabel() {
            return label;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            Object value = ctx.lookupVariable(label);
            if (value == null) {
                throw new UndeclaredVariableException(label);
            }

            if (!(value instanceof NonterminalNodeWithValue)) {
                throw new UnexpectedTypeOfArgumentException(this);
            }

            NonterminalNodeWithValue node = (NonterminalNodeWithValue) value;

            return node.getValue();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Val)) return false;
            Val other = (Val) obj;
            return this.label.equals(other.label);
        }

        @Override
        public int hashCode() {
            return this.label.hashCode();
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("%s.val", label);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class EndOfFile extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression index;

        EndOfFile(Expression index) {
            this.index = index;
        }

        public Expression getIndex() {
            return index;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            int index = (java.lang.Integer) this.index.interpret(ctx, input);
            int length = input.length();
            return length == index + 1;
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("$(%s)", index);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof EndOfFile)) return false;
            EndOfFile other = (EndOfFile) obj;
            return this.index.equals(other.index);
        }

        @Override
        public int hashCode() {
            return this.index.hashCode();
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }

    }

    public static class IfThenElse extends Expression {

        private static final long serialVersionUID = 1L;

        private final Expression condition;
        private final Expression thenPart;
        private final Expression elsePart;

        IfThenElse(Expression condition, Expression thenPart, Expression elsePart) {
            this.condition = condition;
            this.thenPart = thenPart;
            this.elsePart = elsePart;
        }

        public Expression getCondition() {
            return condition;
        }

        public Expression getThenPart() {
            return thenPart;
        }

        public Expression getElsePart() {
            return elsePart;
        }

        @Override
        public Object interpret(IEvaluatorContext ctx, Input input) {
            boolean cond = (java.lang.Boolean) condition.interpret(ctx, input);
            if (cond)
                return thenPart.interpret(ctx, input);
            else
                return elsePart.interpret(ctx, input);
        }

        @Override
        public java.lang.String toString() {
            return java.lang.String.format("(%s)? %s : %s", condition, thenPart, elsePart);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof IfThenElse)) return false;
            IfThenElse other = (IfThenElse) obj;
            return this.condition.equals(other.condition) &&
                    this.thenPart.equals(other.thenPart) &&
                    this.elsePart.equals(other.elsePart);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.condition, this.thenPart, this.elsePart);
        }

        @Override
        public <T> T accept(IAbstractASTVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

}
