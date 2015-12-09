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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import iguana.parsetrees.slot.NonterminalNodeType;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.traversal.FreeVariableVisitor;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Nonterminal.Builder;
import org.iguana.traversal.ISymbolVisitor;

/**
 * 
 * 
 * @authors Ali Afroozeh, Anastasia Izmaylova
 *
 */
public class EBNFToBNF implements GrammarTransformation {
	
	private Map<String, Set<String>> ebnfLefts;
	private Map<String, Set<String>> ebnfRights;

	public static Grammar convert(Grammar grammar) {
        EBNFToBNF ebnfToBNF = new EBNFToBNF();
        return ebnfToBNF.transform(grammar);
    }

	@Override
	public Grammar transform(Grammar grammar) {
		Set<Rule> newRules = new LinkedHashSet<>();
		ebnfLefts = grammar.getEBNFLefts();
		ebnfRights = grammar.getEBNFRights();
		grammar.getRules().forEach(r -> newRules.addAll(transform(r)));
		return Grammar.builder().addRules(newRules)
				.addEBNFl(grammar.getEBNFLefts())
				.addEBNFr(grammar.getEBNFRights())
				.setLayout(grammar.getLayout()).build();
	}
	
	private Set<Rule> transform(Rule rule) {
		Set<Rule> newRules = new LinkedHashSet<>();
		newRules.add(rewrite(rule, newRules));
		return newRules;
	}
	
	public static boolean isEBNF(Symbol s) {
		return s instanceof Star ||
			   s instanceof Plus ||
			   s instanceof Opt ||
			   s instanceof Sequence ||
			   s instanceof Alt;
	}

	
	public Rule rewrite(Rule rule, Set<Rule> newRules) {

		if (rule.getBody() == null)
			return rule;
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());
		
		Set<String> state = new HashSet<>();
		new FreeVariableVisitor(state).compute(rule);
		
		EBNFVisitor ebnf_visitor = new EBNFVisitor(state, newRules, rule.getLayout(), rule.getLayoutStrategy(), ebnfLefts, ebnfRights);
		
		for(Symbol s : rule.getBody())
			builder.addSymbol(s.accept(ebnf_visitor));
		
