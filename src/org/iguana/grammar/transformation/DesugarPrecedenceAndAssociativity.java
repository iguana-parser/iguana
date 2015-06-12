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

import static org.iguana.datadependent.ast.AST.*;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.AssociativityGroup;
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
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.PrecedenceLevel;
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

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * Description:
 * 
 * 		- Two parameters, l and r, are introduced to pass a precedence level,
 *        and each rule gets a precedence level. 
 * 		  We need two parameters to account for left (l) and right (r) recursion.
 *      		- for left recursive rules, symbols on the left should pass as the 1st argument either 
 *              (a) the precedence level of the rule if the rule is left-associative; or 
 *              (b) the precedence level plus one if it is right-associative or non-associative, or
 *                  the first rule in the current group starting from the top.
 *          - for right recursive rules, symbols on the right should pass as the 2nd argument either
 *              (a) the precedence level of the rule if the rule is left-associative; or
 *              (b) the precedence level of the rule if it is right-associative, or
 *                  the first rule in the current group starting from the top.
 *          - The 2nd argument to left recursive symbols or the 1st argument to right recursive symbols
 *            is defined as follows:
 *            (a) the precedence level is propagated if there are rules with lower precedence that
 *                can be affected (this is to account for deep cases), i.e.,
 *                l if right, or left and right associative rules are below, otherwise 0
 *                r if left, or left and right associative rules are below, otherwise 0
 *      - Conditions are added to the rules as follows:
 *          (a) if left recursive, the precedence level (or the highest in the current group of rules) >= r
 *          (b) if right recursive, the precedence level (or the highest in the current group of rules) >= l
 *          (c) if the rule is part of a group but not the 1st in the group:
 *                - if left associative, the precedence level != r
 *                - if right associative, the precedence level != l
 *          (d) if the rule is part of a group which is a left associative with respect to each other,
 *                - if left associative, { the precedence level of a rule in the group != r}
 *                  (the lowest precedence level minus 1???)
 *                - if right associative, { the precedence level of a rule in the group != l}
 *                  (the lowest precedence level minus 1???)
 *        
 */

public class DesugarPrecedenceAndAssociativity implements GrammarTransformation {
	
	private Set<String> leftOrRightRecursiveNonterminals; // operator precedence and associativity
	
	private Map<String, Map<String, Integer>> headsWithLabeledRules; // excepts
	
	@Override
	public Grammar transform(Grammar grammar) {
		
		leftOrRightRecursiveNonterminals = new HashSet<>();
		headsWithLabeledRules = new HashMap<>();
		
		
		for (Rule rule: grammar.getRules()) {
			Nonterminal head = rule.getHead();
			// 1.
			if (rule.getPrecedenceLevel().getRhs() != 1 || 
					(rule.getPrecedence() == 1 && rule.getAssociativity() != Associativity.UNDEFINED))
				leftOrRightRecursiveNonterminals.add(head.getName());
			// else: all the rules have a precedence -1 (non-recursive) or 1, and
			// undefined associativity; therefore, precedence does not apply
			
			// 2.
			if (rule.getLabel() != null) {
				Map<String, Integer> labels = headsWithLabeledRules.get(head.getName());
				if (labels != null) {
					if(!labels.containsKey(rule.getLabel()))
						labels.put(rule.getLabel(), labels.size());
				} else {
					labels = new HashMap<>();
					labels.put(rule.getLabel(), labels.size());
					headsWithLabeledRules.put(head.getName(), labels);
				}
			}
		}
		
		Set<Rule> rules = new LinkedHashSet<>();
		for (Rule rule :grammar.getRules())
			rules.add(transform(rule));
		
		return Grammar.builder().addRules(rules).setLayout(grammar.getLayout()).build();
	}
	
	public Rule transform(Rule rule) {
		return new Visitor(rule, leftOrRightRecursiveNonterminals, headsWithLabeledRules).transform();
	}

	private static class Visitor implements ISymbolVisitor<Symbol> {
		
		private final Rule rule;
		
		private final Set<String> leftOrRightRecursiveNonterminals;
		
		private final Map<String, Map<String, Integer>> headsWithLabeledRules;
		
		private Expression l1;
		private Expression r2;
		
		private Expression l2;
		private Expression r1;
		
		private Set<Condition> preconditions;
		
		private boolean isFirst;
		private boolean isLast;
		
