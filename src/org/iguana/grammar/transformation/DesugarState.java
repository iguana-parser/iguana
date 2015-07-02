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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.eclipse.imp.pdb.facts.util.TrieSet;
import org.iguana.datadependent.traversal.FreeVariableVisitor;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;

/**
 * 
 * @author Anastasia Izmaylova
 *
 */

/*
 * phase: after EBNF transformation
 */
public class DesugarState implements GrammarTransformation {
	
	private final Map<Nonterminal,Set<String>> uses = new HashMap<>();
	private final Map<Nonterminal,Set<String>> updates = new HashMap<>();
	
	private final Map<Nonterminal, Set<String>> returns = new HashMap<>();
	
	private Set<String> current_uses;
	private Set<String> current_updates;
	
	private Map<Nonterminal, Set<Nonterminal>> reachabilityGraph;

	@Override
	public Grammar transform(Grammar grammar) {
		for (Nonterminal head : grammar.getNonterminals()) {
			current_uses = new HashSet<>();
			current_updates = new HashSet<>();
			
			uses.put(head, current_uses);
			updates.put(head, current_updates);
			
			FreeVariableVisitor visitor = new FreeVariableVisitor(current_uses, current_updates);
			
			ImmutableSet<String> env = TrieSet.of();
			
			String[] parameters = head.getParameters();
			if (parameters != null) {
				for (String parameter : parameters)
					env.__insert(parameter);
			}
			
			for (Rule rule : grammar.getAlternatives(head)) {
				
				ImmutableSet<String> _env = env;
				
				for (Symbol symbol : rule.getBody()) {
					symbol.setEnv(_env);
					visitor.visitSymbol(symbol);
					_env = symbol.getEnv();
					symbol.setEmpty();
				}
			}
		}
		
		// After EBNF translation
		reachabilityGraph = new ReachabilityGraph(grammar).getReachabilityGraph();
		
		for (Map.Entry<Nonterminal, Set<Nonterminal>> entry : reachabilityGraph.entrySet()) {
			current_uses = uses.get(entry.getKey());
			current_updates = updates.get(entry.getKey());
			
			for (Nonterminal nonterminal : entry.getValue()) {
				current_uses.addAll(uses.get(nonterminal));
				current_updates.addAll(updates.get(nonterminal));
			}
		}
		
		for (Nonterminal nonterminal : updates.keySet())
			returns.put(nonterminal, new HashSet<>());
		
		for (Nonterminal head : grammar.getNonterminals()) {
			FreeVariableVisitor visitor = new FreeVariableVisitor(updates, returns);
			
			ImmutableSet<String> env = TrieSet.of();
			
			String[] parameters = head.getParameters();
			if (parameters != null) {
				for (String parameter : parameters)
					env.__insert(parameter);
			}
			
			for (Rule rule : grammar.getAlternatives(head)) {
				
				ImmutableSet<String> _env = env;
				visitor.init();
				
				for (Symbol symbol : rule.getBody()) {
					symbol.setEnv(_env);
					visitor.visitSymbol(symbol);
					_env = symbol.getEnv();
					symbol.setEmpty();
				}
			}
		}
		
		Set<Rule> newRules = new LinkedHashSet<>();
		grammar.getRules().forEach(r -> newRules.addAll(transform(r)));
		return Grammar.builder().addRules(newRules).setLayout(grammar.getLayout()).build();
	}
	
	private Set<Rule> transform(Rule rule) {
		Set<Rule> newRules = new LinkedHashSet<>();
		newRules.add(null);
		return newRules;
	}

}
