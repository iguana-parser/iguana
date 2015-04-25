/*
 * Copyright (c) 2015, CWI
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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.traversal.FreeVariableVisitor;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Associativity;
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
import org.iguana.grammar.symbol.LayoutStrategy;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.PrecedenceLevel;
import org.iguana.grammar.symbol.Recursion;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.grammar.symbol.Nonterminal.Builder;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.traversal.ISymbolVisitor;

/**
 * 
 * 
 * @authors Ali Afroozeh, Anastasia Izmaylova
 *
 */
public class EBNFToBNF implements GrammarTransformation {
	

	@Override
	public Grammar transform(Grammar grammar) {
		Set<Rule> newRules = new LinkedHashSet<>();
		grammar.getDefinitions().values().forEach(r -> newRules.addAll(transform(r)));
		return Grammar.builder().addRules(newRules).setLayout(grammar.getLayout()).build();
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

		if (rule.getBody() == null) {
			return rule;
		}
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());
		
		EBNFVisitor visitor = new EBNFVisitor(newRules, rule.getLayout(), rule.getLayoutStrategy());
		
		for(Symbol s : rule.getBody()) {
			builder.addSymbol(s.accept(visitor));
		}
		
		return builder.setLayout(rule.getLayout())
				.setLayoutStrategy(rule.getLayoutStrategy())
				.setRecursion(rule.getRecursion())
				.setAssociativity(rule.getAssociativity())
				.setAssociativityGroup(rule.getAssociativityGroup())
				.setPrecedence(rule.getPrecedence())
				.setPrecedenceLevel(rule.getPrecedenceLevel())
				.setLabel(rule.getLabel())
				.build();
	}
	
	public static List<Symbol> rewrite(List<Symbol> list, Nonterminal layout) {
		EBNFVisitor visitor = new EBNFVisitor(new HashSet<>(), layout, LayoutStrategy.INHERITED);
		return list.stream().map(s -> s.accept(visitor)).collect(Collectors.toList());
	}

	
	private static String getName(Symbol symbol, List<Symbol> separators, Nonterminal layout) {
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
		
		private final Set<Rule> addedRules;
		private final Nonterminal layout;
		private final LayoutStrategy strategy;
		
		public EBNFVisitor(Set<Rule> addedRules, Nonterminal layout, LayoutStrategy strategy) {
			this.addedRules = addedRules;
			this.layout = layout;
			this.strategy = strategy;
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
			List<? extends Symbol> symbols = symbol.getSymbols();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			Alt.builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			symbols = symbols.stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(symbol.getName()) 
					            		: Nonterminal.builder(symbol.getName()).addParameters(parameters).build();
			
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
			Symbol in = symbol.getSymbol();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			in.setEmpty();
			visitor.visitSymbol(in);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(symbol.getName())
									: Nonterminal.builder(symbol.getName()).addParameters(parameters).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbol(in.accept(this)).setLayout(layout).setLayoutStrategy(strategy)
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
			Symbol S = symbol.getSymbol();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			S.setEmpty();
			visitor.visitSymbol(S);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			List<Symbol> seperators = symbol.getSeparators().stream().map(sep -> sep.accept(this)).collect(Collectors.toList());
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(getName(S, symbol.getSeparators(), layout) + "+")
									: Nonterminal.builder(getName(S, symbol.getSeparators(), layout) + "+").addParameters(parameters).build();
			
			S = S.accept(this);
			
			addedRules.add(Rule.withHead(newNt)
									.addSymbol(arguments != null? Nonterminal.builder(newNt).apply(arguments).build() : newNt)
									.addSymbols(seperators)
									.addSymbols(S).setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			addedRules.add(Rule.withHead(newNt).addSymbol(S)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * (S) ::= S
		 */
		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			List<? extends Symbol> symbols = symbol.getSymbols();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			Sequence.builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			List<Symbol> syms = symbols.stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(symbol.getName())
									: Nonterminal.builder(symbol.getName()).addParameters(parameters).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbols(syms).setLayout(layout).setLayoutStrategy(strategy)
								.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
								.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
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
			Symbol S = symbol.getSymbol();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			S.setEmpty();
			visitor.visitSymbol(S);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters != null? Nonterminal.builder(getName(S, symbol.getSeparators(), layout) + "*").addParameters(parameters).build()
						              : Nonterminal.withName(getName(S, symbol.getSeparators(), layout) + "*");
			
			addedRules.add(Rule.withHead(newNt).addSymbols(Plus.builder(S).addSeparators(symbol.getSeparators()).build().accept(this))
									.setLayout(layout).setLayoutStrategy(strategy)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			addedRules.add(Rule.withHead(newNt)
									.setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
									.setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone()).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
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
			Symbol sym = symbol.getThenPart().accept(this);
			if (sym == symbol.getThenPart())
				return symbol;
			
			return IfThen.builder(symbol.getExpression(), sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(IfThenElse symbol) {
			Symbol thenPart = symbol.getThenPart().accept(this);
			Symbol elsePart = symbol.getElsePart().accept(this);
			if (thenPart == symbol.getThenPart() 
					&& elsePart == symbol.getElsePart())
				return symbol;
			
			return IfThenElse.builder(symbol.getExpression(), thenPart, elsePart).setLabel(symbol.getLabel()).addConditions(symbol).build();
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
		
	}

}