		public Visitor(Rule rule, Set<String> leftOrRightRecursiveNonterminals, Map<String, Map<String, Integer>> headsWithLabeledRules) {
			this.rule = rule;
			this.leftOrRightRecursiveNonterminals = leftOrRightRecursiveNonterminals;
			this.headsWithLabeledRules = headsWithLabeledRules;
			
			// 1. Excepts
			if (rule.getLabel() != null) {
				preconditions = new HashSet<>();
				
				int l = headsWithLabeledRules.get(rule.getHead().getName()).get(rule.getLabel());
				preconditions.add(predicate(lShiftANDEqZero(var("_not"), integer(l))));
			}
			
			// 2. Precedence and associativity
			if (this.rule.getPrecedence() == -1) return; // Precedence does not apply
			
			if (!this.leftOrRightRecursiveNonterminals
					.contains(this.rule.getHead().getName())) return; // Precedence does not apply
			
			if (preconditions == null)
				preconditions = new HashSet<>();
			
			AssociativityGroup associativityGroup = rule.getAssociativityGroup();
			PrecedenceLevel precedenceLevel = rule.getPrecedenceLevel();
			
			int precedence = rule.getPrecedence();
			Associativity associativity = rule.getAssociativity();
			
			// Expressions for the left and/or right recursive uses
			
			if (associativityGroup != null 
					&& precedenceLevel.getLhs() == associativityGroup.getLhs() 
						&& precedenceLevel.getRhs() == associativityGroup.getRhs()) {
				
				if (precedence == associativityGroup.getPrecedence()) { // Can use precedence climbing
					boolean first = precedenceLevel.getUndefined() == 0;
					switch(associativityGroup.getAssociativity()) {
						case LEFT:
							l1 = integer(first? 0 : precedence);
							r2 = integer(precedenceLevel.getRhs() + 1);
							break;
						case RIGHT:
							l1 = integer(precedenceLevel.getRhs() + 1);
							r2 = integer(first? 0 : precedence);
							break;
						case NON_ASSOC:
							l1 = integer(precedenceLevel.getRhs() + 1);
							r2 = integer(precedenceLevel.getRhs() + 1);
							break;
						default: throw new RuntimeException("Unexpected associativity: " + associativityGroup.getAssociativity());
					}
					
					// Rule for propagation of a precedence level
					if (precedenceLevel.hasPostfixUnaryBelow())
						r1 = var("r");
					else if (precedenceLevel.hasPostfixUnary())
						r1 = integer(first? 0 : precedence);
					else 
						r1 = l1;
					
					if (precedenceLevel.hasPrefixUnaryBelow())
						l2 = var("l");
					else if (precedenceLevel.hasPrefixUnary())
						l2 = integer(first? 0 : precedence);
					else 
						l2 = r2;
					
				} else {
					l1 = integer(precedence);
					r2 = integer(precedence);
					
					// Rule for propagation of a precedence level
					l2 = precedenceLevel.hasPrefixUnaryBelow()? var("l") : integer(0);
					r1 = precedenceLevel.hasPostfixUnaryBelow()? var("r") : integer(0);
				}
				
			} else if (associativityGroup == null && precedenceLevel.getLhs() == precedenceLevel.getRhs()) { // Can use precedence climbing
				boolean first = precedenceLevel.getUndefined() == 0;
				switch(associativity) {
					case LEFT:
						l1 = integer(first? 0 : precedence);
						r2 = integer(precedence + 1);
						break;
					case RIGHT:
						l1 = integer(precedence + 1);
						r2 = integer(first? 0 : precedence);
						break;
					case NON_ASSOC:
						l1 = integer(precedence + 1);
						r2 = integer(precedence + 1);
						break;
					case UNDEFINED:
						l1 = integer(first? 0 : precedence);
						r2 = integer(first? 0 : precedence);
						break;
					default: throw new RuntimeException("Unexpected associativity: " + associativity);
				}
				
				// Rule for propagation of a precedence level
				if (precedenceLevel.hasPostfixUnaryBelow())
					r1 = var("r");
				else if (precedenceLevel.hasPostfixUnary())
					r1 = integer(first? 0 : precedence);
				else 
					r1 = l1;
				
				if (precedenceLevel.hasPrefixUnaryBelow())
					l2 = var("l");
				else if (precedenceLevel.hasPrefixUnary())
					l2 = integer(first? 0 : precedence);
				else 
					l2 = r2;
				
			} else { // No precedence climbing
				int undefined = precedenceLevel.getUndefined();
				boolean useUndefined = (associativityGroup == null || (associativityGroup != null && associativityGroup.getPrecedence() == precedence)) 
											&& undefined != -1;
				
				switch((associativityGroup != null && associativity == Associativity.UNDEFINED)?
							associativityGroup.getAssociativity() : associativity) {
					case LEFT:
						l1 = integer(useUndefined? undefined : precedence);
						r2 = integer(precedence);
						// Rule for propagation of a precedence level
						l2 = precedenceLevel.hasPrefixUnaryBelow()? var("l"): integer(0);
						r1 = precedenceLevel.hasPostfixUnaryBelow()? var("r") : integer(useUndefined? undefined : 0);
						break;
					case RIGHT:
						l1 = integer(precedence);
						r2 = integer(useUndefined? undefined : precedence);
						// Rule for propagation of a precedence level
						l2 = precedenceLevel.hasPrefixUnaryBelow()? var("l") : integer(useUndefined? undefined : 0);
						r1 = precedenceLevel.hasPostfixUnaryBelow()? var("r") : integer(0);
						break;
					case NON_ASSOC:
						l1 = integer(precedence);
						r2 = integer(precedence);
						// Rule for propagation of a precedence level
						l2 = precedenceLevel.hasPrefixUnaryBelow()? var("l") : integer(0);
						r1 = precedenceLevel.hasPostfixUnaryBelow()? var("r") : integer(0);
						break;
					case UNDEFINED: // Not in the associativity group
						l1 = integer(undefined);
						r2 = integer(undefined);
						// Rule for propagation of a precedence level
						l2 = precedenceLevel.hasPrefixUnaryBelow()? var("l") : integer(undefined);
						r1 = precedenceLevel.hasPostfixUnaryBelow()? var("r") : integer(undefined);
						break;
					default: throw new RuntimeException("Unexpected associativity: " + associativity);
				}
			}
						
			// Constraints (preconditions) for the grammar rule
			
			if (rule.isLeftRecursive())
				preconditions.add(predicate(greaterEq(integer(precedenceLevel.getRhs()), var("r"))));
			
			if (rule.isRightRecursive())
				preconditions.add(predicate(greaterEq(integer(precedenceLevel.getRhs()), var("l"))));
			
			if (precedenceLevel.getLhs() != precedenceLevel.getRhs()) {
				
				if (associativityGroup != null) {
					
					boolean climbing = associativityGroup.getLhs() == precedenceLevel.getLhs() 
								&& associativityGroup.getRhs() == precedenceLevel.getRhs();
					
					switch(associativityGroup.getAssociativity()) {
						case LEFT:
							if (rule.isLeftRecursive()) {
								if (!climbing)
									preconditions.add(predicate(notEqual(integer(associativityGroup.getPrecedence()), var("r"))));
								
								if (!associativityGroup.getAssocMap().isEmpty())
									for (Map.Entry<Integer, Associativity> entry : associativityGroup.getAssocMap().entrySet())
										if (precedence != entry.getKey())
											preconditions.add(predicate(notEqual(integer(entry.getKey()), var("r"))));
							}
							break;						
						case RIGHT:
							if (rule.isRightRecursive()) {
								if (!climbing)
									preconditions.add(predicate(notEqual(integer(associativityGroup.getPrecedence()), var("l"))));
								
								if (!associativityGroup.getAssocMap().isEmpty())
									for (Map.Entry<Integer, Associativity> entry : associativityGroup.getAssocMap().entrySet())
										if (precedence != entry.getKey() && !(climbing && entry.getKey() == associativityGroup.getPrecedence()))
											preconditions.add(predicate(notEqual(integer(entry.getKey()), var("l"))));
							}
							break;
						case NON_ASSOC:
							if (!climbing)
								preconditions.add(predicate(notEqual(integer(associativityGroup.getPrecedence()), var("r"))));
							if (!climbing)
								preconditions.add(predicate(notEqual(integer(associativityGroup.getPrecedence()), var("l"))));
							
							if (!associativityGroup.getAssocMap().isEmpty()) {
								for (Map.Entry<Integer, Associativity> entry : associativityGroup.getAssocMap().entrySet()) {
									if (precedence != entry.getKey() && !(climbing && entry.getKey() == associativityGroup.getPrecedence())) {
										preconditions.add(predicate(notEqual(integer(entry.getKey()), var("r"))));
										preconditions.add(predicate(notEqual(integer(entry.getKey()), var("l"))));
									}
								}
							}
							break;
						default: throw new RuntimeException("Unexpected associativity: " + associativityGroup.getAssociativity());
					}
					
					if (precedence != associativityGroup.getPrecedence()) {
						switch(associativity) {
						case LEFT:
							if (rule.isLeftRecursive())
								preconditions.add(predicate(notEqual(integer(precedence), var("r"))));
							break;						
						case RIGHT:
							if (rule.isRightRecursive())
								preconditions.add(predicate(notEqual(integer(precedence), var("l"))));
							break;
						case NON_ASSOC:
							preconditions.add(predicate(notEqual(integer(precedence), var("l"))));
							preconditions.add(predicate(notEqual(integer(precedence), var("r"))));
							break;
						case UNDEFINED:
							break;
						default: throw new RuntimeException("Unexpected associativity: " + associativity);
						}
					}
					
				} else {	
					switch(associativity) {
						case LEFT:
							if (rule.isLeftRecursive())
								preconditions.add(predicate(notEqual(integer(precedence), var("r"))));
							break;						
						case RIGHT:
							if (rule.isRightRecursive())
								preconditions.add(predicate(notEqual(integer(precedence), var("l"))));
							break;
						case NON_ASSOC:
							preconditions.add(predicate(notEqual(integer(precedence), var("l"))));
							preconditions.add(predicate(notEqual(integer(precedence), var("r"))));
							break;
						case UNDEFINED:
							break;
						default: throw new RuntimeException("Unexpected associativity: " + associativity);
					}
				}
				
			}
			
		}
		
