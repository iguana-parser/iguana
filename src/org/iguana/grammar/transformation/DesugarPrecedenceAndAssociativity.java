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
import java.util.Arrays;
import java.util.Collections;
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
import org.iguana.grammar.condition.DataDependentCondition;
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

/**
 * 
 * @author Anastasia Izmaylova
 * 
 */

public class DesugarPrecedenceAndAssociativity implements GrammarTransformation {
	
	private Set<String> leftOrRightRecursiveNonterminals; // when operator precedence and associativity applies
	
	private Map<String, Map<String, Integer>> headsWithLabeledRules; // excepts
	
	private enum OP { _1, _2 }
	
	private OP config_op = OP._1;
	
	private class Configuration {
		
		// Of associativity groups, rules that specify an associativity different from the group 
		public Map<Integer, Set<Integer>> left_assoc_rules = new HashMap<>();
		public Map<Integer, Set<Integer>> right_assoc_rules = new HashMap<>();
		public Map<Integer, Set<Integer>> non_assoc_rules = new HashMap<>();
		
		// Does not include associativity groups inside groups of rules with the same precedence level
		public Map<Integer, Set<Integer>> binary_rules = new HashMap<>();
		public Map<Integer, Set<Integer>> prefix_rules = new HashMap<>();
		public Map<Integer, Set<Integer>> postfix_rules = new HashMap<>();
		
		public Map<Integer, Set<Integer>> ibinary_rules = new HashMap<>();
		public Map<Integer, Set<Integer>> iprefix_rules = new HashMap<>();
		public Map<Integer, Set<Integer>> ipostfix_rules = new HashMap<>();
		
		public Map<String, Set<String>> leftEnds = new HashMap<>();
		public Map<String, Set<String>> rightEnds = new HashMap<>();
		
		// Encoding: 2 is 2 & 1; 3 is 1 & 2; 4 is 2 & 2 (l-value & p-arg)
		public Map<Integer, Integer> groups = new HashMap<>();
	}
	
	private Configuration config;
	
	public void setOP1() {
		config_op = OP._1;
	}
	
	public void setOP2() {
		config_op = OP._2;
	}
	
