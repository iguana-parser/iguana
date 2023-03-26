
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

package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Alt extends AbstractSymbol {

    protected final List<Symbol> symbols;

    public Alt(Builder builder) {
        super(builder);
        this.symbols = builder.symbols;
    }

    public static Alt from(Symbol... symbols) {
        return from(Arrays.asList(symbols));
    }

    public static Alt from(List<Symbol> list) {
        return new Builder(list).build();
    }

    public int size() {
        return symbols.size();
    }

    public Symbol get(int index) {
        return symbols.get(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Alt))
            return false;

        Alt other = (Alt) obj;

        return other.symbols.equals(symbols);
    }

    @Override
    public int hashCode() {
        return symbols.hashCode();
    }

    @Override
    public Builder copy() {
        return new Builder(this);
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public List<Symbol> getChildren() {
        return symbols;
    }

    public static class Builder extends SymbolBuilder<Alt> {

        private List<Symbol> symbols;

        private Builder() {}

        public Builder(Symbol... symbols) {
            this(Arrays.asList(symbols));
        }

        public Builder(List<Symbol> symbols) {
            this.symbols = symbols;
        }

        public Builder(Alt alt) {
            super(alt);
            this.symbols = alt.getSymbols();
        }

        public Builder add(Symbol symbol) {
            symbols.add(symbol);
            return this;
        }

        public Builder add(List<Symbol> l) {
            symbols.addAll(l);
            return this;
        }

        @Override
        public SymbolBuilder<Alt> setChildren(List<Symbol> symbols) {
            this.symbols = symbols;
            return this;
        }

        @Override
        public Alt build() {
            this.name = "(" + symbols.stream().map(Symbol::getName).collect(Collectors.joining(" | ")) + ")";
            return new Alt(this);
        }
    }

    @Override
    public <E> E accept(ISymbolVisitor<E> visitor) {
        return visitor.visit(this);
    }

}
