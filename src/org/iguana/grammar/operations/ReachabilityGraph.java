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

package org.iguana.grammar.operations;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class ReachabilityGraph {

	private final Map<Nonterminal, List<RuntimeRule>> definitions;
	
	private final Map<Nonterminal, Set<Nonterminal>> reachabilityGraph;
	
	private final Set<String> layouts;
	
	public ReachabilityGraph(RuntimeGrammar grammar) {
		this.reachabilityGraph = new HashMap<>();
		this.definitions = grammar.getDefinitions();
		
		this.layouts = new HashSet<>();
		
		if (grammar.getLayout() != null)
			layouts.add(grammar.getLayout().getName());
		
		grammar.getNonterminals().forEach(n -> reachabilityGraph.put(n, new HashSet<>()));
		
		layouts.addAll(grammar.getRules().stream()
						                 .filter(r -> r.getLayout() != null)
                                         .map(r -> r.getLayout().getName())
						                 .collect(Collectors.toSet()));
		
		calculateReachabilityGraph();
	}
	
	public Map<Nonterminal, Set<Nonterminal>> getReachabilityGraph() {
		return reachabilityGraph;
	}
	
	public Set<Nonterminal> getReachableNonterminals(Nonterminal nt) {
		return reachabilityGraph.get(nt);
	}

	/*
	 * 
	 * Calculate the set of nonterminals that are reachable via the alternates of A.
	 * In other words, if A is a nonterminal, reachable nonterminals are all the B's such as
	 * A =&gt;* alpha B gamma. Note that this method does not calculate direct-nullable reachable
	 * nonterminals.
	 * 
	 */
	public Map<Nonterminal, Set<Nonterminal>> calculateReachabilityGraph() {
		
		Visitor visitor = new Visitor(reachabilityGraph, layouts);
		
		Set<Nonterminal> nonterminals = definitions.keySet();
		
		boolean changed = true;
		
		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {
				
				// Skips layout
				if (layouts.contains(head.getName()))
					continue;
				
				visitor.setHead(head);
				
				for (RuntimeRule rule : definitions.get(head)) {
					
					List<Symbol> alternate = rule.getBody();
					
					if (alternate == null)
						continue;
					
					for (Symbol symbol : alternate)
						changed |= symbol.accept(visitor);
					
				}
			}
		}
		
		return reachabilityGraph;
	}
	
	private static class Visitor implements ISymbolVisitor<Boolean> {
		
		private final Map<Nonterminal, Set<Nonterminal>> reachabilityGraph;
		private final Set<String> layouts;
		
		private Nonterminal head;
		
		public Visitor(Map<Nonterminal, Set<Nonterminal>> reachabilityGraph, Set<String> layouts) {
			this.reachabilityGraph = reachabilityGraph;
			this.layouts = layouts;
		}
		
		public void setHead(Nonterminal head) {
			this.head = head;
		}

		@Override
		public Boolean visit(Align symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Boolean visit(Block symbol) {
			boolean changed = false;
			for (Symbol s : symbol.getSymbols())
				changed |= s.accept(this);
			return changed;
		}

		@Override
		public Boolean visit(Code symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Boolean visit(Conditional symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Boolean visit(IfThen symbol) {
			return symbol.getThenPart().accept(this);
		}

		@Override
		public Boolean visit(IfThenElse symbol) {
			return symbol.getThenPart().accept(this) || symbol.getElsePart().accept(this);
		}
		
		@Override
		public Boolean visit(Ignore symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Boolean visit(Nonterminal symbol) {
			// Skips layout
			if (layouts.contains(symbol.getName()))
				return false;
			return add(head, symbol, reachabilityGraph);
		}

		@Override
		public Boolean visit(Offside symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Boolean visit(Terminal symbol) {
			return false;
		}

		@Override
		public Boolean visit(While symbol) {
			return symbol.getBody().accept(this);
		}
		
		@Override
		public Boolean visit(Return symbol) {
			return false;
		}

		@Override
		public Boolean visit(Alt symbol) {
			boolean changed = false;
			for (Symbol s : symbol.getSymbols())
				changed |= s.accept(this);
			return changed;
		}

		@Override
		public Boolean visit(Opt symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Boolean visit(Plus symbol) {
			boolean changed = symbol.getSymbol().accept(this);
			for (Symbol sep : symbol.getSeparators())
				changed |= sep.accept(this);
			return changed;
		}

		@Override
		public Boolean visit(Group symbol) {
			boolean changed = false;
			for (Symbol s : symbol.getSymbols())
				changed |= s.accept(this);
			return changed;
		}

		@Override
		public Boolean visit(Star symbol) {
			boolean changed = symbol.getSymbol().accept(this);
			for (Symbol sep : symbol.getSeparators())
				changed |= sep.accept(this);
			return changed;
		}

        @Override
        public Boolean visit(Start start) {
//            return start.getNonterminal().accept(this);
			return false;
        }

    }
	
	private static boolean add(Nonterminal a, Nonterminal nonterminal, Map<Nonterminal, Set<Nonterminal>> reachabilityGraph) {
		boolean changed = false;
		changed |= reachabilityGraph.get(a).add(nonterminal);
		changed |= reachabilityGraph.get(a).addAll(reachabilityGraph.get(nonterminal));
		return changed;
	}
	
}