	@Override
	public Grammar transform(Grammar grammar) {
		
		leftOrRightRecursiveNonterminals = new HashSet<>();
		headsWithLabeledRules = new HashMap<>();
		
		config = new Configuration();
		
		for (Rule rule : grammar.getRules()) {
			config.leftEnds.put(rule.getHead().getName(), rule.getLeftEnds());
			config.rightEnds.put(rule.getHead().getName(), rule.getRightEnds());
		}
		
		for (Rule rule : grammar.getRules()) {
			Nonterminal head = rule.getHead();
			// 1. Precedence
			if (rule.getPrecedenceLevel().getRhs() != 1 || 
					(rule.getPrecedence() == 1 && rule.getAssociativity() != Associativity.UNDEFINED))
				leftOrRightRecursiveNonterminals.add(head.getName());
			// else: all the rules have a precedence -1 (non-recursive), or 1 and
			// undefined associativity; therefore, precedence does not apply
			
			// Decision of the arity of a p-argument and l-value (only for the alternative scheme)
			AssociativityGroup assoc_group = rule.getAssociativityGroup();
			PrecedenceLevel prec_level = rule.getPrecedenceLevel();
			
			boolean isBinary = rule.isLeftRecursive() && rule.isRightRecursive();
			boolean isPrefix = !(rule.isLeftRecursive() || rule.isILeftRecursive()) && rule.isRightRecursive();
			boolean isPostfix = rule.isLeftRecursive() && !(rule.isRightRecursive() || rule.isIRightRecursive());
			
			boolean canBeBinary = ((rule.isLeftRecursive() || rule.isILeftRecursive()) && rule.isIRightRecursive())
									|| (rule.isILeftRecursive() && (rule.isRightRecursive() || rule.isIRightRecursive()));
			
			boolean canBePrefix = false;
			boolean canBePostfix = false;
			
			if (rule.isLeftRecursive() && rule.isIRightRecursive()) {
				if (config.rightEnds.get(rule.getRightEnd()).contains("$"))
					canBePostfix = true;
			}
			
			if (rule.isILeftRecursive() && rule.isRightRecursive()) {
				if (config.leftEnds.get(rule.getRightEnd()).contains("$"))
					canBePrefix = true;
			}
			
			if (rule.isILeftRecursive() && !rule.isRightRecursive() && !rule.isIRightRecursive())
				canBePostfix = true;
			
			if (!rule.isLeftRecursive() && !rule.isILeftRecursive() && rule.isIRightRecursive())
				canBePrefix = true;
			
			if (!rule.isLeftRecursive() && !rule.isLeftRecursive() && rule.isILeftRecursive() && rule.isIRightRecursive()) {
				if (config.leftEnds.get(rule.getRightEnd()).contains("$"))
					canBePrefix = true;
				if (config.rightEnds.get(rule.getRightEnd()).contains("$"))
					canBePostfix = true;
			}
			
			if (isBinary) {
				Set<Integer> rules = config.binary_rules.get(prec_level.getLhs());
				if (rules == null) {
					rules = new HashSet<>();
					config.binary_rules.put(prec_level.getLhs(), rules);
				}
				rules.add(rule.getPrecedence());
			}
			
			if (isPrefix) {
				Set<Integer> rules = config.prefix_rules.get(prec_level.getLhs());
				if (rules == null) {
					rules = new HashSet<>();
					config.prefix_rules.put(prec_level.getLhs(), rules);
				}
				rules.add(rule.getPrecedence());
			}
			
			if (isPostfix) {
				Set<Integer> rules = config.postfix_rules.get(prec_level.getLhs());
				if (rules == null) {
					rules = new HashSet<>();
					config.postfix_rules.put(prec_level.getLhs(), rules);
				}
				rules.add(rule.getPrecedence());
			}
			
			if (canBeBinary) {
				Set<Integer> rules = config.ibinary_rules.get(prec_level.getLhs());
				if (rules == null) {
					rules = new HashSet<>();
					config.ibinary_rules.put(prec_level.getLhs(), rules);
				}
				rules.add(rule.getPrecedence());
			}
			
			if (canBePrefix) {
				Set<Integer> rules = config.iprefix_rules.get(prec_level.getLhs());
				if (rules == null) {
					rules = new HashSet<>();
					config.prefix_rules.put(prec_level.getLhs(), rules);
				}
				rules.add(rule.getPrecedence());
			}
			
			if (canBePostfix) {
				Set<Integer> rules = config.ipostfix_rules.get(prec_level.getLhs());
				if (rules == null) {
					rules = new HashSet<>();
					config.postfix_rules.put(prec_level.getLhs(), rules);
				}
				rules.add(rule.getPrecedence());
			}
			
			if (assoc_group != null) {
				// Inside an associativity group
				
				Associativity assoc = rule.getAssociativity();
				
				if (assoc != assoc_group.getAssociativity() || assoc != Associativity.UNDEFINED) {
					// Rules in an associativity group that override the associativity
					Set<Integer> rules = null;
					
					switch(assoc) {
						case LEFT: 
							rules = config.left_assoc_rules.get(assoc_group.getLhs());
							if (rules == null) {
								rules = new HashSet<>();
								config.left_assoc_rules.put(assoc_group.getLhs(), rules);
							}
							rules.add(rule.getPrecedence());
							break;
						case RIGHT:
							rules = config.right_assoc_rules.get(assoc_group.getLhs());
							if (rules == null) {
								rules = new HashSet<>();
								config.right_assoc_rules.put(assoc_group.getLhs(), rules);
							}
							rules.add(rule.getPrecedence());
							break;
						case NON_ASSOC:
							rules = config.non_assoc_rules.get(assoc_group.getLhs());
							if (rules == null) {
								rules = new HashSet<>();
								config.non_assoc_rules.put(assoc_group.getLhs(), rules);
							}
							rules.add(rule.getPrecedence());
							break;
						default:
					}
				}
				
				boolean climbing = prec_level.getLhs() == assoc_group.getLhs() && prec_level.getRhs() == assoc_group.getRhs();
				
				if (climbing) {				
					if (isBinary && rule.getPrecedence() != assoc_group.getPrecedence())
						config.groups.put(assoc_group.getLhs(), 4);
					if (canBeBinary && rule.getPrecedence() != assoc_group.getPrecedence())
						config.groups.put(assoc_group.getLhs(), 4);
				} else
					config.groups.put(prec_level.getLhs(), 4);
			} 
			
			if (assoc_group == null) {
				if (prec_level.getLhs() != prec_level.getRhs() && (canBeBinary || canBePrefix || canBePostfix)
						&& rule.getAssociativity() != Associativity.UNDEFINED)
					config.groups.put(prec_level.getLhs(), 4);
			}
			
			// 2. Excepts
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
		
		for (Rule rule: grammar.getRules()) {
			
			if (config_op == OP._1) break;
			
			AssociativityGroup assoc_group = rule.getAssociativityGroup();
			PrecedenceLevel prec_level = rule.getPrecedenceLevel();
			
			Integer arity = config.groups.get(prec_level.getLhs());
			
			if (assoc_group != null) {
				
				boolean climbing = prec_level.getLhs() == assoc_group.getLhs() && prec_level.getRhs() == assoc_group.getRhs();
				
				if (climbing) {
					
					if (arity == null || arity != 4) {
						
						boolean hasBinary = config.binary_rules.get(prec_level.getLhs()) != null;
						boolean hasPrefix = config.prefix_rules.get(prec_level.getLhs()) != null;
						boolean hasPostfix = config.postfix_rules.get(prec_level.getLhs()) != null;
						
						boolean canHaveBinary = config.ibinary_rules.get(prec_level.getLhs()) != null;
						boolean canHavePrefix = config.iprefix_rules.get(prec_level.getLhs()) != null;
						boolean canHavePostfix = config.ipostfix_rules.get(prec_level.getLhs()) != null;
						
						if (((hasBinary || canHaveBinary) && (hasPrefix || hasPostfix || canHavePrefix || canHavePostfix)) 
								|| ((hasPrefix || canHavePrefix) && (hasPostfix || canHavePostfix))) {
							Associativity assoc = assoc_group.getAssociativity();
							
							if ((assoc == Associativity.LEFT || assoc == Associativity.NON_ASSOC) && (hasPostfix || canHavePostfix)) {
								for (int group : config.binary_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 3)
											config.groups.put(prec_level.getLhs(), 3);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
								for (int group : config.postfix_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 3)
											config.groups.put(prec_level.getLhs(), 3);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
								
								for (int group : config.ibinary_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 3)
											config.groups.put(prec_level.getLhs(), 3);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
								for (int group : config.ipostfix_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 3)
											config.groups.put(prec_level.getLhs(), 3);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
							}
							if ((assoc == Associativity.RIGHT || assoc == Associativity.NON_ASSOC) && (hasPrefix || canHavePostfix)) {
								for (int group : config.binary_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 2)
											config.groups.put(prec_level.getLhs(), 2);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
								for (int group : config.postfix_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 2)
											config.groups.put(prec_level.getLhs(), 2);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
								
								for (int group : config.ibinary_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 2)
											config.groups.put(prec_level.getLhs(), 2);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
								for (int group : config.ipostfix_rules.keySet()) {
									if (group > rule.getPrecedence()) {
										if (arity == null || arity == 2)
											config.groups.put(prec_level.getLhs(), 2);
										else
											config.groups.put(prec_level.getLhs(), 4);
									}
								}
							}
						}
					}
				}
			} else {
				
				if (arity == null || arity != 4) {
					
					boolean isBinary = rule.isLeftRecursive() && rule.isRightRecursive();
					boolean canBeBinary = ((rule.isLeftRecursive() || rule.isILeftRecursive()) && rule.isIRightRecursive())
											|| (rule.isILeftRecursive() && (rule.isRightRecursive() || rule.isIRightRecursive()));
					
					Set<Integer> binary_rules = config.binary_rules.get(prec_level.getLhs());
					Set<Integer> prefix_rules = config.prefix_rules.get(prec_level.getLhs());
					Set<Integer> postfix_rules = config.postfix_rules.get(prec_level.getLhs());
					
					Set<Integer> ibinary_rules = config.ibinary_rules.get(prec_level.getLhs());
					Set<Integer> iprefix_rules = config.iprefix_rules.get(prec_level.getLhs());
					Set<Integer> ipostfix_rules = config.ipostfix_rules.get(prec_level.getLhs());
					
					int left_rec =   (binary_rules  == null ? 0 : binary_rules.size())
								   + (postfix_rules == null ? 0 : postfix_rules.size());
					
					int right_rec =   (binary_rules == null ? 0 : binary_rules.size())
									+ (prefix_rules == null ? 0 : prefix_rules.size());
					
					int ileft_rec =   (ibinary_rules  == null ? 0 : ibinary_rules.size())
									+ (ipostfix_rules == null ? 0 : ipostfix_rules.size());
			
					int iright_rec =  (ibinary_rules == null ? 0 : ibinary_rules.size())
							 		+ (iprefix_rules == null ? 0 : iprefix_rules.size());
					
					if (rule.getAssociativity() != Associativity.UNDEFINED) {
						
						if(isBinary && (rule.getAssociativity() == Associativity.LEFT || rule.getAssociativity() == Associativity.NON_ASSOC)) {
							if (left_rec + ileft_rec >= 2) {
								Integer n = config.groups.get(prec_level.getLhs());
								if (n == null)
									config.groups.put(prec_level.getLhs(), 3);
								else if (n != 3)
									config.groups.put(prec_level.getLhs(), 4);
							}	
						}
						
						if(isBinary && (rule.getAssociativity() == Associativity.RIGHT || rule.getAssociativity() == Associativity.NON_ASSOC)) {
							if (right_rec + iright_rec >= 2) {
								Integer n = config.groups.get(prec_level.getLhs());
								if (n == null)
									config.groups.put(prec_level.getLhs(), 2);
								else if (n != 2)
									config.groups.put(prec_level.getLhs(), 4);
							}
								
						}
						
						if (!(isBinary || canBeBinary) && rule.isRightRecursive() && rule.getAssociativity() == Associativity.NON_ASSOC) {
							if (left_rec + ileft_rec >= 1) {
								Integer n = config.groups.get(prec_level.getLhs());
								if (n == null)
									config.groups.put(prec_level.getLhs(), 3);
								else if (n != 3)
									config.groups.put(prec_level.getLhs(), 4);
							}
						}
					}
				}
			}
		}
		
		Set<Rule> rules = new LinkedHashSet<>();
		for (Rule rule :grammar.getRules())
			rules.add(transform(rule));
		
		return Grammar.builder().addRules(rules).setLayout(grammar.getLayout()).build();
	}
	
	public Rule transform(Rule rule) {
		return new Visitor(rule, leftOrRightRecursiveNonterminals, headsWithLabeledRules, config, config_op).transform();
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
		
		private final OP config_op;
		
		/*
		 *  Variables of the alternative scheme
		 */
		
		// Priority and associativity related:
		private final Configuration config;
		private int larity = 1;
		private int parity = 1;
		
		private Expression larg = null;
		private Expression rarg = null;
		
		private Expression lcond = null;
		private Expression rcond = null;
		private Expression ret = null;
		
		// ! related:
		private Expression xlcond = null;
		private Expression xrcond = null;
		private Expression xret = null;
		
		public Visitor(Rule rule, Set<String> leftOrRightRecursiveNonterminals, Map<String, Map<String, Integer>> headsWithLabeledRules, 
							Configuration config, OP config_op) {
			this.rule = rule;
			this.leftOrRightRecursiveNonterminals = leftOrRightRecursiveNonterminals;
			this.headsWithLabeledRules = headsWithLabeledRules;
			this.config = config;
			
			Set<Integer> groups = config.groups.keySet();
			
			if (groups.contains(2) || groups.contains(4))
				larity = 2;
			if (groups.contains(3) || groups.contains(4)) 
				parity = 2;
				
			this.config_op = config_op;
			switch(config_op) {
				case _1: excepts1(); precedence1(); break;
				case _2: excepts2(); precedence2(); break;
			}
		}
		
		private void excepts1() {
			if (rule.getLabel() != null) {
				preconditions = new HashSet<>();	
				int l = headsWithLabeledRules.get(rule.getHead().getName()).get(rule.getLabel());
				preconditions.add(predicate(lShiftANDEqZero(var("_not"), integer(l))));
			}
		}
		
		private void excepts2() {
			Map<String, Integer> labels = headsWithLabeledRules.get(rule.getHead().getName());
			
			if (labels == null) return;
			
			if (rule.getLabel() != null) {
				int l = headsWithLabeledRules.get(rule.getHead().getName()).get(rule.getLabel());
				xrcond = lShiftANDEqZero(var("_not"), integer(l));
				xret = integer(l);
			} else 
				xret = integer(-1);
			
			if (rule.isLeftRecursive()) {
				Nonterminal nonterminal = (Nonterminal) rule.getBody().get(0);
				if (nonterminal.getExcepts() != null) {
					int n = 0;
					for (String except : nonterminal.getExcepts()) {
						Integer i = labels.get(except);
							
						if (i == null)
							throw new RuntimeException("Undeclared label: " + except);
							
						n += 1 << i;
					}
					
					if (!leftOrRightRecursiveNonterminals.contains(rule.getHead().getName()))
						xlcond = or(equal(var("l"), integer(-1)), lShiftANDEqZero(integer(n), var("l")));
					else xlcond = or(equal(get(var("l"),integer(1)), integer(-1)), lShiftANDEqZero(integer(n), get(var("l"),integer(1))));
				}
			}
		}
		
		private void precedence1() { // Priority and associativity
			if (rule.getPrecedence() == -1)
				return; // Precedence does not apply
						
			if (!leftOrRightRecursiveNonterminals.contains(rule.getHead().getName()))
				return; // Precedence does not apply
			
			if (preconditions == null)
				preconditions = new HashSet<>();
			
			AssociativityGroup associativityGroup = rule.getAssociativityGroup();
			PrecedenceLevel precedenceLevel = rule.getPrecedenceLevel();
			
			int precedence = rule.getPrecedence();
			Associativity associativity = rule.getAssociativity();
			
			boolean nUseMin = false;
			
			// 1. Expressions for the left and/or right recursive uses
			
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
						r1 = nUseMin? var("r") : pr(precedence, precedenceLevel.postfixUnaryBelow, false);
					else if (precedenceLevel.hasPostfixUnary())
						r1 = integer(first? 0 : precedence);
					else 
						r1 = l1;
					
					if (precedenceLevel.hasPrefixUnaryBelow())
						l2 = nUseMin? var("l") : pr(precedence, precedenceLevel.prefixUnaryBelow, true);
					else if (precedenceLevel.hasPrefixUnary())
						l2 = integer(first? 0 : precedence);
					else 
						l2 = r2;
				} else {
					l1 = integer(precedence);
					r2 = integer(precedence);
					
					// Rule for propagation of a precedence level
					l2 = nUseMin? var("l") : precedenceLevel.hasPrefixUnaryBelow()? pr(precedence, precedenceLevel.prefixUnaryBelow, true) : integer(0);
					r1 = nUseMin? var("r") : precedenceLevel.hasPostfixUnaryBelow()? pr(precedence, precedenceLevel.postfixUnaryBelow, false) : integer(0);
				}
				
			} else if (associativityGroup == null && precedenceLevel.getLhs() == precedenceLevel.getRhs()) { // Can use precedence climbing
				boolean first = precedenceLevel.getUndefined() == 0;
				int il1 = -1; int ir2 = -1;
				switch(associativity) {
					case LEFT:
						il1 = first? 0 : precedence;
						l1 = integer(il1);
						ir2 = precedence + 1;
						r2 = integer(ir2);
						break;
					case RIGHT:
						il1 = precedence + 1;
						l1 = integer(il1);
						ir2 = first? 0 : precedence;
						r2 = integer(ir2);
						break;
					case NON_ASSOC:
						il1 = precedence + 1;
						l1 = integer(il1);
						ir2 = precedence + 1;
						r2 = integer(ir2);
						break;
					case UNDEFINED:
						il1 = first? 0 : precedence;
						l1 = integer(il1);
						ir2 = first? 0 : precedence;
						r2 = integer(ir2);
						break;
					default: throw new RuntimeException("Unexpected associativity: " + associativity);
				}
							
				// Rule for propagation of a precedence level
				if (precedenceLevel.hasPostfixUnaryBelow())
					r1 = nUseMin? var("r") : pr(precedenceLevel.hasPostfixUnary()? precedence : il1, precedenceLevel.postfixUnaryBelow, false);
				else if (precedenceLevel.hasPostfixUnary())
					r1 = integer(first? 0 : precedence);
				else 
					r1 = l1;
							
				if (precedenceLevel.hasPrefixUnaryBelow())
					l2 = nUseMin? var("l") : pr(precedenceLevel.hasPrefixUnary()? precedence : ir2, precedenceLevel.prefixUnaryBelow, true);
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
						l2 = nUseMin? var("l") : precedenceLevel.hasPrefixUnaryBelow()? pr(precedence, precedenceLevel.prefixUnaryBelow, true) : integer(0);
						r1 = nUseMin? var("r") : precedenceLevel.hasPostfixUnaryBelow()? pr(precedence, precedenceLevel.postfixUnaryBelow, false) : integer(useUndefined? undefined : 0);
						break;
					case RIGHT:
						l1 = integer(precedence);
						r2 = integer(useUndefined? undefined : precedence);
						// Rule for propagation of a precedence level
						l2 = nUseMin? var("l") : precedenceLevel.hasPrefixUnaryBelow()? pr(precedence, precedenceLevel.prefixUnaryBelow, true) : integer(useUndefined? undefined : 0);
						r1 = nUseMin? var("r") : precedenceLevel.hasPostfixUnaryBelow()? pr(precedence, precedenceLevel.postfixUnaryBelow, false) : integer(0);
						break;
					case NON_ASSOC:
						l1 = integer(precedence);
						r2 = integer(precedence);
						// Rule for propagation of a precedence level
						l2 = nUseMin? var("l") : precedenceLevel.hasPrefixUnaryBelow()? pr(precedence, precedenceLevel.prefixUnaryBelow, true) : integer(0);
						r1 = nUseMin? var("r") : precedenceLevel.hasPostfixUnaryBelow()? pr(precedence, precedenceLevel.postfixUnaryBelow, false) : integer(0);
						break;
					case UNDEFINED: // Not in the associativity group
						l1 = integer(undefined);
						r2 = integer(undefined);
						// Rule for propagation of a precedence level
						l2 = nUseMin? var("l") : precedenceLevel.hasPrefixUnaryBelow()? pr(precedence, precedenceLevel.prefixUnaryBelow, true) : integer(undefined);
						r1 = nUseMin? var("r") : precedenceLevel.hasPostfixUnaryBelow()? pr(precedence, precedenceLevel.postfixUnaryBelow, false) : integer(undefined);
						break;
					default: throw new RuntimeException("Unexpected associativity: " + associativity);
				}
			}
			
