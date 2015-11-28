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

import java.util.ArrayList;
import java.util.List;




import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;

/**
 * @author Anastasia Izmaylova
 */
public abstract class Builder implements iguana.parsetrees.iggy.Builder {
	
	@Override
	public Grammar grammar(List<Object> rules) {
		org.iguana.grammar.Grammar.Builder builder = Grammar.builder();
		rules.forEach(rule -> builder.addRule((Rule) rule));
		return builder.build();
	}
	
	@Override
	public List<Rule> rule(List<Object> tag, Object name, List<Object> params, List<Object> body) {
		// TODO: Add the logic of handling precedence
		List<Rule> rules = new ArrayList<>();
		body.forEach(elem -> {
			PrecG g = (PrecG) elem;
			g.alts.forEach(alt -> {
				Nonterminal head = Nonterminal.withName((String) name);
				head = Nonterminal.builder(head).addParameters(params.stream().map(p -> (String)p).toArray(String[]::new)).build();
				org.iguana.grammar.symbol.Rule.Builder builder = Rule.withHead(head);
				if (alt instanceof Sequence)
					builder.addSymbols(((Sequence)alt).syms);
				else if (alt instanceof AssocG) {
					((AssocG)alt).seqs.forEach(seq -> {
						builder.addSymbols(seq.syms);
					});
				}
				rules.add(builder.build());
			});
		});
		return rules;
	}
	
	@Override
	public Object rule(Object name, Object body) {
		// TODO:
		return null;
	}
	
	@Override
	public Object precG(List<Object> alts) {
		return new PrecG(alts);
	}
	
	@Override
	public Object assocG(List<Object> elems) {
		List<Sequence> seqs = new ArrayList<>();
		List<String> assoc = new ArrayList<>();
		elems.forEach(elem -> {
			if (elem instanceof Sequence)
				seqs.add((Sequence) elem);
			else if (elem instanceof String)
				assoc.add((String) elem);
		});
		return new AssocG(seqs, assoc.get(0));
	}
	
	@Override
	public Sequence body(List<Object> elems) {
		List<Symbol> syms = new ArrayList<>();
		List<String> attrs = new ArrayList<>();
		elems.forEach(elem -> {
			if (elem instanceof Symbol)
				syms.add((Symbol) elem);
			else if (elem instanceof Expression)
				syms.add(Return.ret((Expression) elem));
			else if (elem instanceof String)
				attrs.add((String) elem);
		});
		return new Sequence(syms, attrs);
	}
	
	
	/*
	 *  Helper classes
	 */
	
	public static class PrecG {
		
		public final List<Object> alts;
		
		public PrecG(List<Object> alts) {
			this.alts = alts;
		}
	}
	
	public static class AssocG {
		
		public final List<Sequence> seqs;
		public final String assoc;
		
		public AssocG(List<Sequence> seqs, String assoc) {
			this.seqs = seqs;
			this.assoc = assoc;
		}
	}
	
	public static class Sequence {
		
		public final List<Symbol> syms;
		public final List<String> attrs;
		
		public Sequence(List<Symbol> syms, List<String> attrs) {
			this.syms = syms;
			this.attrs = attrs;
		}
	}

}
