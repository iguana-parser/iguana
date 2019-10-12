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
package org.iguana.iggy;

import iguana.regex.Char;
import iguana.regex.*;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.exception.GrammarValidationException;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Group;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.transformation.GrammarTransformation;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Anastasia Izmaylova on 06/12/15.
 */
public class InlineRegex implements GrammarTransformation, ISymbolVisitor<Symbol>, RegularExpressionVisitor<RegularExpression> {

    private final Map<String, RegularExpression> definitions = new HashMap<>();
    private String head;

    @Override
    public Grammar transform(Grammar grammar) {

        List<Rule> rules = new ArrayList<>();

        for (Rule rule : grammar.getRules()) {
            if (rule.getAttributes().containsKey("regex")) {
                if (definitions.containsKey(rule.getHead().getName()))
                    throw new GrammarValidationException(new HashSet<>(Arrays.asList(new RuntimeException("Should not have happened."))));
                definitions.put(rule.getHead().getName(), ((Terminal) rule.getBody().get(0)).getRegularExpression());
            } else
                rules.add(rule);
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, RegularExpression> entry : definitions.entrySet()) {
                head = entry.getKey();
                RegularExpression regex = entry.getValue().accept(this);
                definitions.put(entry.getKey(), regex);
                changed |= regex != entry.getValue();
            }
        }
        head = "";

        List<Rule> newRules = new ArrayList<>();
        for (Rule rule : rules) {
            newRules.add(rule.copyBuilder().setSymbols(rule.getBody().stream().map(s -> visitSym(s)).collect(Collectors.toList())).build());
        }
        return Grammar.builder()
                .addRules(newRules)
                // TODO: check other attributes
                .setLayout(grammar.getLayout())
                .build();
    }

    @Override
    public Symbol visit(Align symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Align.align(sym);
    }

    @Override
    public Symbol visit(Block symbol) {
        return null;
    }

    @Override
    public RegularExpression visit(Char symbol) {
        return symbol;
    }

    @Override
    public RegularExpression visit(CharRange symbol) {
        return symbol;
    }

    @Override
    public Symbol visit(Code symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Code.code(sym, symbol.getStatements());
    }

    @Override
    public Symbol visit(Conditional symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Conditional.when(sym, symbol.getExpression());
    }

    @Override
    public RegularExpression visit(EOF symbol) {
        return symbol;
    }

    @Override
    public RegularExpression visit(Epsilon symbol) {
        return symbol;
    }

    @Override
    public Symbol visit(IfThen symbol) {
        Symbol sym = visitSym(symbol.getThenPart());
        if (sym == symbol.getThenPart())
            return symbol;
        return IfThen.ifThen(symbol.getExpression(), sym);
    }

    @Override
    public Symbol visit(IfThenElse symbol) {
        Symbol sym1 = visitSym(symbol.getThenPart());
        Symbol sym2 = visitSym(symbol.getElsePart());
        if (sym1 == symbol.getThenPart() && sym2 == symbol.getElsePart())
            return symbol;
        return IfThenElse.ifThenElse(symbol.getExpression(), sym1, sym2);
    }

    @Override
    public Symbol visit(Ignore symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Ignore.ignore(sym);
    }

    @Override
    public Symbol visit(Nonterminal symbol) {
        if (symbol.getName().equals(head))
            throw new GrammarValidationException(new HashSet<>(Arrays.asList(new RuntimeException("Regex definitions must not be recursive: " + symbol.getName()))));
        if (definitions.containsKey(symbol.getName())) {
            return Terminal.builder(definitions.get(symbol.getName()))
                    .setNodeType(TerminalNodeType.Keyword)
                    .setName(symbol.getName())
                    .build();
        }
        return symbol;
    }

    @Override
    public Symbol visit(Offside symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Offside.offside(sym);
    }

    @Override
    public Symbol visit(Terminal symbol) {
        return symbol;
    }

    @Override
    public Symbol visit(While symbol) {
        return null;
    }

    @Override
    public Symbol visit(Return symbol) {
        return symbol;
    }

    @Override
    public <E extends Symbol> Symbol visit(Alt<E> symbol) {
        boolean changed = false;
        List<Symbol> syms = new ArrayList<>();
        for (Symbol s : symbol.getSymbols()) {
            Symbol sym = visitSym(s);
            changed |= sym != s;
            syms.add(sym);
        }
        if (!changed)
            return symbol;
        return Alt.from(syms);
    }

    @Override
    public Symbol visit(Opt symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Opt.from(sym);
    }

    @Override
    public Symbol visit(Plus symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Plus.from(sym);
    }

    @Override
    public <E extends Symbol> Symbol visit(Group<E> symbol) {
        boolean changed = false;
        List<Symbol> syms = new ArrayList<>();
        for (Symbol s : symbol.getSymbols()) {
            Symbol sym = visitSym(s);
            changed |= sym != s;
            syms.add(sym);
        }
        if (!changed)
            return symbol;
        return Group.from(syms);
    }

    @Override
    public Symbol visit(Star symbol) {
        Symbol sym = visitSym(symbol.getSymbol());
        if (sym == symbol.getSymbol())
            return symbol;
        return Star.from(sym);
    }

    @Override
    public Symbol visit(Start start) {
        Symbol sym = visitSym(start.getNonterminal());
        if (sym == start.getNonterminal())
            return start;
        return Start.from((Nonterminal) sym);
    }

    @Override
    public RegularExpression visit(iguana.regex.Star s) {
        RegularExpression sym = s.getSymbol().accept(this);
        if (sym == s.getSymbol())
            return s;
        return iguana.regex.Star.from(sym);
    }

    @Override
    public RegularExpression visit(iguana.regex.Plus p) {
        RegularExpression sym = p.getSymbol().accept(this);
        if (sym == p.getSymbol())
            return p;
        return iguana.regex.Plus.from(sym);
    }

    @Override
    public RegularExpression visit(iguana.regex.Opt o) {
        RegularExpression sym = o.getSymbol().accept(this);
        if (sym == o.getSymbol())
            return o;
        return iguana.regex.Opt.from(sym);
    }

    @Override
    public <E extends RegularExpression> RegularExpression visit(iguana.regex.Alt<E> alt) {
        boolean changed = false;
        List<RegularExpression> syms = new ArrayList<>();
        for (RegularExpression s : alt.getSymbols()) {
            RegularExpression sym = s.accept(this);
            changed |= sym != s;
            syms.add(sym);
        }
        if (!changed)
            return alt;
        return iguana.regex.Alt.from(syms);
    }

    @Override
    public <E extends RegularExpression> RegularExpression visit(iguana.regex.Seq<E> seq) {
        boolean changed = false;
        List<RegularExpression> syms = new ArrayList<>();
        for (RegularExpression s : seq.getSymbols()) {
            RegularExpression sym = s.accept(this);
            changed |= sym != s;
            syms.add(sym);
        }
        if (!changed)
            return seq;
        return iguana.regex.Seq.from(syms);
    }

    @Override
    public RegularExpression visit(Reference ref) {
        return null;
    }

    private Symbol visitSym(Symbol symbol) {
        return symbol.accept(this).copyBuilder()
                .addPreConditions(symbol.getPreConditions())
                .addPostConditions(symbol.getPostConditions())
                .setLabel(symbol.getLabel())
                .build();
    }
}