			// 2. Constraints (preconditions) for the grammar rule
			
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
		
		private void precedence2() { // Priority and associativity
						
			if (!leftOrRightRecursiveNonterminals.contains(rule.getHead().getName()))
				return; // Precedence does not apply
			
			if (rule.getPrecedence() == -1) {
				if (this.larity == 1) 
					ret = integer(0);
				else
					ret = tuple(integer(0), integer(0));		
				return; // Precedence does not apply
			}
				
			PrecedenceLevel prec_level = rule.getPrecedenceLevel();
			AssociativityGroup assoc_group = rule.getAssociativityGroup();
			
			int prec = rule.getPrecedence();
			Associativity assoc = rule.getAssociativity();
			
			int undefined = prec_level.getUndefined();
			boolean first = undefined == 0;
			
			boolean labeled = headsWithLabeledRules.containsKey(rule.getHead().getName());
			
			// Either prec or (prec,assoc)
			Expression lprec = null;
			Expression lassoc = null;
			Expression rprec = null;
			Expression pprec = null;
			Expression passoc = null;
			
			if (labeled && larity == 2) { 
				lprec = get(get(var("l"), integer(0)), integer(0));  // l.0.0
				lassoc = get(get(var("l"), integer(0)), integer(1)); // l.0.1
				rprec = get(get(var("r"), integer(0)), integer(0));  // r.0.0
			} else if (labeled && larity != 2) {
				lprec = get(var("l"), integer(0));  // l.0
				lassoc = get(var("l"), integer(0)); // l.0
				rprec = get(var("r"), integer(0));  // l.0
			} else if (!labeled && larity == 2) {
				lprec = get(var("l"), integer(0));  // l.0
				lassoc = get(var("l"), integer(1)); // l.1
				rprec = get(var("r"), integer(0));  // l.0
			} else {
				lprec = var("l");
				lassoc = var("l");
				rprec = var("r");
			}
			
			if (parity == 2) { 
				pprec = get(var("p"), integer(0));  // p.0
				passoc = get(var("p"), integer(1)); // p.1
			} else {
				pprec = var("p");  // p
				passoc = var("p"); // p
			}
			
			if (parity == 2)
				larg = tuple(pprec, integer(0));
			else 
				larg = pprec;
			
			boolean canBeBinary = ((rule.isLeftRecursive() || rule.isILeftRecursive()) && rule.isIRightRecursive())
									|| (rule.isILeftRecursive() && (rule.isRightRecursive() || rule.isIRightRecursive()));
			boolean canBePrefix = false;
			boolean canBePostfix = false;
			
			if (rule.isLeftRecursive() && rule.isIRightRecursive()) {
			if (config.rightEnds.get(rule.getRightEnd()).contains("$"))
				canBePostfix = true;
			}
			
			if (rule.isILeftRecursive() && rule.isRightRecursive()) {
			if (config.leftEnds.get(rule.getRightEnd()).contains("$"))
				canBePrefix = true;
			}
			
			if (rule.isILeftRecursive() && !rule.isRightRecursive() && !rule.isIRightRecursive())
			canBePostfix = true;
			
			if (!rule.isLeftRecursive() && !rule.isILeftRecursive() && rule.isIRightRecursive())
			canBePrefix = true;
			
			if (!rule.isLeftRecursive() && !rule.isLeftRecursive() && rule.isILeftRecursive() && rule.isIRightRecursive()) {
			if (config.leftEnds.get(rule.getRightEnd()).contains("$"))
				canBePrefix = true;
			if (config.rightEnds.get(rule.getRightEnd()).contains("$"))
				canBePostfix = true;
			}
			
			// 1. Expressions for the left and/or right recursive uses
			if (assoc_group != null) {
				
				if (rule.isILeftRecursive() || rule.isIRightRecursive())
					throw new RuntimeException("Not yet implemented: indirect recursion inside an associativity group");
				
				// Local to an associativity group
				int arity = config.groups.get(prec_level.getLhs()) == null? 1 : config.groups.get(prec_level.getLhs());
				int larity = (arity == 2 || arity == 4)? 2 : 1;
				int parity = (arity == 3 || arity == 4)? 2 : 1;
				
				if (assoc_group.getLhs() == prec_level.getLhs() && assoc_group.getRhs() == prec_level.getRhs())
					undefined = assoc_group.getPrecedence();
				
				// ***Climbing condition now: larity == 1 || parity == 1
				
				Expression negative = lessEq(lprec, integer(0));
					
				switch(assoc_group.getAssociativity()) {
				
					case LEFT: // restricts right end
						
						if (rule.isLeftRecursive())
							lcond = or(negative, greaterEq(lprec, integer(prec_level.getLhs())));
						
						if (rule.isLeftRecursive()) {
							
							rcond = greaterEq(integer(prec_level.getRhs()), pprec);
							
							if (parity == 1) {	
								Set<Integer> prefix_rules = config.prefix_rules.get(prec_level.getLhs());
								Set<Integer> non_assoc_rules = config.non_assoc_rules.get(prec_level.getLhs());
								
								if (prefix_rules != null && non_assoc_rules != null) {
									for (int p : prefix_rules)
										if (non_assoc_rules.contains(p))
											rcond = and(rcond, not(equal(pprec, integer(p))));
								}
							} 
							
							if (parity == 2) {
								if (rule.getAssociativity() == Associativity.RIGHT)
									rcond = and(rcond, or(equal(passoc, integer(prec)), 
												          not(and(greaterEq(integer(assoc_group.getRhs()), passoc),
														          greaterEq(passoc, integer(assoc_group.getLhs()))))));
								else
									rcond = and(rcond, not(and(greaterEq(integer(assoc_group.getRhs()), passoc),
														       greaterEq(passoc, integer(assoc_group.getLhs())))));
							}
						}
						
						if (parity == 2) {
							rarg = tuple(integer(prec_level.getRhs()), integer(prec)); // as constrained right end
						} else {
							if (this.parity == 2)
								rarg = tuple(integer(prec_level.getRhs() + 1), integer(0));
							else
								rarg = integer(prec_level.getRhs() + 1);
						}
						
						if (rule.isRightRecursive()) {
							if (prec_level.hasPrefixUnaryBelow()) {
								if (larity == 2)
									ret = tuple(minimum(prec_level.getLhs(), rprec), integer(0)); // as unconstrained left end
								else {
									if (this.larity == 2)
										ret = tuple(minimum(undefined != -1? undefined : prec, rprec), integer(0)); // as unconstrained left end
									else
										ret = minimum(undefined != -1? undefined : prec, rprec); // as unconstrained left end
								}
							} else {
								if (larity == 2)
									ret = tuple(integer(prec_level.getLhs()), integer(0));
								else {
									if (this.larity == 2)
										ret = tuple(integer(undefined != -1? undefined : prec), integer(0));
									else
										ret = integer(undefined != -1? undefined : prec);
								}
							}
						} else { 
							if (this.larity == 2)
								ret = tuple(integer(0), integer(0));
							else
								ret = integer(0);
						}
							
						if (prec != assoc_group.getPrecedence()) {
							
							switch(rule.getAssociativity()) {
							
								case NON_ASSOC:
									if (rule.isLeftRecursive() && rule.isRightRecursive()) {
										
										if (larity == 2)
											lcond = and(or(negative, greaterEq(lprec, integer(prec_level.getLhs()))), 
													    notEqual(lassoc, integer(prec)));
										else
											lcond = or(negative, and(notEqual(lprec, integer(prec)), 
																	 greaterEq(lprec, integer(prec_level.getLhs()))));
										if (parity == 2)
											rarg = tuple(integer(prec_level.getRhs()), 
													     integer(assoc_group.getPrecedence() != -1? assoc_group.getPrecedence() : prec)); // as constrained right end
										
										if (prec_level.hasPrefixUnaryBelow()) {
											if (larity == 2)
												ret = tuple(minimum(prec_level.getLhs(), rprec), integer(prec));
											else {
												if (this.larity == 2)
													ret = tuple(minimum(prec, rprec), integer(0));
												else
													ret = minimum(prec, rprec);
											}
										} else {
											if (larity == 2)
												ret = tuple(integer(prec_level.getLhs()), integer(prec));
											else {
												if (this.larity == 2)
													ret = tuple(integer(prec), integer(0));
												else
													ret = integer(prec);
											}
										}
										
									} else if (rule.isLeftRecursive()) {
										
										if (larity == 2) {
											lcond = and(or(negative, greaterEq(lprec, integer(prec_level.getLhs()))),
                                                        notEqual(lassoc, neg(integer(prec))));
											ret = tuple(integer(0), neg(integer(prec)));
										} else {
											lcond = or(and(negative, notEqual(lprec, neg(integer(prec)))),
													   greaterEq(lprec, integer(prec_level.getLhs())));
											if (this.larity == 2)
												ret = tuple(neg(integer(prec)), integer(0));
											else
												ret = neg(integer(prec));
										}
										
									} else {
										if (parity == 1) {
											if (this.parity == 2)
												rarg = tuple(integer(prec), integer(0));
											else
												rarg = integer(prec);
										}
										
										rcond = notEqual(parity == 2? passoc : pprec, integer(prec));
									}
									break;
								case RIGHT: // larity == 2
									if (rule.isLeftRecursive() && rule.isRightRecursive()) {
										
										lcond = and(or(negative, greaterEq(lprec, integer(prec_level.getLhs()))), 
													notEqual(lassoc, integer(prec)));
										
										if (prec_level.hasPrefixUnaryBelow()) {
											if (larity == 2)
												ret = tuple(minimum(prec_level.getLhs(), rprec), integer(prec));
											else {
												if (this.larity == 2)
													ret = tuple(minimum(prec, rprec), integer(0));
												else
													ret = minimum(prec, rprec);
											}
										} else {
											if (larity == 2)
												ret = tuple(integer(prec_level.getLhs()), integer(prec));
											else {
												if (this.larity == 2)
													ret = tuple(integer(prec), integer(0));
												else
													ret = integer(prec);
											}
										}
									}
									break;
								default: break;
							}
						}
						
						break;
						
					case RIGHT: // restricts left end
						
						if (rule.isLeftRecursive()) {
							
							if (larity == 2) {
								if (rule.getAssociativity() == Associativity.LEFT) {
									lcond = and(or(negative, greaterEq(lprec, integer(prec_level.getLhs()))),
											    or(lessEq(lassoc, integer(0)), 
											       or(equal(lassoc, integer(prec)), 
													  not(and(greaterEq(integer(assoc_group.getRhs()), lassoc),
															  greaterEq(lassoc, integer(assoc_group.getLhs())))))));
								} else
									lcond = and(or(negative, greaterEq(lprec, integer(prec_level.getLhs()))),
										        or(lessEq(lassoc, integer(0)), 
												   not(and(greaterEq(integer(assoc_group.getRhs()), lassoc),
														   greaterEq(lassoc, integer(assoc_group.getLhs()))))));
							} else
								lcond = or(negative, greaterEq(lprec, integer(prec_level.getRhs() + 1)));
						}
						
						if (rule.isLeftRecursive())
							rcond = greaterEq(integer(prec_level.getRhs()), pprec);
						
						if (parity == 2)
							rarg = tuple(integer(prec_level.getRhs()), integer(0)); // as right end is unconstrained
						else {
							if (this.parity == 2)
								rarg = tuple(integer(undefined != -1? undefined : prec), integer(0)); // as right end is unconstrained
							else
								rarg = integer(undefined != -1? undefined : prec); // as right end is unconstrained
						}
						
						if (rule.isRightRecursive()) {
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (larity == 2)
									ret = tuple(minimum(prec_level.getLhs(), rprec), integer(prec)); // as constrained left end
								else {
									if (this.larity == 2)
										ret = tuple(minimum(prec, rprec), integer(0)); // as constrained left end
									else
										ret = minimum(prec, rprec); // as constrained left end
								}
							} else {
								if (larity == 2)
									ret = tuple(integer(prec_level.getLhs()), integer(prec));
								else {
									if (this.larity == 2)
										ret = tuple(integer(prec), integer(0));
									else
										integer(prec);
								}
							}
							
						} else {
							if (parity == 2)
								ret = tuple(integer(0), integer(0));
							else
								ret = integer(0);
						}
						
						if (prec != assoc_group.getPrecedence()) {
							
							switch(rule.getAssociativity()) {
							
								case NON_ASSOC:
									if (rule.isLeftRecursive() && rule.isRightRecursive()) {
										
										if (parity == 2) {
											rarg = tuple(integer(prec_level.getRhs()), integer(prec));
										} else {
											if (this.parity == 2)
												rarg = tuple(integer(prec), integer(0));
											else
												rarg = integer(prec);
										}
										
										rcond = and(greaterEq(integer(prec_level.getRhs()), pprec), 
													not(equal(parity == 2? passoc : pprec, integer(prec))));
										
										int newprec = assoc_group.getPrecedence() != -1? assoc_group.getPrecedence() : prec;
										if (prec_level.hasPrefixUnaryBelow()) {
											if (larity == 2)
												ret = tuple(minimum(prec_level.getLhs(), rprec), integer(newprec));
											else {
												if (this.larity == 2)
													ret = tuple(minimum(newprec, rprec), integer(0));
												else
													ret = minimum(newprec, rprec);
											}
										} else {
											if (larity == 2)
												ret = tuple(integer(prec_level.getLhs()), integer(newprec));
											else {
												if (this.larity == 2)
													ret = tuple(integer(newprec), integer(0));
												else
													integer(newprec);
											}
										}
										
									} else if (rule.isLeftRecursive()) {
										
										if (larity == 2) {
											lcond = and(or(negative, greaterEq(lprec, integer(prec_level.getLhs()))),
									                    or(and(lessEq(lassoc, integer(0)), notEqual(lassoc, neg(integer(prec)))), 
											               and(greater(lassoc, integer(0)),
											            	   not(and(greaterEq(integer(assoc_group.getRhs()), lassoc),
													                   greaterEq(lassoc, integer(assoc_group.getLhs())))))));
											
											ret = tuple(integer(0), neg(integer(prec)));
										} else {
											lcond = or(and(negative, notEqual(lprec, neg(integer(prec)))),
													   greaterEq(lprec, integer(prec_level.getRhs() + 1)));
											if (this.larity == 2)
												ret = tuple(neg(integer(prec)), integer(0));
											else
												ret = neg(integer(prec));
										}
										
									} else {
										
										if (parity == 2)
											rarg = tuple(integer(prec_level.getRhs()), integer(prec));
										else {
											if (this.parity == 2)
												rarg = tuple(integer(prec), integer(0));
											else
												rarg = integer(prec);
										}
										
										rcond = not(equal(parity == 2? passoc : pprec, integer(prec)));
										
										int newprec = assoc_group.getPrecedence() != -1? assoc_group.getPrecedence() : prec;
										if (prec_level.hasPrefixUnaryBelow()) {
											if (larity == 2)
												ret = tuple(minimum(prec_level.getLhs(), rprec), integer(newprec));
											else {
												if (this.larity == 2)
													ret = tuple(minimum(newprec, rprec), integer(0));
												else
													ret = minimum(newprec, rprec);
											}
										} else {
											if (larity == 2)
												ret = tuple(integer(prec_level.getLhs()), integer(newprec));
											else {
												if (this.larity == 2)
													ret = tuple(integer(newprec), integer(0));
												else
													integer(newprec);
											}
										}
									}
									break;
								case LEFT: // parity == 2
									if (rule.isLeftRecursive() && rule.isRightRecursive()) {
										rarg = tuple(integer(prec_level.getRhs()), integer(prec));
										
										rcond = and(greaterEq(integer(prec_level.getRhs()), pprec), 
													not(equal(passoc, integer(prec))));
									}
									break;
								default: break;
							}
						}
						
						break;
					case NON_ASSOC: // restricts both ends (in contrast to other associativity groups applies regardless ends)
						
						if (larity == 1 && parity == 1 && !prec_level.hasPrefixUnary() 
													   && !prec_level.hasPostfixUnary()) {
							
							lcond = or(negative, greaterEq(lprec, integer(prec_level.getRhs() + 1)));
							
							rcond = greaterEq(integer(prec_level.getRhs()), pprec);
							
							if (this.parity == 2)
								rarg = tuple(integer(prec_level.getRhs() + 1), integer(0));
							else
								rarg = integer(prec_level.getRhs() + 1);
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (this.larity == 2)
									ret = tuple(minimum(prec, rprec), integer(0));
								else
									ret = minimum(prec, rprec);
							} else {
								if (this.larity == 2)
									ret = tuple(integer(prec), integer(0));
								else
									ret = integer(prec);
							}
							
						} else {
							
							if (rule.isLeftRecursive()) {
								if (rule.getAssociativity() == Associativity.LEFT) // larity == 2
									lcond = and(or(and(lessEq(lassoc, integer(0)), 
											           not(and(greaterEq(neg(integer(assoc_group.getLhs())), lassoc),
								    		                   greaterEq(lassoc, neg(integer(assoc_group.getRhs())))))),
											       and(greater(lassoc, integer(0)),
											    	   or(equal(lassoc, integer(prec)), 
											    	      not(and(greaterEq(integer(assoc_group.getRhs()), lassoc),
													              greaterEq(lassoc, integer(assoc_group.getLhs()))))))), 
											    or(negative, greaterEq(lprec, integer(prec_level.getLhs()))));
								else
		                            lcond = and(or(and(lessEq(larity == 2? lassoc : lprec, integer(0)), 
											           not(and(greaterEq(neg(integer(assoc_group.getLhs())), larity == 2? lassoc : lprec),
								     		                   greaterEq(larity == 2? lassoc : lprec, neg(integer(assoc_group.getRhs())))))),
											       and(greater(larity == 2? lassoc : lprec, integer(0)),
											    	   not(and(greaterEq(integer(assoc_group.getRhs()), larity == 2? lassoc : lprec),
													           greaterEq(larity == 2? lassoc : lprec, integer(assoc_group.getLhs())))))), 
											    or(negative, greaterEq(lprec, integer(prec_level.getLhs()))));
							}
							
							if (rule.isLeftRecursive()) {
								if (rule.getAssociativity() == Associativity.RIGHT) // parity == 2
									rcond = and(or(equal(passoc, integer(prec)),
											       not(and(greaterEq(integer(assoc_group.getRhs()), passoc), 
													   	   greaterEq(passoc, integer(assoc_group.getLhs()))))),
											    greaterEq(integer(prec_level.getRhs()), pprec));
								else
									rcond = and(not(and(greaterEq(integer(assoc_group.getRhs()), parity == 2? passoc : pprec), 
											            greaterEq(parity == 2? passoc : pprec, integer(assoc_group.getLhs())))),
								                greaterEq(integer(prec_level.getRhs()), pprec));
							}
							
							if (parity == 2) 
								rarg = tuple(integer(prec_level.getRhs()), integer(prec));
							else {
								if (this.parity == 2)
									rarg = tuple(integer(prec), integer(0));
								else
									rarg = integer(prec);
							}
							
							if (rule.isRightRecursive()) {
								if (prec_level.hasPrefixUnaryBelow()) {
									if (larity == 2)
										ret = tuple(minimum(prec_level.getLhs(), rprec), integer(prec));
									else {
										if (this.larity == 2)
											ret = tuple(minimum(prec, rprec), integer(0));
										else
											ret = minimum(prec, rprec);
									}
								} else {
									if (larity == 2)
										ret = tuple(integer(prec_level.getLhs()), integer(prec));
									else {
										if (this.larity == 2)
											ret = tuple(integer(prec), integer(0));
										else
											ret = integer(prec);
									}
								}
							} else {
								if (this.larity == 2)
									ret = tuple(integer(0), integer(0));
								else
									ret = integer(prec);
							}
						}
						
						if (!rule.isLeftRecursive() && rule.isRightRecursive())
							rcond = not(and(greaterEq(integer(assoc_group.getRhs()), parity == 2? passoc : pprec),
									        greaterEq(parity == 2? passoc : pprec, integer(assoc_group.getLhs()))));
						
						if (rule.isLeftRecursive() && !rule.isRightRecursive()) {
							if (larity == 2)
								ret = tuple(integer(0), neg(integer(prec)));
							else {
								if (this.larity == 2)
									ret = tuple(neg(integer(prec)), integer(0));
								else
									ret = neg(integer(prec));
							}
						}
						 
						break;
					default: throw new RuntimeException("Unexpected associativity: " + assoc_group.getAssociativity());
				}
			
			} 
			
			if (assoc_group == null && prec_level.getLhs() == prec_level.getRhs()) { // larity == 1 && parity == 1 
				switch(assoc) {
					case LEFT:
						
						if (rule.isLeftRecursive() || rule.isILeftRecursive()) {
							
							lcond = or(lessEq(lprec, integer(0)), greaterEq(lprec, integer(prec)));
							rcond = greaterEq(integer(prec), pprec);
							
							if (rule.isILeftRecursive() && canBePrefix) {
								// UNDEF
								lcond = or(equal(var("l"), null), lcond);
								rcond = ifThenElse(equal(var("l"), null), TRUE, rcond); // TODO: should become post-condition
							}
						}
						
						if (rule.isRightRecursive() || rule.isIRightRecursive()) {
							if (this.parity == 2)
								rarg = tuple(integer(prec + 1), integer(0));
							else
								rarg = integer(prec + 1);
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (this.larity == 2) {
									
									ret = tuple(minimum(prec, rprec), integer(0));
									
									if (rule.isIRightRecursive() && canBePostfix)
										// UNDEF
										ret = ifThenElse(equal(var("r"), null), tuple(integer(0), integer(0)), ret);
									
								} else {
									ret = minimum(prec, rprec);
									
									if (rule.isIRightRecursive() && canBePostfix)
										// UNDEF
										ret = ifThenElse(equal(var("r"), null), tuple(integer(0), integer(0)), ret);
								}
							} else {
								if (this.larity == 2)
									ret = tuple(integer(prec), integer(0));
								else
									ret = integer(prec);
							}
						} else {
							if (this.larity == 2)
								ret = tuple(integer(0), integer(0));
							else
								ret = integer(0);
						}
						 
						break;
					case RIGHT:
						
						if (rule.isLeftRecursive() || rule.isILeftRecursive()) {
							lcond = or(lessEq(lprec, integer(0)), greaterEq(lprec, integer(prec + 1)));
							rcond = greaterEq(integer(prec), pprec);
							
							if (rule.isILeftRecursive() && canBePrefix) {
								// UNDEF
								lcond = or(equal(var("l"), null), lcond);
								rcond = ifThenElse(equal(var("l"), null), TRUE, rcond); // TODO: should become post-condition
							}
						}
						
						if (rule.isRightRecursive() || rule.isIRightRecursive()) {
							if (this.parity == 2)
								rarg = tuple(integer(first? 0 : prec), integer(0));
							else
								rarg = integer(first? 0 : prec);
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (this.larity == 2) {
									
									ret = tuple(minimum(prec, rprec), integer(0));
									
									if (rule.isIRightRecursive() && canBePostfix)
										// UNDEF
										ret = ifThenElse(equal(var("r"), null), tuple(integer(0), integer(0)), ret);
								} else {
									ret = minimum(prec, rprec);
									
									if (rule.isIRightRecursive() && canBePostfix)
										// UNDEF
										ret = ifThenElse(equal(var("r"), null), tuple(integer(0), integer(0)), ret);
								}
							} else {
								if (this.larity == 2)
									ret = tuple(integer(prec), integer(0));
								else
									ret = integer(prec);
							}
						} else {
							if (this.larity == 2)
								ret = tuple(integer(0), integer(0));
							else
								ret = integer(0);
						}
						
						break;
					case NON_ASSOC:
						if (rule.isLeftRecursive() && rule.isRightRecursive()) {
							
							lcond = or(lessEq(lprec, integer(0)), greaterEq(lprec, integer(prec + 1)));
							rcond = greaterEq(integer(prec), pprec);
							
							if (this.parity == 2)
								rarg = tuple(integer(prec + 1), integer(0));
							else
								rarg = integer(prec + 1);
								
							if (prec_level.hasPrefixUnaryBelow()) {
								if (this.larity == 2)
									ret = tuple(minimum(prec, rprec), integer(0));
								else
									ret = minimum(prec, rprec);
							} else {
								if (this.larity == 2)
									ret = tuple(integer(prec), integer(0));
								else
									ret = integer(prec);
							}
							
						} else if (rule.isLeftRecursive()) {
							lcond = or(and(lessEq(lprec, integer(0)), 
									       notEqual(lprec, neg(integer(prec)))), 
									   greaterEq(lprec, integer(prec)));
							rcond = greaterEq(integer(prec), pprec);
							
							if (this.larity == 2)
								ret = tuple(neg(integer(prec)), integer(0));
							else
								ret = neg(integer(prec));
							
						} else {
							
							rcond = notEqual(pprec, integer(prec));
							
							if (this.parity == 2)
								rarg = tuple(integer(first? 0 : prec), integer(0));
							else
								rarg = integer(first? 0 : prec);
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (this.larity == 2)
									ret = tuple(minimum(prec, rprec), integer(0));
								else
									ret = minimum(prec, rprec);
							} else {
								if (this.larity == 2)
									ret = tuple(integer(prec), integer(0));
								else
									ret = integer(prec);
							}
						}
						
						break;
					case UNDEFINED:
						
						if (rule.isLeftRecursive()) {
							lcond = or(lessEq(lprec, integer(0)), greaterEq(lprec, integer(prec)));
							rcond = greaterEq(integer(prec), pprec);
						}
						
						if (rule.isRightRecursive()) {
							if (this.parity == 2)
								rarg = tuple(integer(first? 0 : prec), integer(0));
							else
								rarg = integer(first? 0 : prec);
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (this.larity == 2)
									ret = tuple(minimum(prec, rprec), integer(0));
								else
									ret = minimum(prec, rprec);
							} else {
								if (this.larity == 2)
									ret = tuple(integer(prec), integer(0));
								else
									ret = integer(prec);
							}
						} else {
							if (this.larity == 2)
								ret = tuple(integer(0), integer(0));
							else
								ret = integer(0);
						} 
						break;
					default: throw new RuntimeException("Unexpected associativity: " + assoc);
				}	
				
			} 
			
			if (assoc_group == null && prec_level.getLhs() != prec_level.getRhs()) {
				
				if (rule.isILeftRecursive() || rule.isIRightRecursive())
					throw new RuntimeException("Not yet implemented: indirect recursion inside a group of the same precedence with multiple rules");
				
				// Local to an associativity group
				int arity = config.groups.get(prec_level.getLhs()) == null? 1 : config.groups.get(prec_level.getLhs());
				int larity = (arity == 2 || arity == 4)? 2 : 1;
				int parity = (arity == 3 || arity == 4)? 2 : 1;
				
				if (rule.isLeftRecursive()) {
					lcond = or(lessEq(lprec, integer(0)), greaterEq(lprec, integer(prec)));
					rcond = greaterEq(integer(prec), pprec);
				}
				
				if (rule.isRightRecursive()) {
					if (parity == 2) 
						rarg = tuple(integer(first? 0 : undefined), integer(0));
					else {
						if (this.parity == 2)
							rarg = tuple(integer(first? 0 : undefined), integer(0));
						else
							rarg = integer(first? 0 : undefined);
					}
					
					if (prec_level.hasPrefixUnaryBelow()) {
						if (larity == 2)
							ret = tuple(minimum(undefined, rprec), integer(0));
						else {
							if (this.larity == 2)
								ret = tuple(minimum(undefined, rprec), integer(0));
							else
								ret = minimum(undefined, rprec);
						}
					} else {
						if (larity == 2)
							ret = tuple(integer(undefined), integer(0));
						else {
							if (this.larity == 2)
								ret = tuple(integer(undefined), integer(0));
							else
								ret = integer(undefined);
						}
					}
				} else {
					if (this.larity == 2)
						ret = tuple(integer(0), integer(0));
					else
						ret = integer(0);
				}
				
				if (rule.getAssociativity() != Associativity.UNDEFINED) {
					
					switch(rule.getAssociativity()) {
					
						case LEFT:
							if (parity == 2) 
								rarg = tuple(integer(first? 0 : undefined), integer(prec));
							else {
								if (this.parity == 2)
									rarg = tuple(integer(prec), integer(0));
								else
									rarg = integer(prec);
							}
							
							rcond = and(greaterEq(integer(prec), pprec),
									    notEqual(parity == 2? passoc : pprec, integer(prec)));
							
							break;
						case RIGHT:
							
							if (prec_level.hasPrefixUnaryBelow()) {
								if (larity == 2)
									ret = tuple(minimum(prec, rprec), integer(0));
								else {
									if (this.larity == 2)
										ret = tuple(minimum(prec, rprec), integer(0));
									else
										ret = minimum(prec, rprec);
								}
							} else {
								if (larity == 2)
									ret = tuple(integer(prec), integer(0));
								else {
									if (this.larity == 2)
										ret = tuple(integer(prec), integer(0));
									else
										ret = integer(prec);
								}
							}
							
							lcond = or(lessEq(lprec, integer(0)), 
									   and(notEqual(larity == 2? lassoc : lprec, integer(prec)), 
										   greaterEq(lprec, integer(prec))));
							
							break;
						case NON_ASSOC:
							
							if (rule.isLeftRecursive() && rule.isRightRecursive()) {
								
								if (parity == 2) 
									rarg = tuple(integer(first? 0 : undefined), integer(prec));
								else {
									if (this.parity == 2)
										rarg = tuple(integer(prec), integer(0));
									else
										rarg = integer(prec);
								}
								
								if (prec_level.hasPrefixUnaryBelow()) {
									if (larity == 2)
										ret = tuple(minimum(prec, rprec), integer(0));
									else {
										if (this.larity == 2)
											ret = tuple(minimum(prec, rprec), integer(0));
										else
											ret = minimum(prec, rprec);
									}
								} else {
									if (larity == 2)
										ret = tuple(integer(prec), integer(0));
									else {
										if (this.larity == 2)
											ret = tuple(integer(prec), integer(0));
										else
											ret = integer(prec);
									}
								}
								
								lcond = or(lessEq(lprec, integer(0)), 
										   and(notEqual(larity == 2? lassoc : lprec, integer(prec)), 
											   greaterEq(lprec, integer(prec))));
								
								rcond = and(greaterEq(integer(prec), pprec),
									        notEqual(parity == 2? passoc : pprec, integer(prec)));
								
							} else if (rule.isLeftRecursive()) {
								
								if (larity == 2) {
									ret = tuple(integer(0), neg(integer(prec)));
									
									lcond = and(or(lessEq(lprec, integer(0)), greaterEq(lprec, integer(prec))),
											    notEqual(lassoc, neg(integer(prec))));
								} else {
									if (this.larity == 2)
										ret = tuple(neg(integer(prec)), integer(0));
									else
										ret = neg(integer(prec));
									
									lcond = or(and(lessEq(lprec, integer(0)), 
											       notEqual(lprec, neg(integer(prec)))), 
											   greaterEq(lprec, integer(prec)));
								}
								
							} else {
								if (parity == 2) 
									rarg = tuple(integer(first? 0 : undefined), integer(prec));
								else {
									if (this.parity == 2)
										rarg = tuple(integer(prec), integer(0));
									else
										rarg = integer(prec);
								}
								
								rcond = notEqual(parity == 2? passoc : pprec, integer(prec));
							}
							
							break;
						
						default: break;
					}
				}	
			}
		}
		
		private static Expression pr(int current, Integer[] indices, boolean prefix) {
			if (prefix) {
				if (indices.length == 0)
					return var("l");
				
				if (indices.length == 1)
					return pr1(var("l"), integer(current), integer(indices[0] + 1));
				
				List<Integer> list = Arrays.asList(indices);
				Collections.reverse(list);
				
				return pr2(var("l"), integer(current), list.stream().map(i -> integer(i + 1)).toArray(Expression[]::new));
				
			} else {
				if (indices.length == 0)
					return var("r");
				
				if (indices.length == 1)
					return pr1(var("r"), integer(current), integer(indices[0] + 1));
				
				List<Integer> list = Arrays.asList(indices);
				Collections.reverse(list);
				
				return pr2(var("r"), integer(current), list.stream().map(i -> integer(i + 1)).toArray(Expression[]::new));
			}
		}
		
		private static Expression minimum(int precedence, Expression rprec) {
			return ifThenElse(lessEq(rprec, integer(0)), 
		     		  		  integer(precedence), 
		     		  		  min(rprec,integer(precedence)));
		}
		
		public Rule transform() {
			
			if (rule.getBody() == null)
				return rule;
			
			List<Symbol> symbols = new ArrayList<>();
			Rule.Builder builder = null;
			
			String head = rule.getHead().getName();
			
			boolean isLeftOrRightRecursiveNonterminal = leftOrRightRecursiveNonterminals.contains(head);
			boolean isHeadWithLabeledRules = headsWithLabeledRules.containsKey(head);
			
			switch(config_op) {
				case _1: 
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
					
					break;
				case _2:
					if (isLeftOrRightRecursiveNonterminal && isHeadWithLabeledRules)
						builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("p", "_not").build());
					else if (isLeftOrRightRecursiveNonterminal)
						builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("p").build());
					else if (isHeadWithLabeledRules)
						builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("_not").build());
					else builder = rule.copyBuilder();
					
					builder = builder.setSymbols(symbols);
					
					i = 0;
					for (Symbol symbol : rule.getBody()) {
						
						if (i == 0) isFirst = true;
						else isFirst = false;
						
						if (i == rule.getBody().size() - 1) isLast = true;
						else isLast = false;
						
						Symbol sym = symbol.accept(this);
						
						Set<Condition> preconditions = new HashSet<>();
						Set<Condition> postconditions = new HashSet<>();
						
						if (rcond != null && i == 0)
							preconditions.add(DataDependentCondition.predicate(rcond));
						
						if (xrcond != null && i == 0)
							preconditions.add(DataDependentCondition.predicate(xrcond));
						
						if (lcond != null && i == 0)
							postconditions.add(DataDependentCondition.predicate(lcond));
						
						if (xlcond != null && i == 0)
							postconditions.add(DataDependentCondition.predicate(xlcond));
						
						if (!preconditions.isEmpty() && !postconditions.isEmpty())
							symbols.add(sym.copyBuilder().addPreConditions(preconditions).addPostConditions(postconditions).build());
						else if (!postconditions.isEmpty())
							symbols.add(sym.copyBuilder().addPostConditions(postconditions).build());
						else if (!preconditions.isEmpty())
							symbols.add(sym.copyBuilder().addPreConditions(preconditions).build());
						else 
							symbols.add(sym);
						
						i++;
					}
					
					if (ret != null && xret != null)
						symbols.add(Return.ret(tuple(ret,xret)));
					else if (ret != null)
						symbols.add(Return.ret(ret));
					else if (xret != null)
						symbols.add(Return.ret(xret));
				    
					break;
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
			
			Expression _not = null;
			Expression[] arguments = null;
			
			switch(config_op) {
				case _1:
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
					
					if (isUseOfLeftOrRight)
						arguments = new Expression[] { integer(0), integer(0) };
					
					if (isRecursiveUseOfLeftOrRight && isFirst)
						arguments = new Expression[] { l1, r1 };
					else if (isRecursiveUseOfLeftOrRight && isLast)
						arguments = new Expression[] { l2, r2 };
					
					if (arguments != null && _not != null)
						return symbol.copyBuilder().apply(arguments).apply(_not).build();
					else if (arguments != null)
						return symbol.copyBuilder().apply(arguments).build();
					else 
						return symbol.copyBuilder().apply(_not).build();
					
				case _2:
					if (labels != null) {
						if (rule.isLeftRecursive() && isFirst) {
							_not = integer(0);
						} else {
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
					}
					
					if (isUseOfLeftOrRight)
						arguments = new Expression[] { integer(0) };
					
					String variable = "";
					
					if (isRecursiveUseOfLeftOrRight && isFirst) {
						variable = "l";
						arguments = new Expression[] { larg };
					} else if (isRecursiveUseOfLeftOrRight && isLast) {
						variable = "r";
						arguments = new Expression[] { rarg };
					}
					
					// Case of a left-recursive nonterminal that does not specify precedence
					if (labels != null && symbol.getExcepts() != null && rule.isLeftRecursive() && isFirst) 
						variable = "l";
					
					if (arguments != null && _not != null)
						return variable.isEmpty()? symbol.copyBuilder().apply(arguments).apply(_not).build()
												 : symbol.copyBuilder().apply(arguments).apply(_not).setVariable(variable).build();
					else if (arguments != null)
						return variable.isEmpty()? symbol.copyBuilder().apply(arguments).build()
												 : symbol.copyBuilder().apply(arguments).setVariable(variable).build();
					else 
						return variable.isEmpty()? symbol.copyBuilder().apply(_not).build()
								                 : symbol.copyBuilder().apply(_not).setVariable(variable).build();
			}
			
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
			// TODO: support for return
			return null;
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
