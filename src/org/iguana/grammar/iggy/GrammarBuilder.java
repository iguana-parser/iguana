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

import org.iguana.grammar.symbol.Nonterminal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anastasia Izmaylova
 */
public class GrammarBuilder {

    public static class Identifier {
        public final String id;
        public Identifier(String id) {
            this.id = id;
        }
        public static Identifier id(String name) {
            return new Identifier(name);
        }
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
                                symbols.addAll(alternate.first.rest);
                            symbols.addAll(alternate.first.ret);
                            rules.add(builder.addSymbols().build());
                        }
                        alternate.rest.forEach(sequence -> {
                            org.iguana.grammar.symbol.Rule.Builder builder = org.iguana.grammar.symbol.Rule.withHead(Nonterminal.withName(name.id));
                            List<org.iguana.grammar.symbol.Symbol> symbols = new ArrayList<>();
                            symbols.add(sequence.first);
                            if (sequence.rest != null)
                                symbols.addAll(sequence.rest);
                            symbols.addAll(sequence.ret);
                            rules.add(builder.addSymbols(symbols).build());
                        });
                    } else {
                        org.iguana.grammar.symbol.Rule.Builder builder = org.iguana.grammar.symbol.Rule.withHead(Nonterminal.withName(name.id));
                        List<org.iguana.grammar.symbol.Symbol> symbols = new ArrayList<>();
                        symbols.add(alternate.first.first);
                        if (alternate.first.rest != null)
                            symbols.addAll(alternate.first.rest);
                        symbols.addAll(alternate.first.ret);
                        rules.add(builder.addSymbols().build());
                    }
                });
            });
			return rules;
		}
		public static List<org.iguana.grammar.symbol.Rule> regex() {
			return null;
		}
        public static List<org.iguana.grammar.symbol.Rule> regexs() {
            return null;
        }
	}
	
	public static Rule rule() { return new Rule(); }

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

        public static Alternate sequence(Sequence sequence) { return new Alternate(sequence); }
        public static Alternate assoc(Sequence sequence, List<Sequence> sequences, String associativity) {
            return new Alternate(sequence, sequences, associativity);
        }
    }

    public static Alternate alternate() { return new Alternate(null); }

    public static class Sequence {

        public final org.iguana.grammar.symbol.Symbol first;
        public final List<org.iguana.grammar.symbol.Symbol> rest;
        public final List<org.iguana.grammar.symbol.Symbol> ret;
        public final List<Attribute> attributes;
        public final List<String> label;

        public Sequence(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> ret, List<String> label) {
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

        public static Sequence single(org.iguana.grammar.symbol.Symbol symbol, List<org.iguana.grammar.symbol.Symbol> ret, List<String> label) {
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

    public static Attribute attribute() { return new Attribute(null); }

    public static class Symbols {
        public static org.iguana.regex.Sequence<org.iguana.grammar.symbol.Symbol> sequence(List<org.iguana.grammar.symbol.Symbol> symbols) { return org.iguana.regex.Sequence.from(symbols); }
    }

    public static Symbols symbols() { return new Symbols(); }



}
