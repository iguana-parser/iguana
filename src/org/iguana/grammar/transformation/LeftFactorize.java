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

package org.iguana.grammar.transformation;

import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.regex.Alt;
import org.iguana.regex.Sequence;
import org.iguana.util.trie.Edge;
import org.iguana.util.trie.Node;
import org.iguana.util.trie.Trie;

public class LeftFactorize implements GrammarTransformation {
	
	@Override
	public Grammar transform(Grammar grammar) {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
		
			Trie<Symbol> trie = new Trie<>();

			Node<Symbol> node = trie.getRoot();
			for (Rule rule : grammar.getAlternatives(nonterminal)) {
				List<Symbol> alternative = rule.getBody();
				node = trie.getRoot();
				for (Symbol s : alternative) {
					node = trie.add(node, s);
				}
				trie.add(node, Epsilon.getInstance());
			}

			builder.addRule(Rule.withHead(nonterminal).addSymbols(retrieve(trie.getRoot())).build());
		}
		
		return builder.build();
	}
	
	private static Symbol retrieve(Node<Symbol> node) {
		
		if (node.size() == 0) return null;
		
		if (node.size() == 1 && node.getEdges().get(0).getLabel() == Epsilon.getInstance()) return null;

		List<Symbol> outer = new ArrayList<>();
		
		for (Edge<Symbol> edge : node.getEdges()) {
			List<Symbol> inner = new ArrayList<>();
			inner.add(edge.getLabel());
			Symbol next = retrieve(edge.getDestination());
			if (next != null) {
				inner.add(next);
			}
			if (inner.size() == 1){
				outer.add(inner.get(0));
			} else {
				outer.add(Sequence.from(inner));
			}
		}
		if (outer.size() == 1){
			return outer.get(0);
		} else {
			return Alt.from(outer);
		}
	}
	
}
