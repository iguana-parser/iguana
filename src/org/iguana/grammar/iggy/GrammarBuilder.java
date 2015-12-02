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

import org.iguana.grammar.symbol.Symbol;

import java.util.List;

/**
 * @author Anastasia Izmaylova
 */
public class GrammarBuilder {
	
	public static class Rule {
		public static List<org.iguana.grammar.symbol.Rule> syntax(List<String> tag, Ident name, List<Ident> parameters, List<Alternates> body) {
			return null; 
		}
		public static List<org.iguana.grammar.symbol.Rule> regex() {
			return null;
		}
        public static List<org.iguana.grammar.symbol.Rule> regexs() {
            return null;
        }
	}
	
	public static Rule rule() { return new Rule(); }

    public static class Identifier {
        public static Ident id(String name) {
            return new Ident(name);
        }
    }

    public static Identifier identifier() {return new Identifier(); }

    /*
     * Wrappers
     */
    public static class Ident {
        public final String id;
        public Ident(String id) {
            this.id = id;
        }
    }

    public static class Alternates {
        public final List<Alternate> alternates;
        public Alternates(List<Alternate> alternates) {
            this.alternates = alternates;
        }
    }

    public static abstract class Alternate {

        public final Sequence first;
        public final List<Sequence> rest;
        public final String associativity;

        public Alternate(Sequence sequence) {
            this.first = sequence;
            this.rest = null;
            this.associativity = null;
        }
        public Alternate(Sequence sequence, List<Sequence> sequences, String associativity) {
            this.first = sequence;
            this.rest = sequences;
            this.associativity = associativity;
        }
    }

    public static class Sequence {

        public final Symbol first;
        public final List<Symbol> rest;
        public final List<Symbol> ret;
        public final List<Attribute> attributes;
        public final List<String> label;

        public Sequence(Symbol symbol, List<Symbol> ret, List<String> label) {
            this.first = symbol;
            this.rest = null;
            this.ret = ret;
            this.attributes = null;
            this.label = label;
        }

        public Sequence(Symbol symbol, List<Symbol> symbols, List<Symbol> ret, List<Attribute> attributes) {
            this.first = symbol;
            this.rest = symbols;
            this.ret = ret;
            this.attributes = attributes;
            this.label = null;
        }

    }

    public static class Attribute {
        public final String attribute;
        public Attribute(String attribute) {
            this.attribute = attribute;
        }
    }

    public static class Symbols {
        public final List<Symbol> symbols;
        public Symbols(List<Symbol> symbols) {
            this.symbols = symbols;
        }
    }

}
