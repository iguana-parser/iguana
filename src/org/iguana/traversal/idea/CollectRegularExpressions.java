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

import iguana.regex.RegularExpression;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Map;

/**
 * Created by Anastasia Izmaylova on 17/12/15.
 */

/*
 * Collects regular expressions by type: named, keywords, the rest.
 */
public class CollectRegularExpressions implements ISymbolVisitor<Void> {

    private final Map<String, RegularExpression> terminals;

    public CollectRegularExpressions(Map<String, RegularExpression> terminals) {
        this.terminals = terminals;
    }

    public void collect(RuntimeGrammar grammar) {
        for (RuntimeRule rule : grammar.getRules()) {
            for (Symbol symbol : rule.getBody())
                symbol.accept(this);
        }

        if (grammar.getLayout() instanceof Terminal)
            grammar.getLayout().accept(this);
    }

    @Override
    public Void visit(Align symbol) {
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Block symbol) {
        for (Symbol sym : symbol.getSymbols())
            sym.accept(this);
        return null;
    }

    @Override
    public Void visit(Code symbol) {
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Conditional symbol) {
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(IfThen symbol) {
        return symbol.getThenPart().accept(this);
    }

    @Override
    public Void visit(IfThenElse symbol) {
        symbol.getThenPart().accept(this);
        return symbol.getElsePart().accept(this);
    }

    @Override
    public Void visit(Ignore symbol) {
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Nonterminal symbol) {
        return null;
    }

    @Override
    public Void visit(Offside symbol) {
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Terminal symbol) {
        RegularExpression regex = symbol.getRegularExpression();

        if (symbol.getNodeType() == TerminalNodeType.Regex) {
            terminals.put("|regex|:" + symbol.getName(), regex);
        } else if (symbol.getNodeType() == TerminalNodeType.Keyword)
            terminals.put("|keyword|:" + symbol.getName(), regex);
        else
            terminals.put(symbol.getName(), regex);

        return null;
    }

    @Override
    public Void visit(While symbol) {
        return symbol.getBody().accept(this);
    }

    @Override
    public Void visit(Return symbol) {
        return null;
    }

    @Override
    public Void visit(Alt symbol) {
        for (Symbol sym : symbol.getSymbols())
            sym.accept(this);
        return null;
    }

    @Override
    public Void visit(Opt symbol) {
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Plus symbol) {
        for (Symbol sep : symbol.getSeparators())
            sep.accept(this);
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Group symbol) {
        for (Symbol sym : symbol.getSymbols())
            sym.accept(this);
        return null;
    }

    @Override
    public Void visit(Star symbol) {
        for (Symbol sep : symbol.getSeparators())
            sep.accept(this);
        return symbol.getSymbol().accept(this);
    }

    @Override
    public Void visit(Start start) {
        return null;
//        return start.getNonterminal().accept(this);
    }
}
