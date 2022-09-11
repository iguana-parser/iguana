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

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.traversal.FreeVariableVisitor;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Nonterminal.Builder;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public class EBNFToBNF implements GrammarTransformation {
	
	private Map<String, Set<String>> ebnfLefts;
	private Map<String, Set<String>> ebnfRights;

	public static RuntimeGrammar convert(RuntimeGrammar grammar) {
        EBNFToBNF ebnfToBNF = new EBNFToBNF();
        return ebnfToBNF.transform(grammar);
    }

	@Override
	public RuntimeGrammar transform(RuntimeGrammar grammar) {
		Set<RuntimeRule> newRules = new LinkedHashSet<>();
		ebnfLefts = grammar.getEBNFLefts();
		ebnfRights = grammar.getEBNFRights();
		grammar.getRules().forEach(r -> newRules.addAll(transform(r)));
		return RuntimeGrammar.builder().addRules(newRules)
				.addEBNFl(grammar.getEBNFLefts())
				.addEBNFr(grammar.getEBNFRights())
				.setLayout(grammar.getLayout())
				.setGlobals(grammar.getGlobals())
				.setStartSymbol(grammar.getStartSymbol()).build();
	}
	
	private Set<RuntimeRule> transform(RuntimeRule rule) {
		Set<RuntimeRule> newRules = new LinkedHashSet<>();
		newRules.add(rewrite(rule, newRules));
		return newRules;
	}
	
	public RuntimeRule rewrite(RuntimeRule rule, Set<RuntimeRule> newRules) {

		if (rule.getBody() == null)
			return rule;
		
		RuntimeRule.Builder builder = new RuntimeRule.Builder(rule.getHead());
		
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
				.setDefinition(rule.getDefinition())
				.build();
	}
	
	public static List<Symbol> rewrite(List<Symbol> list, Nonterminal layout) {
		EBNFVisitor visitor = new EBNFVisitor(new HashSet<>(), new HashSet<>(), layout, LayoutStrategy.INHERITED, new HashMap<>(), new HashMap<>());
		return list.stream().map(s -> s.accept(visitor)).collect(Collectors.toList());
	}

	
	public static String getName(Symbol symbol, List<Symbol> separators, Symbol layout) {
		if (separators.isEmpty() && layout == null) {
			return symbol.getName();
		} else {
			return "{" + symbol.getName() +
					  ", " + separators.stream().map(Symbol::getName).collect(Collectors.joining(", ")) +
					  ", " + layout + 
				   "}";	
		}
	}
		
	private static class EBNFVisitor implements ISymbolVisitor<Symbol> {
		
		private final Set<String> state;
		private final Set<RuntimeRule> addedRules;
		private final Symbol layout;
		private final LayoutStrategy strategy;
		
		private final Map<String, Set<String>> ebnfLefts;
		private final Map<String, Set<String>> ebnfRights;
		
		private static int counter = 0;
		
		public EBNFVisitor(Set<String> state, Set<RuntimeRule> addedRules, Symbol layout, LayoutStrategy strategy,
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
		public Symbol visit(Alt symbol) {
			List<Symbol> symbols = symbol.getSymbols().stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			new Alt.Builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? new Nonterminal.Builder(symbol.getName()).setNodeType(NonterminalNodeType.Alt).build()
					            		: new Nonterminal.Builder(symbol.getName()).addParameters(parameters).setNodeType(NonterminalNodeType.Alt).build();
			
			symbols.forEach(x -> addedRules.add(RuntimeRule.withHead(newNt).addSymbol(x).setLayout(layout).setLayoutStrategy(strategy)
														.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
														.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
														.setDefinition(symbol)
														.build()));
			
			Builder copyBuilder = arguments == null? newNt.copy() : newNt.copy().apply(arguments);
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
			
			Nonterminal newNt = parameters == null? new Nonterminal.Builder(symbol.getName()).setNodeType(NonterminalNodeType.Opt).build()
									: new Nonterminal.Builder(symbol.getName()).addParameters(parameters).setNodeType(NonterminalNodeType.Opt).build();
			
			addedRules.add(RuntimeRule.withHead(newNt).addSymbol(in).setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setDefinition(symbol)
									.build());
			addedRules.add(RuntimeRule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setDefinition(symbol)
									.build());
			
			Builder copyBuilder = arguments == null? newNt.copy() : newNt.copy().apply(arguments);
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

			Nonterminal newNt = parameters == null? new Nonterminal.Builder(getName(S, symbol.getSeparators(), layout) + "+").setNodeType(NonterminalNodeType.Plus).build()
									: new Nonterminal.Builder(getName(S, symbol.getSeparators(), layout) + "+").addParameters(parameters).setNodeType(NonterminalNodeType.Plus).build();
			
			addedRules.add(RuntimeRule.withHead(newNt)
									.addSymbol(arguments != null? new Nonterminal.Builder(newNt).apply(arguments).build() : newNt)
									.addSymbols(seperators)
									.addSymbols(S).setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnd(newNt.getName())
									.setRightEnd(S.getName())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()) : new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()) : new HashSet<>())
									.setDefinition(symbol)
									.build());
			addedRules.add(RuntimeRule.withHead(newNt).addSymbol(S)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnd(S.getName())
									.setRightEnd(S.getName())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()) : new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()) : new HashSet<>())
									.setDefinition(symbol)
									.build());
			
			Builder copyBuilder = arguments == null? newNt.copy() : newNt.copy().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * (S) ::= S
		 */
		@Override
		public Symbol visit(Group symbol) {
			List<Symbol> symbols = symbol.getSymbols().stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			new Group.Builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				freeVars.removeAll(state);
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? new Nonterminal.Builder(symbol.getName()).setNodeType(NonterminalNodeType.Seq).build()
									: new Nonterminal.Builder(symbol.getName()).addParameters(parameters).setNodeType(NonterminalNodeType.Seq).build();
			
			addedRules.add(RuntimeRule.withHead(newNt).addSymbols(symbols).setLayout(layout).setLayoutStrategy(strategy)
								.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
								.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
								.setLeftEnd(symbols.get(0).getName())
								.setRightEnd(symbols.get(symbols.size() - 1).getName())
								.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()) : new HashSet<>())
								.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()) : new HashSet<>())
								.setDefinition(symbol)
								.build());
			
			Builder copyBuilder = arguments == null? newNt.copy() : newNt.copy().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S* ::= S+ 
		 *      | epsilon
		 *        
		 */
		@Override
		public Symbol visit(Star symbol) {
			Symbol S = new Plus.Builder(symbol.getSymbol()).addSeparators(symbol.getSeparators()).build().accept(this);
			
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
			Nonterminal newNt = parameters != null? new Nonterminal.Builder(base + "*").addParameters(parameters).setNodeType(NonterminalNodeType.Star).build()
						              : new Nonterminal.Builder(base + "*").setNodeType(NonterminalNodeType.Star).build();
			
			addedRules.add(RuntimeRule.withHead(newNt).addSymbols(S)
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnd(S.getName())
									.setRightEnd(S.getName())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()): new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()): new HashSet<>())
									.setDefinition(symbol)
									.build());
			addedRules.add(RuntimeRule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setLeftEnds(ebnfLefts.containsKey(newNt.getName())? ebnfLefts.get(newNt.getName()): new HashSet<>())
									.setRightEnds(ebnfRights.containsKey(newNt.getName())? ebnfRights.get(newNt.getName()): new HashSet<>())
									.setDefinition(symbol)
									.build());
			
			Builder copyBuilder = arguments == null? newNt.copy() : newNt.copy().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

        @Override
        public Symbol visit(Start start) {
			return start;
//            return start.getNonterminal().accept(this);
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
			
			Nonterminal newNt = new Nonterminal.Builder("IF_" + counter++).addParameters(parameters).build();
			
			addedRules.add(RuntimeRule.withHead(newNt).addSymbol(thenPart.copy().addPreCondition(DataDependentCondition.predicate(AST.var(id))).build())
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setDefinition(symbol)
									.build());
			
			// FIXME: epsilon rule can have a condition
			addedRules.add(RuntimeRule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			return newNt.copy().apply(arguments).addConditions(symbol).setLabel(symbol.getLabel()).build();
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
			String[] parameters;
			Expression[] arguments;
			
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
			
			Nonterminal newNt = new Nonterminal.Builder("IF_THEN_ELSE_" + counter++).addParameters(parameters).build();
			
			addedRules.add(RuntimeRule.withHead(newNt).addSymbol(thenPart.copy().addPreCondition(DataDependentCondition.predicate(AST.var(id))).build())
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setDefinition(symbol)
									.build());
			
			addedRules.add(RuntimeRule.withHead(newNt).addSymbol(elsePart.copy().addPreCondition(DataDependentCondition.predicate(AST.not(AST.var(id)))).build())
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
									.setDefinition(symbol)
									.build());
			
			return newNt.copy().apply(arguments).addConditions(symbol).setLabel(symbol.getLabel()).build();
		}
		
		/**
		 *  The rest:
		 */

		@Override
		public Symbol visit(Align symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: new Align.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Block symbol) {
			List<Symbol> symbols = symbol.getSymbols();
			Symbol[] syms = new Symbol[symbols.size()];
			
			int j = 0;
			boolean modified = false;
			for (Symbol sym : symbols) {
				syms[j] = sym.accept(this);
				if (sym != syms[j])
					modified |= true;
				j++;
			}
			return modified? new Block.Builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build()
					: symbol;
		}

		@Override
		public Symbol visit(Code symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return new Code.Builder(sym, symbol.getStatements()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Conditional symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return new Conditional.Builder(sym, symbol.getExpression()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Ignore symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: new Ignore.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Nonterminal symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(Offside symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: new Offside.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
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
			
			return new While.Builder(symbol.getExpression(), body).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}
		
		@Override
		public Symbol visit(Return symbol) {
			return symbol;
		}
		
	}

}
