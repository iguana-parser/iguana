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
package org.iguana.grammar.iggy;

import iguana.parsetrees.iggy.TermTraversal;
import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.regex.CharacterRange;
import org.iguana.regex.Epsilon;
import org.iguana.regex.RegularExpression;
import org.iguana.traversal.ISymbolVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Anastasia Izmaylova
 */
public class GrammarBuilder implements TermTraversal.Actions {

    public static class Identifier {
        public final String id;
        public Identifier(String id) {
            this.id = id;
        }
        public static Identifier ident(String name) { return new Identifier(name); }
    }

    public static Identifier identifier() { return new Identifier(null); }
	
	public static class Rule {
		public static List<org.iguana.grammar.symbol.Rule> syntax(List<String> tag, Identifier name, List<Identifier> parameters, List<Alternates> body) {
            List<org.iguana.grammar.symbol.Rule> rules = new ArrayList<>();
            body.forEach(group -> { // TODO: integrate precedence logic
                group.alternates.forEach(alternate -> {
                    if (alternate.rest != null) { // Associativity group
                        {   // Note: Do not move this block!
                            org.iguana.grammar.symbol.Rule.Builder builder = org.iguana.grammar.symbol.Rule.withHead(Nonterminal.withName(name.id));
                            List<org.iguana.grammar.symbol.Symbol> symbols = new ArrayList<>();
                            symbols.add(alternate.first.first);
                            if (alternate.first.rest != null)
                                addAll(symbols, alternate.first.rest);
                            symbols.addAll(alternate.first.ret);
                            rules.add(builder.addSymbols(symbols).build());
                        }
                        alternate.rest.forEach(sequence -> {
                            org.iguana.grammar.symbol.Rule.Builder builder = org.iguana.grammar.symbol.Rule.withHead(Nonterminal.withName(name.id));
                            List<org.iguana.grammar.symbol.Symbol> symbols = new ArrayList<>();
                            symbols.add(sequence.first);
                            if (sequence.rest != null)
                                addAll(symbols, sequence.rest);
                            symbols.addAll(sequence.ret);
                            rules.add(builder.addSymbols(symbols).build());
                        });
                    } else {
                        org.iguana.grammar.symbol.Rule.Builder builder = org.iguana.grammar.symbol.Rule.withHead(Nonterminal.withName(name.id));
                        List<org.iguana.grammar.symbol.Symbol> symbols = new ArrayList<>();
                        symbols.add(alternate.first.first);
                        if (alternate.first.rest != null)
                            addAll(symbols, alternate.first.rest);
                        symbols.addAll(alternate.first.ret);
                        rules.add(builder.addSymbols(symbols).build());
                    }
                });
            });
            return rules;
		}
	}
	
	public static Rule rule() { return new Rule(); }

    public static class RegexRule {
        public static org.iguana.grammar.symbol.Rule regex(Identifier name, List<org.iguana.grammar.symbol.Symbol> body) {
            return org.iguana.grammar.symbol.Rule.withHead(Nonterminal.withName(name.id)).addSymbols(body).build();
        }
    }

    public static RegexRule regexrule() { return new RegexRule(); }

    public static class Alternates {
        public final List<Alternate> alternates;
        public Alternates(List<Alternate> alternates) {
            this.alternates = alternates;
        }

        public static Alternates prec(List<Alternate> alternates) { return new Alternates(alternates); }
    }

    public static Alternates alternates() { return new Alternates(null); }

    public static class Alternate {

        public final Sequence first;
        public final List<Sequence> rest;
        public final Attribute associativity;

        public Alternate(Sequence sequence) {
            this.first = sequence;
            this.rest = null;
            this.associativity = null;
        }
        public Alternate(Sequence sequence, List<Sequence> sequences, Attribute associativity) {
            this.first = sequence;
            this.rest = sequences;
            this.associativity = associativity;
        }

        public static Alternate sequence(Sequence sequence) { return new Alternate(sequence); }
        public static Alternate assoc(Sequence sequence, List<Sequence> sequences, Attribute associativity) {
            return new Alternate(sequence, sequences, associativity);
        }
    }

    public static Alternate alternate() { return new Alternate(null); }

