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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.traversal.FreeVariableVisitor;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.exception.UnexpectedSymbol;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Nonterminal.Builder;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.traversal.ISymbolVisitor;
import org.iguana.util.generator.GeneratorUtil;

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
	private final Map<Nonterminal, List<Map<Nonterminal, Set<String>>>> bindings = new HashMap<>();
	
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
			for (Rule rule : grammar.getAlternatives(head))
				visitor.compute(rule);
			if (!current_updates.isEmpty())
				System.out.println("Updates: " + head + "    " + GeneratorUtil.listToString(current_updates, " , ") );
		}
		
		reachabilityGraph = new ReachabilityGraph(grammar).getReachabilityGraph();		
		for (Map.Entry<Nonterminal, Set<Nonterminal>> entry : reachabilityGraph.entrySet()) {
			current_uses = uses.get(entry.getKey());
			current_updates = updates.get(entry.getKey());
			
			for (Nonterminal nonterminal : entry.getValue()) {
				current_uses.addAll(uses.get(nonterminal));
				current_updates.addAll(updates.get(nonterminal));
			}
		}
		
		// Compute returns		
		for (Nonterminal nonterminal : updates.keySet())
			returns.put(nonterminal, new HashSet<>());
		
		for (Nonterminal head : grammar.getNonterminals()) {
			FreeVariableVisitor visitor = new FreeVariableVisitor(uses, updates, returns);
			
			List<Map<Nonterminal, Set<String>>> nonterminal_bindings = new ArrayList<>(); 
			bindings.put(head, nonterminal_bindings);
			
			for (Rule rule : grammar.getAlternatives(head))	
				nonterminal_bindings.add(visitor.computeBindings(rule));
		}
		
		boolean changed = true;		
		while (changed) {		
			changed = false;
			for (Map.Entry<Nonterminal, Set<String>> head : returns.entrySet()) {
				if (!head.getValue().isEmpty()) {
					for (Map<Nonterminal, Set<String>> rule_bindings : bindings.get(head.getKey())) {
						for (Map.Entry<Nonterminal, Set<String>> in_rule_bindings : rule_bindings.entrySet()) {
							Set<String> nonterminal_updates = updates.get(in_rule_bindings.getKey());
							Set<String> nonterminal_returns = returns.get(in_rule_bindings.getKey());
							for (String ret : head.getValue()) {
								if (nonterminal_updates.contains(ret) && !in_rule_bindings.getValue().contains(ret)) {
									in_rule_bindings.getValue().add(ret);				
									if (!nonterminal_returns.contains(ret)) {
										nonterminal_returns.add(ret);
										changed = true;
									}
								}
							}
						}
					}
				}
			}
		}
		
		for (Map.Entry<Nonterminal, Set<String>> entry : uses.entrySet())
			if (!entry.getValue().isEmpty()) {
				System.out.println("Uses: " + entry.getKey() + "    " + GeneratorUtil.listToString(entry.getValue(), ";"));
			}
		
		for (Map.Entry<Nonterminal, Set<String>> entry : returns.entrySet())
			if (!entry.getValue().isEmpty()) {
				System.out.println("Returns: " + entry.getKey() + "    " + GeneratorUtil.listToString(entry.getValue(), ";"));
			}
		
		Set<Rule> newRules = new LinkedHashSet<>();
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			int i = 0;
			List<Map<Nonterminal, Set<String>>> nonterminal_bindings = bindings.get(nonterminal);
			for (Rule rule : grammar.getAlternatives(nonterminal)) {
				newRules.add(transform(rule, uses, nonterminal_bindings == null? new HashMap<>() : nonterminal_bindings.get(i++), returns));
			}
		}
		return Grammar.builder().addRules(newRules).setLayout(grammar.getLayout()).build();
	}
	
	private Rule transform(Rule rule, Map<Nonterminal, Set<String>> uses, Map<Nonterminal, Set<String>> bindings, Map<Nonterminal, Set<String>> returns) {
		if (rule.getBody() == null)
			return rule;
		
		Set<String> head_uses = uses.get(rule.getHead());
		
		Rule.Builder builder = null;
		if (!head_uses.isEmpty()) {
			
			String[] parameters = new String[head_uses.size()];
			int i = 0;
			for (String parameter : head_uses)
			    parameters[i++] = parameter;
			    
			builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters(parameters).build());
		}
		
		if (builder == null)
			builder = rule.copyBuilder();
		
		List<Symbol> symbols = new ArrayList<>();
		
		builder = builder.setSymbols(symbols);
		
		DesugarStateVisitor visitor = new DesugarStateVisitor(uses, returns, bindings);
		
		Return rsym = null;
		for (Symbol symbol : rule.getBody()) {
			if (symbol instanceof Return)
				rsym = (Return) symbol;
			else
				symbols.add(symbol.accept(visitor));
		}
		
		Set<String> rets = returns.get(rule.getHead());
		
		if (rets != null && !rets.isEmpty()) {
			Expression[] exps;
			int i = 0;
			if (rsym != null) {
				exps = new Expression[rets.size() + 1];
				exps[i++] = rsym.getExpression();
			} else {
				exps = new Expression[rets.size()];
			}
			
			for (String ret : rets)
				exps[i++] = AST.var(ret);
			
			symbols.add(Return.ret(AST.tuple(exps)));
		} else {
			if (rsym != null)
				symbols.add(rsym);
		}
		
		return builder.build();
	}
	
	static public class DesugarStateVisitor implements ISymbolVisitor<Symbol> {
		
		private final Map<Nonterminal, Set<String>> uses;
		private final Map<Nonterminal, Set<String>> returns;
		private final Map<Nonterminal, Set<String>> bindings;
		
		public DesugarStateVisitor(Map<Nonterminal, Set<String>> uses, Map<Nonterminal, Set<String>> returns, Map<Nonterminal, Set<String>> bindings) {
			this.uses = uses;
			this.returns = returns;
			this.bindings = bindings;
		}

		@Override
		public Symbol visit(Align symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			return Align.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Block symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(Character symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(CharacterRange symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(Code symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return Code.builder(sym, symbol.getStatements()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Conditional symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			return Conditional.builder(sym, symbol.getExpression()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(EOF symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(Epsilon symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(IfThen symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(IfThenElse symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(Ignore symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			return Ignore.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Nonterminal symbol) {
			
			Builder builder = symbol.copyBuilder();
			
			boolean changed = false;
			
			Set<String> pass = uses.get(symbol);
			
			if (pass != null && !pass.isEmpty()) {
				
				Expression[] arguments = new Expression[pass.size()];
				int i = 0;
				for (String argument : pass)
					arguments[i++] = AST.var(argument);
				
				builder.apply(arguments);
				changed = true;
			}
			
			Set<String> bind = bindings.get(symbol);
			
			if (bind != null) {
				Set<String> state = new LinkedHashSet<>();
				
				for (String ret : returns.get(symbol)) {
					if (bind.contains(ret)) state.add(ret);
					else state.add("_");
				}
				
				builder.setState(state);
				changed = true;
			}
			
			return changed? builder.build() : symbol;
		}

		@Override
		public Symbol visit(Offside symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			return Offside.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Terminal symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(While symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(Return symbol) {
			return symbol;
		}

		@Override
		public <E extends Symbol> Symbol visit(Alt<E> symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(Opt symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(Plus symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}

		@Override
		public Symbol visit(Star symbol) {
			throw new UnexpectedSymbol(symbol, "desugar-state");
		}
		
	}

}
