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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.iguana.utils.string.StringUtil.listToString;

public class Plus extends AbstractSymbol {

    private final Symbol s;

    private final List<Symbol> separators;

    public static Plus from(Symbol s) {
        return new Builder(s).build();
    }

    private Plus(Builder builder) {
        super(builder);
        this.s = builder.s;
        this.separators = Collections.unmodifiableList(builder.separators);
    }

    public List<Symbol> getSeparators() {
        return separators;
    }

    @Override
    public SymbolBuilder<Plus> copy() {
        return new Builder(this);
    }

    @Override
    public List<Symbol> getChildren() {
        List<Symbol> children = new ArrayList<>();
        children.add(s);
        children.addAll(separators);
        return children;
    }

    public Symbol getSymbol() {
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Plus))
            return false;

        Plus other = (Plus) obj;
        return s.equals(other.s) && separators.equals(other.separators);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static class Builder extends SymbolBuilder<Plus> {

        private Symbol s;

        private List<Symbol> separators = new ArrayList<>();

        private Builder() {}

        public Builder(Symbol s) {
            this.s = s;
        }

        public Builder(Plus plus) {
            super(plus);
            this.s = plus.s;
            this.addSeparators(plus.getSeparators());
        }

        public Builder addSeparator(Symbol symbol) {
            separators.add(symbol);
            return this;
        }

        public Builder addSeparators(List<Symbol> symbols) {
            separators.addAll(symbols);
            return this;
        }

        public Builder addSeparators(Symbol...symbols) {
            separators.addAll(Arrays.asList(symbols));
            return this;
        }

        @Override
        public SymbolBuilder<Plus> setChildren(List<Symbol> symbols) {
            this.s = symbols.get(0);
            this.separators = symbols.subList(1, symbols.size());
            return this;
        }

        @Override
        public Plus build() {
            if (separators.isEmpty()) {
                this.name = s.getName() + "+";
            } else {
                this.name = String.format("{%s %s}+", s.getName(), listToString(separators));
            }
            return new Plus(this);
        }
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