    public static class Sequence {

        public final org.iguana.grammar.symbol.Symbol first;
        public final List<org.iguana.grammar.symbol.Symbol> rest;
        public final List<org.iguana.grammar.symbol.Symbol> ret;
        public final List<Attribute> attributes;
        public final List<Attribute> label;

        public Sequence(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> ret, List<Attribute> label) {
            this.first = symbol;
            this.rest = null;
            this.ret = ret;
            this.attributes = null;
            this.label = label;
        }

        public Sequence(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> symbols, List<org.iguana.grammar.symbol.Symbol> ret, List<Attribute> attributes) {
            this.first = symbol;
            this.rest = symbols;
            this.ret = ret;
            this.attributes = attributes;
            this.label = null;
        }

        public static Sequence single(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> ret, List<Attribute> label) {
            return new Sequence(symbol, ret, label);
        }

        public static Sequence morethantwo(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> symbols, List<org.iguana.grammar.symbol.Symbol> ret, List<Attribute> attributes) {
            return new Sequence(symbol, symbols, ret, attributes);
        }

    }

    public static Sequence sequence() { return new Sequence(null, null, null); }

    public static class Attribute {
        public final String attribute;
        public Attribute(String attribute) {
            this.attribute = attribute;
        }

        public static Attribute assoc(String associativity) { return new Attribute(associativity); }
        public static Attribute label(Identifier label) { return new Attribute(label.id); }
    }

    public static class AAttribute {
        public static Attribute assoc(String attribute) { return new Attribute(attribute); }
    }

    public static AAttribute aattribute() { return new AAttribute(); }

    public static class LAttribute {
        public static Attribute label(Identifier attribute) { return new Attribute(attribute.id); }
    }

    public static LAttribute lattribute() { return new LAttribute(); }

    public static class Symbols {
        public static org.iguana.grammar.symbol.Sequence<org.iguana.grammar.symbol.Symbol> sequence(List<org.iguana.grammar.symbol.Symbol> symbols) {
            if (symbols.isEmpty())
                return org.iguana.grammar.symbol.Sequence.from(symbols);
            List<org.iguana.grammar.symbol.Symbol> l = new ArrayList<>();
            addAll(l, symbols);
            return org.iguana.grammar.symbol.Sequence.from(l);
        }
    }

    public static Symbols symbols() { return new Symbols(); }

    public static class Symbol {

        public static org.iguana.grammar.symbol.Symbol star(org.iguana.grammar.symbol.Symbol symbol) {
            return Star.from(symbol);
        }
        public static org.iguana.grammar.symbol.Symbol plus(org.iguana.grammar.symbol.Symbol symbol) {
            return Plus.from(symbol);
        }
        public static org.iguana.grammar.symbol.Symbol option(org.iguana.grammar.symbol.Symbol symbol) {
            return Opt.from(symbol);
        }
        public static org.iguana.grammar.symbol.Symbol sequence(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> symbols) {
            List<org.iguana.grammar.symbol.Symbol> l = new ArrayList<>();
            l.add(symbol);
            addAll(l, symbols);
            return org.iguana.grammar.symbol.Sequence.from(l);
        }

        public static org.iguana.grammar.symbol.Symbol alternation(org.iguana.grammar.symbol.Sequence<org.iguana.grammar.symbol.Symbol> sequence, List<org.iguana.grammar.symbol.Sequence<org.iguana.grammar.symbol.Symbol>> sequences) {
            List<org.iguana.grammar.symbol.Symbol> l = new ArrayList<>();
            if (sequence.getSymbols().isEmpty())
                l.add(Terminal.epsilon());
            else if (sequence.getSymbols().size() == 1)
                l.add(sequence.getSymbols().get(0));
            else
                l.add(sequence);
            sequences.forEach(s -> {
                if (s.getSymbols().isEmpty())
                    l.add(Terminal.epsilon());
                else if (s.getSymbols().size() == 1)
                    l.add(s.getSymbols().get(0));
                else
                    l.add(s);
            });
            return Alt.from(l);
        }