		public Rule transform() {
			
			if (rule.getBody() == null)
				return rule;
			
			List<Symbol> symbols = new ArrayList<>();
			Rule.Builder builder;
			
			String head = rule.getHead().getName();
			
			boolean isLeftOrRightRecursiveNonterminal = leftOrRightRecursiveNonterminals.contains(head);
			boolean isHeadWithLabeledRules = headsWithLabeledRules.containsKey(head);
			
			if (isLeftOrRightRecursiveNonterminal && isHeadWithLabeledRules)
				builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("l","r", "_not").build());
			else if (isLeftOrRightRecursiveNonterminal)
				builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("l","r").build());
			else if (isHeadWithLabeledRules)
				builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("_not").build());
			else builder = rule.copyBuilder();
			
			builder = builder.setSymbols(symbols);
			
			int i = 0;
			for (Symbol symbol : rule.getBody()) {
				
				if (i == 0) isFirst = true;
				else isFirst = false;
				
				if (i == rule.getBody().size() - 1) isLast = true;
				else isLast = false;
				
				Symbol sym = symbol.accept(this);
				if (preconditions != null && i == 0)
					symbols.add(sym.copyBuilder().addPreConditions(preconditions).build());
				else 
					symbols.add(sym);
				i++;
			}
			
			return builder.build();
		}
		
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
			
			boolean isFirst = this.isFirst;
			boolean isLast = this.isLast;
			
			int j = 0;
			boolean modified = false;
			for (Symbol sym : symbols) {
				
				if (isFirst && j == 0) this.isFirst = true;
				else this.isFirst = false;
				
				if (isLast && j == symbols.length - 1) this.isLast = true;
				else this.isLast = false;
				
				syms[j] = sym.accept(this);
				if (sym != syms[j])
					modified |= true;
				j++;
			}
			
			this.isFirst = isFirst;
			this.isLast = isLast;
			
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
			
			boolean isUseOfLeftOrRight = leftOrRightRecursiveNonterminals.contains(symbol.getName());
			Map<String, Integer> labels = headsWithLabeledRules.get(symbol.getName());
			
			if (!isUseOfLeftOrRight && labels == null) return symbol;
			
			boolean isRecursiveUseOfLeftOrRight = isUseOfLeftOrRight && symbol.getName().equals(rule.getHead().getName());
			
			Expression[] arguments = null;
			
			if (isUseOfLeftOrRight)
				arguments = new Expression[] { integer(0), integer(0) };
			
			if (isRecursiveUseOfLeftOrRight && isFirst)
				arguments = new Expression[] { l1, r1 };
			else if (isRecursiveUseOfLeftOrRight && isLast)
				arguments = new Expression[] { l2, r2 };
			
			Expression _not = null;
			
			if (labels != null) {
				int n = 0;
				Set<String> excepts = symbol.getExcepts();
				if (excepts != null)
					for (String except : excepts) {
						Integer i = labels.get(except);
						
						if (i == null)
							throw new RuntimeException("Undeclared label: " + except);
						
						n += 1 << i;
					}
				
				_not = integer(n);
			}
			
			if (arguments != null && _not != null)
				return symbol.copyBuilder().apply(arguments).apply(_not).build();
			else if (arguments != null)
				return symbol.copyBuilder().apply(arguments).build();
			else 
				return symbol.copyBuilder().apply(_not).build();
			
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
		public <E extends Symbol> Symbol visit(Alt<E> symbol) {
			throw new RuntimeException("TODO: Unsupported EBNF while desugaring > and assoc!");
		}

		@Override
		public Symbol visit(Opt symbol) {
			throw new RuntimeException("TODO: Unsupported EBNF while desugaring > and assoc!");
		}

		@Override
		public Symbol visit(Plus symbol) {
			throw new RuntimeException("TODO: Unsupported EBNF while desugaring > and assoc!");
		}

		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			throw new RuntimeException("TODO: Unsupported EBNF while desugaring > and assoc!");
		}

		@Override
		public Symbol visit(Star symbol) {
			throw new RuntimeException("TODO: Unsupported EBNF while desugaring > and assoc!");
		}
		
	}
}
