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

package org.iguana.traversal.idea;

import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Error;
import org.iguana.grammar.transformation.GrammarTransformation;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;

/**
 * Created by Anastasia Izmaylova on 20/10/15.
 */

// <Name>@0 => <Name>$Declaration ::= Name
// <Name>@1 => <Name>$Reference ::= Name
public class Names implements GrammarTransformation {

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        List<RuntimeRule> rules = new ArrayList<>();

        NameVisitor visitor = new NameVisitor(rules, (Nonterminal) grammar.getLayout());

        for (RuntimeRule rule : grammar.getRules())
            rules.add(visitor.visitRule(rule));

        return RuntimeGrammar.builder()
                .addRules(rules)
                .addEBNFl(grammar.getEBNFLefts())
                .addEBNFr(grammar.getEBNFRights())
                .setLayout(grammar.getLayout())
                .build();
    }

    private static class NameVisitor implements ISymbolVisitor<Symbol> {

        final List<RuntimeRule> rules;
        private Set<Nonterminal> heads = new HashSet<>();

        private final Nonterminal layout;

        NameVisitor(List<RuntimeRule> rules, Nonterminal layout) {
            this.rules = rules;
            this.layout = layout;
        }

        public RuntimeRule visitRule(RuntimeRule rule) {
            RuntimeRule.Builder builder = rule.copy();
            List<Symbol> symbols = new ArrayList<>();
            for (Symbol symbol : rule.getBody())
                symbols.add(visitSymbol(symbol));
            return builder.setSymbols(symbols).build();
        }

        public Symbol visitSymbol(Symbol symbol) {
            SymbolBuilder<? extends Symbol> builder = symbol.accept(this).copy();
            builder.addPreConditions(symbol.getPreConditions());
            builder.addPostConditions(symbol.getPostConditions());
            builder.setLabel(symbol.getLabel());
            return builder.build();
        }

        @Override
        public Symbol visit(Align symbol) {
            return Align.align(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Block block) {
            return Block.block(block.getSymbols().stream().map(this::visitSymbol).toArray(Symbol[]::new));
        }

        @Override
        public Symbol visit(Code symbol) {
            return Code.code(visitSymbol(symbol.getSymbol()), symbol.getStatements());
        }

        @Override
        public Symbol visit(Error error) {
            return error;
        }

        @Override
        public Symbol visit(Conditional symbol) {
            return Conditional.when(visitSymbol(symbol.getSymbol()), symbol.getExpression());
        }

        @Override
        public Symbol visit(IfThen symbol) {
            return IfThen.ifThen(symbol.getExpression(), visitSymbol(symbol.getThenPart()));
        }

        @Override
        public Symbol visit(IfThenElse symbol) {
            return IfThenElse.ifThenElse(symbol.getExpression(), visitSymbol(symbol.getThenPart()), visitSymbol(symbol.getElsePart()));
        }

        @Override
        public Symbol visit(Ignore symbol) {
            return Ignore.ignore(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Nonterminal symbol) {
            Map<String, Object> attributes = symbol.getAttributes();
            if (attributes.containsKey("name")) {
                int value = (Integer) attributes.get("name");
                Nonterminal sym = symbol;
                switch (value) {
                    case 0:
                        sym = Nonterminal.withName(symbol.getName() + "$Declaration");
                        if (!heads.contains(sym)) {
                            heads.add(sym);
                            rules.add(RuntimeRule.withHead(sym).addSymbol(symbol)
                                        .setLayout(layout).setLayoutStrategy(LayoutStrategy.INHERITED)
                                        .setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
                                        .setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                                        .build());
                        }
                        return sym;
                    case 1:
                        sym = Nonterminal.withName(symbol.getName() + "$Reference");
                        if (!heads.contains(sym)) {
                            heads.add(sym);
                            rules.add(RuntimeRule.withHead(sym).addSymbol(symbol)
                                        .setLayout(layout).setLayoutStrategy(LayoutStrategy.INHERITED)
                                        .setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
                                        .setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                                        .build());
                        }
                        return sym;
                    default:
                        break;
                }
            }
            return symbol;
        }

        @Override
        public Symbol visit(Offside symbol) {
            return Offside.offside(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Terminal symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(While symbol) {
            return While.whileLoop(symbol.getExpression(), visitSymbol(symbol.getBody()));
        }

        @Override
        public Symbol visit(Return symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(Alt symbol) {
            return Alt.from(symbol.getSymbols().stream().map(s -> visitSymbol(s)).toArray(Symbol[]::new));
        }

        @Override
        public Symbol visit(Opt symbol) {
            return Opt.from(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Plus symbol) {
            return new Plus.Builder(visitSymbol(symbol.getSymbol())).addSeparators(symbol.getSeparators()).build();
        }

        @Override
        public Symbol visit(Group symbol) {
            return Group.from(symbol.getSymbols().stream().map(s -> visitSymbol(s)).toArray(Symbol[]::new));
        }

        @Override
        public Symbol visit(Star symbol) {
            return new Star.Builder(visitSymbol(symbol.getSymbol())).addSeparators(symbol.getSeparators()).build();
        }

        @Override
        public Symbol visit(Start start) {
            return start;
//            return Start.from((Nonterminal) start.getNonterminal().accept(this));
        }
    }
}