		return builder.setLayout(rule.getLayout())
				.setLayoutStrategy(rule.getLayoutStrategy())
				.setRecursion(rule.getRecursion())
				.setAssociativity(rule.getAssociativity())
				.setAssociativityGroup(rule.getAssociativityGroup())
				.setPrecedence(rule.getPrecedence())
				.setPrecedenceLevel(rule.getPrecedenceLevel())
				.setiRecursion(rule.getIRecursion())
				.setLeftEnd(rule.getLeftEnd())
				.setRightEnd(rule.getRightEnd())
				.setLeftEnds(rule.getLeftEnds())
				.setRightEnds(rule.getRightEnds())
				.setLabel(rule.getLabel())
				.setAction(rule.getAction())
                .setHasRuleType(rule.hasRuleType())
                .setRuleType(rule.getRuleType())
				.build();
	}
	
	public static List<Symbol> rewrite(List<Symbol> list, Nonterminal layout) {
		EBNFVisitor visitor = new EBNFVisitor(new HashSet<>(), new HashSet<>(), layout, LayoutStrategy.INHERITED, new HashMap<>(), new HashMap<>());
		return list.stream().map(s -> s.accept(visitor)).collect(Collectors.toList());
	}

	
	public static String getName(Symbol symbol, List<Symbol> separators, Nonterminal layout) {
		if (separators.isEmpty() && layout == null) {
			return symbol.getName();
		} else {
			return "{" + symbol.getName() +
					  ", " + separators.stream().map(s -> s.getName()).collect(Collectors.joining(", ")) + 
					  ", " + layout + 
				   "}";	
		}
	}
		
	private static class EBNFVisitor implements ISymbolVisitor<Symbol> {
		
		private final Set<String> state;
		private final Set<Rule> addedRules;
		private final Nonterminal layout;
		private final LayoutStrategy strategy;
		
		private final Map<String, Set<String>> ebnfLefts;
		private final Map<String, Set<String>> ebnfRights;
		
		private static int counter = 0;
		
		public EBNFVisitor(Set<String> state, Set<Rule> addedRules, Nonterminal layout, LayoutStrategy strategy,
						   Map<String, Set<String>> ebnfLefts, Map<String, Set<String>> ebnfRights) {
			this.state = state;
			this.addedRules = addedRules;
			this.layout = layout;
			this.strategy = strategy;
			this.ebnfLefts = ebnfLefts;
			this.ebnfRights = ebnfRights;
		}
		
		/**
		 *  Target cases:
		 */
		
		private Set<String> freeVars;
		private FreeVariableVisitor visitor;
		
		private void init() {
			freeVars = new LinkedHashSet<>();
			visitor = new FreeVariableVisitor(freeVars);			
		}
		
		/**
		 * (A | B) ::= A 
		 *           | B
		 */
		@Override
		public <E extends Symbol> Symbol visit(Alt<E> symbol) {	
			List<? extends Symbol> symbols = symbol.getSymbols().stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			Alt.builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? Nonterminal.builder(symbol.getName()).setType(NonterminalNodeType.Alt()).build()
					            		: Nonterminal.builder(symbol.getName()).addParameters(parameters).setType(NonterminalNodeType.Alt()).build();
			
			symbols.forEach(x -> addedRules.add(Rule.withHead(newNt).addSymbol(x).setLayout(layout).setLayoutStrategy(strategy)
														.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
														.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build()));
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S? ::= S 
		 *      | epsilon
		 */
		@Override
		public Symbol visit(Opt symbol) {
			Symbol in = symbol.getSymbol().accept(this);
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			in.setEmpty();
			visitor.visitSymbol(in);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? Nonterminal.builder(symbol.getName()).setType(NonterminalNodeType.Opt()).build()
									: Nonterminal.builder(symbol.getName()).addParameters(parameters).setType(NonterminalNodeType.Opt()).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbol(in).setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			addedRules.add(Rule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S+ ::= S+ S
		 *      | S
		 *      
		 * in case of separators:
		 * 
		 * (S sep)+ ::= (S sep)+ sep S
		 *            | S
		 *      
		 */
		@Override
		public Symbol visit(Plus symbol) {
			Symbol S = symbol.getSymbol().accept(this);
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			S.setEmpty();
			visitor.visitSymbol(S);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			List<Symbol> seperators = symbol.getSeparators().stream().map(sep -> sep.accept(this)).collect(Collectors.toList());

			Nonterminal newNt = parameters == null? Nonterminal.builder(getName(S, symbol.getSeparators(), layout) + "+").setType(NonterminalNodeType.Plus()).build()
									: Nonterminal.builder(getName(S, symbol.getSeparators(), layout) + "+").addParameters(parameters).setType(NonterminalNodeType.Plus()).build();
			
			addedRules.add(Rule.withHead(newNt)
									.addSymbol(arguments != null? Nonterminal.builder(newNt).apply(arguments).build() : newNt)
									.addSymbols(seperators)
									.addSymbols(S).setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnd(newNt.getName())
									.setRightEnd(S.getName())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()) : new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()) : new HashSet<>())
									.build());
			addedRules.add(Rule.withHead(newNt).addSymbol(S)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnd(S.getName())
									.setRightEnd(S.getName())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()) : new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()) : new HashSet<>())
									.build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * (S) ::= S
		 */
		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			List<Symbol> symbols = symbol.getSymbols().stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			Sequence.builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? Nonterminal.builder(symbol.getName()).setType(NonterminalNodeType.Seq()).build()
									: Nonterminal.builder(symbol.getName()).addParameters(parameters).setType(NonterminalNodeType.Seq()).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbols(symbols).setLayout(layout).setLayoutStrategy(strategy)
								.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
								.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
								.setLeftEnd(symbols.get(0).getName())
								.setRightEnd(symbols.get(symbols.size() - 1).getName())
								.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()) : new HashSet<>())
								.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()) : new HashSet<>())
								.build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S* ::= S+ 
		 *      | epsilon
		 *        
		 */
		@Override
		public Symbol visit(Star symbol) {
			Symbol S = Plus.builder(symbol.getSymbol()).addSeparators(symbol.getSeparators()).build().accept(this);
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			S.setEmpty();
			visitor.visitSymbol(S);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			String base = getName(symbol.getSymbol(), symbol.getSeparators(), layout);
			Nonterminal newNt = parameters != null? Nonterminal.builder(base + "*").addParameters(parameters).setType(NonterminalNodeType.Star()).build()
						              : Nonterminal.builder(base + "*").setType(NonterminalNodeType.Star()).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbols(S)
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnd(S.getName())
									.setRightEnd(S.getName())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()): new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()): new HashSet<>())
									.build());
			addedRules.add(Rule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()): new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()): new HashSet<>())
									.build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}
		
		/**
		 * S_IF(cond) ::= [ cond] S 
		 *              | [!cond] epsilon 
		 */
		@Override
		public Symbol visit(IfThen symbol) {
			
			Expression cond = symbol.getExpression();
			Symbol thenPart = symbol.getThenPart().accept(this);
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			thenPart.setEmpty();
			visitor.visitSymbol(thenPart);
			
			String id = "condIF";
			
			if (!freeVars.isEmpty()) {
				
				freeVars.removeAll(state);
				
				parameters = new String[freeVars.size() + 1];
				arguments = new Expression[freeVars.size() + 1];
				
				parameters[0] = id;
				arguments[0] = cond;
				
				int i = 1;
				for (String var : freeVars) {
					parameters[i] = var;
					arguments[i] = AST.var(var);
					i++;
				}
			} else {
				parameters = new String[] { id };
				arguments = new Expression[] { cond };
			}
			
			Nonterminal newNt = Nonterminal.builder("IF_" + counter++).addParameters(parameters).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbol(thenPart.copyBuilder().addPreCondition(DataDependentCondition.predicate(AST.var(id))).build())
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			// FIXME: epsilon rule can have a condition
			addedRules.add(Rule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			return newNt.copyBuilder().apply(arguments).addConditions(symbol).setLabel(symbol.getLabel()).build();
		}
		
		/**
		 * AB_IF_ELSE(cond) ::= [ cond] A
		 *                    | [!cond] B
		 */
		@Override
		public Symbol visit(IfThenElse symbol) {
			
			Expression cond = symbol.getExpression();
			Symbol thenPart = symbol.getThenPart().accept(this);
			Symbol elsePart = symbol.getElsePart().accept(this);
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			thenPart.setEmpty();
			elsePart.setEmpty();
			visitor.visitSymbol(thenPart);
			visitor.visitSymbol(elsePart);
			
			String id = "condIF";
			
			if (!freeVars.isEmpty()) {
				
				freeVars.removeAll(state);
				
				parameters = new String[freeVars.size() + 1];
				arguments = new Expression[freeVars.size() + 1];
				
				parameters[0] = id;
				arguments[0] = cond;
				
				int i = 1;
				for (String var : freeVars) {
					parameters[i] = var;
					arguments[i] = AST.var(var);
					i++;
				}
			} else {
				parameters = new String[] { id };
				arguments = new Expression[] { cond };
			}
			
			Nonterminal newNt = Nonterminal.builder("IF_THEN_ELSE_" + counter++).addParameters(parameters).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbol(thenPart.copyBuilder().addPreCondition(DataDependentCondition.predicate(AST.var(id))).build())
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			addedRules.add(Rule.withHead(newNt).addSymbol(elsePart.copyBuilder().addPreCondition(DataDependentCondition.predicate(AST.not(AST.var(id)))).build())
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			return newNt.copyBuilder().apply(arguments).addConditions(symbol).setLabel(symbol.getLabel()).build();
		}
		
		/**
		 *  The rest:
		 */

		@Override
		public Symbol visit(Align symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: Align.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Block symbol) {
			Symbol[] symbols = symbol.getSymbols();
			Symbol[] syms = new Symbol[symbols.length];
			
			int j = 0;
			boolean modified = false;
			for (Symbol sym : symbols) {
				syms[j] = sym.accept(this);
				if (sym != syms[j])
					modified |= true;
				j++;
			}
			return modified? Block.builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build()
					: symbol;
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
		public Symbol visit(Ignore symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: Ignore.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Nonterminal symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(Offside symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: Offside.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Terminal symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(While symbol) {
			Symbol body = symbol.getBody().accept(this);
			if (body == symbol.getBody()) 
				return symbol;
			
			return While.builder(symbol.getExpression(), body).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}
		
		@Override
		public Symbol visit(Return symbol) {
			return symbol;
		}
		
	}

}
