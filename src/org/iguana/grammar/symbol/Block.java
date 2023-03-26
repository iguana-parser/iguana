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
import java.util.List;

import static org.iguana.utils.string.StringUtil.listToString;

public class Block extends AbstractSymbol {

    private final List<Symbol> symbols;

    Block(Builder builder) {
        super(builder);
        this.symbols = builder.symbols;
    }

    public static Block block(Symbol... symbols) {
        return new Builder(symbols).build();
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public Builder copy() {
        return new Builder(this);
    }

    @Override
    public List<Symbol> getChildren() {
        return symbols;
    }

    @Override
    public int size() {
        int size = 0;
        for (Symbol symbol: symbols)
            size = size + symbol.size();
        return size;
    }

    @Override
    public String toString() {
        return String.format("{ %s }", listToString(symbols, " "));
    }

    @Override
    public String toString(int j) {
        List<String> strings = new ArrayList<>(symbols.size());

        int k = 0;
        for (Symbol symbol : symbols) {
            strings.set(k, j <= 1? symbol.toString(j) : symbol.toString());
            j = j - symbol.size();
            k++;
        }

        return String.format("{ %s }", listToString(strings, " "));
    }

    public static class Builder extends SymbolBuilder<Block> {

        private List<Symbol> symbols;

        public Builder(Block block) {
            super(block);
            this.symbols = block.symbols;
        }

        public Builder(Symbol... symbols) {
            assert symbols.length != 0;

            this.symbols = Arrays.asList(symbols);
        }

        @Override
        public SymbolBuilder<Block> setChildren(List<Symbol> symbols) {
            this.symbols = new ArrayList<>(symbols);
            return this;
        }

        @Override
        public Block build() {
            this.name = String.format("{ %s }", listToString(symbols, " "));
            return new Block(this);
        }

    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