        public static org.iguana.grammar.symbol.Symbol call(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.datadependent.ast.Expression> arguments) {
            Nonterminal nt = (Nonterminal) symbol;
            return nt.copyBuilder().apply(arguments.stream().toArray(org.iguana.datadependent.ast.Expression[]::new)).build();
        }
        public static org.iguana.grammar.symbol.Symbol align(org.iguana.grammar.symbol.Symbol symbol) {
            return Align.align(symbol);
        }
        public static org.iguana.grammar.symbol.Symbol offside(org.iguana.grammar.symbol.Symbol symbol) {
            return Offside.offside(symbol);
        }
        public static org.iguana.grammar.symbol.Symbol ignore(org.iguana.grammar.symbol.Symbol symbol) {
            return Ignore.ignore(symbol);
        }
        public static org.iguana.grammar.symbol.Symbol conditional(org.iguana.datadependent.ast.Expression expression, org.iguana.grammar.symbol.Symbol thenPart, org.iguana.grammar.symbol.Symbol elsePart) {
            return IfThenElse.ifThenElse(expression, thenPart, elsePart);
        }
        public static org.iguana.grammar.symbol.Symbol variable(Identifier name, org.iguana.grammar.symbol.Symbol symbol) {
            Nonterminal nt = (Nonterminal) symbol;
            return nt.copyBuilder().setVariable(name.id).build();
        }
        public static org.iguana.grammar.symbol.Symbol labeled(Identifier name, org.iguana.grammar.symbol.Symbol symbol) {
            return symbol.copyBuilder().setLabel(name.id).build();
        }
        public static org.iguana.grammar.symbol.Symbol constraints(List<org.iguana.datadependent.ast.Expression> expressions) {
            return new CodeHolder(null, expressions);
        }
        public static org.iguana.grammar.symbol.Symbol bindings(List<org.iguana.datadependent.ast.Statement> statements) {
            return new CodeHolder(statements, null);
        }
        public static org.iguana.grammar.symbol.Symbol precede(RegularExpression regex, org.iguana.grammar.symbol.Symbol symbol) {
            return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.precede(regex)).build();
        }
        public static org.iguana.grammar.symbol.Symbol notprecede(RegularExpression regex, org.iguana.grammar.symbol.Symbol symbol) {
            return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.notPrecede(regex)).build();
        }
        public static org.iguana.grammar.symbol.Symbol follow(org.iguana.grammar.symbol.Symbol symbol, RegularExpression regex) {
            return symbol.copyBuilder().addPostCondition(RegularExpressionCondition.follow(regex)).build();
        }
        public static org.iguana.grammar.symbol.Symbol notfollow(org.iguana.grammar.symbol.Symbol symbol, RegularExpression regex) {
            return symbol.copyBuilder().addPostCondition(RegularExpressionCondition.notFollow(regex)).build();
        }
        public static org.iguana.grammar.symbol.Symbol exclude(org.iguana.grammar.symbol.Symbol symbol, RegularExpression regex) {
            return symbol.copyBuilder().addPostCondition(RegularExpressionCondition.notMatch(regex)).build();
        }
        public static org.iguana.grammar.symbol.Symbol nont(Identifier name) {
            return Nonterminal.withName(name.id);
        }
        public static org.iguana.grammar.symbol.Symbol string(String s) {
            return Terminal.from(org.iguana.regex.Sequence.from(s.substring(1,s.length() - 1).chars().toArray()));
        }
        public static org.iguana.grammar.symbol.Symbol character(String s) {
            return Terminal.from(org.iguana.regex.Sequence.from(s.substring(1,s.length() - 1).chars().toArray()));
        }
    }

    public static Symbol symbol() { return new Symbol(); }

    public static class Expression {
        public static org.iguana.datadependent.ast.Expression call(org.iguana.datadependent.ast.Expression expression, List<org.iguana.datadependent.ast.Expression> arguments) {
            if (expression instanceof org.iguana.datadependent.ast.Expression.Name) {
                String name = ((org.iguana.datadependent.ast.Expression.Name) expression).getName();
                switch (name) {
                    case "col": return AST.indent(arguments.get(0));
                    default: // TODO: Add all the cases
                }
            }
            return null;
        }
        public static org.iguana.datadependent.ast.Expression multiplication(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return null;
        }
        public static org.iguana.datadependent.ast.Expression division(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return null;
        }
        public static org.iguana.datadependent.ast.Expression plus(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return null;
        }
        public static org.iguana.datadependent.ast.Expression minus(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return null;
        }
        public static org.iguana.datadependent.ast.Expression greatereq(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.greaterEq(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression lesseq(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.lessEq(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression greater(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.greater(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression less(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.less(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression equal(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.equal(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression notequal(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.notEqual(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression and(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.and(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression or(org.iguana.datadependent.ast.Expression lhs, org.iguana.datadependent.ast.Expression rhs) {
            return AST.or(lhs, rhs);
        }
        public static org.iguana.datadependent.ast.Expression lextent(Identifier name) {
            return AST.lExt(name.id);
        }
        public static org.iguana.datadependent.ast.Expression rextent(Identifier name) {
            return AST.rExt(name.id);
        }
        public static org.iguana.datadependent.ast.Expression yield(Identifier name) {
            return AST.yield(name.id);
        }
        public static org.iguana.datadependent.ast.Expression name(Identifier name) {
            return AST.var(name.id);
        }
        public static org.iguana.datadependent.ast.Expression number(String n) {
            return AST.integer(Integer.valueOf(n));
        }
    }

    public static Expression expression() { return new Expression(); }

    public static class ReturnExpression {
        public static org.iguana.grammar.symbol.Symbol returnexpression(org.iguana.datadependent.ast.Expression expression) {
            return Return.ret(expression);
        }
    }

    public static ReturnExpression returnexpression() { return new ReturnExpression(); }

    public static class Regex {
        public static RegularExpression star(RegularExpression regex) { return org.iguana.regex.Star.from(regex); }
        public static RegularExpression plus(RegularExpression regex) { return org.iguana.regex.Plus.from(regex); }
        public static RegularExpression option(RegularExpression regex) { return org.iguana.regex.Opt.from(regex); }
        public static RegularExpression bracket(RegularExpression regex) { return regex; }
        public static RegularExpression sequence(RegularExpression regex, List<RegularExpression> regexs) {
            List<org.iguana.regex.RegularExpression> l = new ArrayList<>();
            l.add(regex);
            l.addAll(regexs);
            return org.iguana.regex.Sequence.from(l);
        }
        public static RegularExpression alternation(org.iguana.regex.Sequence<org.iguana.regex.RegularExpression> sequence, List<org.iguana.regex.Sequence<org.iguana.regex.RegularExpression>> sequences) {
            List<org.iguana.regex.RegularExpression> l = new ArrayList<>();
            if (sequence.getSymbols().isEmpty())
                l.add(Epsilon.getInstance());
            else if (sequence.getSymbols().size() == 1)
                l.add(sequence.getSymbols().get(0));
            else
                l.add(sequence);
            sequences.forEach(s -> {
                if (s.getSymbols().isEmpty())
                    l.add(Epsilon.getInstance());
                else if (s.getSymbols().size() == 1)
                    l.add(s.getSymbols().get(0));
                else
                    l.add(s);
            });
            return org.iguana.regex.Alt.from(l);
        }
        // public static RegularExpression nont(Identifier name) { return Nonterminal.withName(name.id); }
        public static RegularExpression charclass(RegularExpression regex) { return regex; }
        public static RegularExpression string(String s) {
            return org.iguana.regex.Sequence.from(s.substring(1,s.length() - 1).chars().toArray());
        }
        public static RegularExpression character(String s) {
            return org.iguana.regex.Sequence.from(s.substring(1,s.length() - 1).chars().toArray());
        }
    }

    public static Regex regex() { return new Regex(); }

    public static class Regexs {
        public static org.iguana.regex.Sequence<org.iguana.regex.RegularExpression> sequence(List<RegularExpression> regexs) {
            return org.iguana.regex.Sequence.from(regexs.stream().map(regex -> (org.iguana.regex.RegularExpression)regex).collect(Collectors.toList()));
        }
    }

    public static Regexs regexs() { return new Regexs(); }

    public static class CharClass {
        public static RegularExpression chars(List<CharacterRange> ranges) {
            return org.iguana.regex.Alt.from(ranges);
        }
        public static RegularExpression notchars(List<CharacterRange> ranges) {
            return null;
        }
    }

    public static CharClass charclass() { return new CharClass(); }

    public static class Range {
        public static CharacterRange range(String from, String delimiter, String to) {
            return CharacterRange.in(from.charAt(0), to.charAt(0));
        }
        public static CharacterRange character(String s) {
            return CharacterRange.from(s.charAt(0));
        }
    }

    public static Range range() { return new Range(); }

    public static class Binding {
        public static org.iguana.datadependent.ast.Statement assign(Identifier name, org.iguana.datadependent.ast.Expression expression) {
            return AST.stat(AST.assign(name.id, expression));
        }
        public static org.iguana.datadependent.ast.Statement declare(Identifier name, org.iguana.datadependent.ast.Expression expression) {
            return AST.varDeclStat(name.id, expression);
        }
    }

    public static Binding binding() { return new Binding(); }

    // TODO: this should be a temporary trick!
    private static class CodeHolder implements org.iguana.grammar.symbol.Symbol {

        public final List<org.iguana.datadependent.ast.Statement> statements;
        public final List<org.iguana.datadependent.ast.Expression> expressions;

        public CodeHolder(List<org.iguana.datadependent.ast.Statement> statements, List<org.iguana.datadependent.ast.Expression> expressions) {
            this.statements = statements;
            this.expressions = expressions;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Set<Condition> getPreConditions() {
            return null;
        }

        @Override
        public Set<Condition> getPostConditions() {
            return null;
        }

        @Override
        public Object getObject() {
            return null;
        }

        @Override
        public String getLabel() {
            return null;
        }

        @Override
        public SymbolBuilder<? extends org.iguana.grammar.symbol.Symbol> copyBuilder() {
            return null;
        }

        @Override
        public String toString(int j) {
            return null;
        }

        @Override
        public <T> T accept(ISymbolVisitor<T> visitor) {
            return null;
        }

        @Override
        public ImmutableSet<String> getEnv() {
            return null;
        }

        @Override
        public void setEnv(ImmutableSet<String> env) {}

        @Override
        public void setEmpty() {}

    }

    private static void addAll(List<org.iguana.grammar.symbol.Symbol> symbols, List<org.iguana.grammar.symbol.Symbol> rest) {
        int i = 0;
        List<Condition> preConditions = new ArrayList<>();
        if (symbols.size() == 1 && symbols.get(0) instanceof CodeHolder) {
            CodeHolder holder = (CodeHolder) symbols.get(0);
            if (holder.expressions != null) {
                symbols.remove(0);
                preConditions = holder.expressions.stream().map(e -> DataDependentCondition.predicate(e)).collect(Collectors.toList());
                if (rest.isEmpty()) {
                    symbols.add(Terminal.epsilon().copyBuilder().addPreConditions(preConditions).build());
                    return;
                }
            }

        }
        for (org.iguana.grammar.symbol.Symbol symbol : rest) {
            if (symbol instanceof CodeHolder) {
                CodeHolder holder = (CodeHolder) symbol;
                if (holder.expressions != null) {
                    if (i != rest.size() - 1) {
                        for (org.iguana.datadependent.ast.Expression e : holder.expressions)
                            preConditions.add(DataDependentCondition.predicate(e));
                    } else {
                        org.iguana.grammar.symbol.Symbol last = symbols.remove(symbols.size() - 1);
                        symbols.add(last.copyBuilder().addPostConditions(holder.expressions.stream()
                                .map(e -> DataDependentCondition.predicate(e)).collect(Collectors.toList())).build());
                    }
                } else if (holder.statements != null) {
                    org.iguana.grammar.symbol.Symbol last = symbols.remove(symbols.size() - 1);
                    symbols.add(Code.code(last, holder.statements.stream().toArray(org.iguana.datadependent.ast.Statement[]::new)));
                }
            } else {
                if (preConditions.isEmpty())
                    symbols.add(symbol);
                else {
                    symbols.add(symbol.copyBuilder().addPreConditions(preConditions).build());
                    preConditions = new ArrayList<>();
                }
            }
            i++;
        }
    }

}
