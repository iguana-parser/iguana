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

package org.iguana.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.condition.ConditionsFactory;
import org.iguana.grammar.exception.IncorrectNumberOfArgumentsException;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.slot.AbstractTerminalTransition;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.CodeTransition;
import org.iguana.grammar.slot.ConditionalTransition;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.EpsilonGrammarSlot;
import org.iguana.grammar.slot.EpsilonTransition;
import org.iguana.grammar.slot.EpsilonTransition.Type;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.NonterminalTransition;
import org.iguana.grammar.slot.ReturnTransition;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.grammar.slot.TerminalTransition;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.slot.lookahead.LookAheadTest;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Position;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parser.gss.lookup.ArrayNodeLookup;
import org.iguana.parser.gss.lookup.DummyNodeLookup;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.parser.gss.lookup.HashMapNodeLookup;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.matcher.DFAMatcherFactory;
import org.iguana.regex.matcher.JavaRegexMatcherFactory;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Configuration.GSSType;
import org.iguana.util.Configuration.LookupImpl;
import org.iguana.util.Configuration.MatcherType;
import org.iguana.util.Input;
import org.iguana.util.collections.IntRangeTree;
import org.iguana.util.collections.RangeTree;

public class GrammarGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	private final Map<String, GrammarSlot> names;
	
	private List<GrammarSlot> slots;
	
	private final FirstFollowSets firstFollow;
	
	Grammar grammar;

	private Configuration config;

	private Input input;
	
	private final Nonterminal layout;
	
	private int id = 1;
	
	private final MatcherFactory matcherFactory;
	
	private final TerminalGrammarSlot epsilonSlot;
	
	public GrammarGraph(Grammar grammar, Input input, Configuration config) {
		this.grammar = grammar;
		this.input = input;
		this.config = config;
		this.layout = grammar.getLayout();
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		this.names = new HashMap<>();
		this.slots = new ArrayList<>();
		
		if (config.getMatcherType() == MatcherType.JAVA_REGEX) {
			matcherFactory = new JavaRegexMatcherFactory();
		} else {
			matcherFactory = new DFAMatcherFactory();
		}
		
		this.firstFollow = new FirstFollowSets(grammar);
		
		epsilonSlot = new TerminalGrammarSlot(0, Epsilon.getInstance(), matcherFactory);
		
		terminalsMap.put(Epsilon.getInstance(), epsilonSlot);

		add(epsilonSlot);
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			getNonterminalGrammarSlot(nonterminal);
		}
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal, grammar);
		}
	}
	
	public NonterminalGrammarSlot getHead(Nonterminal start) {
		if (start instanceof Start) {
			
			NonterminalGrammarSlot s = nonterminalsMap.get(start);
			if (s != null) {
				return s;
			}
			
			Nonterminal nt = ((Start)start).getNonterminal();

			if (layout == null) {
				return nonterminalsMap.get(nt);
			}
			
			Rule startRule = Rule.withHead(start)
								 .addSymbol(layout).addSymbol(nt).addSymbol(layout).build();
			NonterminalGrammarSlot nonterminalGrammarSlot = getNonterminalGrammarSlot(start);
			nonterminalGrammarSlot.setFollowTest(FollowTest.NO_FOLLOW);
			nonterminalGrammarSlot.setLookAheadTest(LookAheadTest.NO_LOOKAYOUT);
			addRule(nonterminalGrammarSlot, startRule);
			return nonterminalGrammarSlot;
		}
		
		return nonterminalsMap.get(start);
	}	
	
	public TerminalGrammarSlot getTerminal(RegularExpression regex) {
		return terminalsMap.get(regex);
	}

	public GrammarSlot getGrammarSlot(String s) {
		return names.get(s);
	}
	
	public Collection<NonterminalGrammarSlot> getNonterminals() {
		return nonterminalsMap.values();
	}
	
	public RegularExpression getRegularExpression(String s) {
		GrammarSlot slot = names.get(s);
		if (!(slot instanceof TerminalGrammarSlot))
			throw new RuntimeException("No regular expression for " + s + " found.");
		return ((TerminalGrammarSlot) names.get(s)).getRegularExpression();
	}
		
	private void convert(Nonterminal nonterminal, Grammar grammar) {
		List<Rule> rules = grammar.getAlternatives(nonterminal);
		NonterminalGrammarSlot nonterminalSlot = getNonterminalGrammarSlot(nonterminal);
		rules.forEach(r -> addRule(nonterminalSlot, r));
		nonterminalSlot.setLookAheadTest(getLookAheadTest(nonterminal, nonterminalSlot));
		nonterminalSlot.setFollowTest(getFollowTest(nonterminal));
	}

	private LookAheadTest getLookAheadTest(Nonterminal nonterminal, NonterminalGrammarSlot nonterminalSlot) {
		
		if (config.getLookAheadCount() == 0)
			return LookAheadTest.NO_LOOKAYOUT;
		
		RangeTree<List<BodyGrammarSlot>> rangeTree = new RangeTree<>();
		
		Map<CharacterRange, List<BodyGrammarSlot>> map = new HashMap<>();
		
		List<Rule> alternatives = grammar.getAlternatives(nonterminal);
		
		for (int i = 0; i < alternatives.size(); i++) {
			Rule rule = alternatives.get(i);
			BodyGrammarSlot firstSlot = nonterminalSlot.getFirstSlots().get(i);
			Set<CharacterRange> set = firstFollow.getPredictionSet(rule, 0);
			set.forEach(cr -> map.computeIfAbsent(cr, k -> new ArrayList<>()).add(firstSlot));			
		}
		
		map.entrySet().forEach(e -> rangeTree.insert(e.getKey(), e.getValue()));
		
		return i -> rangeTree.get(i);
	}
	
	private FollowTest getFollowTest(Nonterminal nonterminal) {
		
		if (config.getLookAheadCount() == 0)
			return FollowTest.NO_FOLLOW;
		
		Set<CharacterRange> followSet = firstFollow.getFollowSet(nonterminal);
		IntRangeTree rangeTree = new IntRangeTree();
		followSet.forEach(cr -> rangeTree.insert(cr, 1));
		
		return i -> rangeTree.get(i) == 1;
	}
	
	private void addRule(NonterminalGrammarSlot head, Rule rule) {
		
		BodyGrammarSlot firstSlot = getFirstGrammarSlot(rule, head);
		head.addFirstSlot(firstSlot);	
		BodyGrammarSlot currentSlot = firstSlot;
		
		GrammarGraphSymbolVisitor rule2graph = new GrammarGraphSymbolVisitor(head, rule, currentSlot);
		
		while (rule2graph.hasNext()) 
			rule2graph.nextSymbol();
	}
	
	private class GrammarGraphSymbolVisitor extends  AbstractGrammarGraphSymbolVisitor {
		
		private final NonterminalGrammarSlot head;
		private final Rule rule;
		
		private BodyGrammarSlot currentSlot;
		private int i = 0;
		
		private int j = -1;
		
		public GrammarGraphSymbolVisitor(NonterminalGrammarSlot head, Rule rule, BodyGrammarSlot currentSlot) {
			this.head = head;
			this.rule = rule;
			this.currentSlot = currentSlot;
		}
		
		public boolean hasNext() {
			return i < rule.size();
		}
		
		public void nextSymbol() {
			j = -1;
			visitSymbol(rule.symbolAt(i));
			i++;
		}
		
		public Void visit(Nonterminal symbol) {
			
			NonterminalGrammarSlot nonterminalSlot = getNonterminalGrammarSlot(symbol);
			
			BodyGrammarSlot slot;
			if (i == rule.size() - 1 && j == -1)
				slot = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), symbol.getVariable());
			else
				slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), symbol.getVariable());
			
			Expression[] arguments = symbol.getArguments();
			
			validateNumberOfArguments(nonterminalSlot.getNonterminal(), arguments);
			
			Set<Condition> preConditions = symbol.getPreConditions();
			currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, arguments, getConditions(preConditions)));
			
			currentSlot = slot;
			
			return null;
		}
		
		@Override
		public Void visit(Conditional symbol) {
			
			Symbol sym = symbol.getSymbol();
			Expression expression = symbol.getExpression();
			
			visitSymbol(sym);
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot));
			currentSlot = thenSlot;
			
			return null;
		}
		
		@Override
		public Void visit(Code symbol) {
			
			Symbol sym = symbol.getSymbol();
			Statement[] statements = symbol.getStatements();
			
			visitSymbol(sym);
			
			BodyGrammarSlot done = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new CodeTransition(statements, currentSlot, done));
			currentSlot = done;
			
			return null;
		}
				
		public Void visit(Return symbol) {
			BodyGrammarSlot done;
			if (i != rule.size() - 1) {
				throw new RuntimeException("Return symbol can only be used at the end of a grammar rule!");
			} else {
				done = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			}
			currentSlot.addTransition(new ReturnTransition(symbol.getExpression(), currentSlot, done));
			currentSlot = done;
			
			return null;
		}
		
		@Override
		public Void visit(RegularExpression symbol) {
			TerminalGrammarSlot terminalSlot = getTerminalGrammarSlot(symbol);
			
			BodyGrammarSlot slot;
			
			if (i == rule.size() - 1 && j == -1)
				slot = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), null);
			else
				slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), null);
			
			Set<Condition> preConditions = symbol.getPreConditions();
			Set<Condition> postConditions = symbol.getPostConditions();
			currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, postConditions));
			currentSlot = slot;
			
			return null;
		}
		
		/**
		 *  Introduces epsilon transitions to handle labels and preconditions/postconditions
		 */
		private void visitSymbol(Symbol symbol) {
			
			if (symbol instanceof Nonterminal || symbol instanceof RegularExpression || symbol instanceof Return) { // TODO: I think this can be unified
				symbol.accept(this);
				return;
			}
			
			j = 0;
			
			if (symbol.getLabel() != null) {
				BodyGrammarSlot declared = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				currentSlot.addTransition(new EpsilonTransition(Type.DECLARE_LABEL, symbol.getLabel(), getConditions(symbol.getPreConditions()), currentSlot, declared));
				currentSlot = declared;
			} else {
				BodyGrammarSlot checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				currentSlot.addTransition(new EpsilonTransition(getConditions(symbol.getPreConditions()), currentSlot, checked));
				currentSlot = checked;
			}
			
			symbol.accept(this);
			
			if (symbol.getLabel() != null) {
				
				BodyGrammarSlot stored;
				if (i == rule.size() - 1)
					stored = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				else
					stored = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				
				currentSlot.addTransition(new EpsilonTransition(Type.STORE_LABEL, symbol.getLabel(), getConditions(symbol.getPostConditions()), currentSlot, stored));
				currentSlot = stored;
			} else {
				
				BodyGrammarSlot checked;
				if (i == rule.size() - 1)
					checked = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				else
					checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				
				currentSlot.addTransition(new EpsilonTransition(getConditions(symbol.getPostConditions()), currentSlot, checked));
				currentSlot = checked;
			}
		}
		
	}
	
	private AbstractTerminalTransition getTerminalTransition(Rule rule, int i, TerminalGrammarSlot slot, 
															 BodyGrammarSlot origin, BodyGrammarSlot dest,
															 Set<Condition> preConditions, Set<Condition> postConditions) {
		
		return new TerminalTransition(slot, origin, dest, getConditions(preConditions), getConditions(postConditions));
	}
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		TerminalGrammarSlot terminalSlot = new TerminalGrammarSlot(id++, regex, matcherFactory);
		add(terminalSlot);
		return terminalsMap.computeIfAbsent(regex, k -> terminalSlot);
	}
	
	private NonterminalGrammarSlot getNonterminalGrammarSlot(Nonterminal nonterminal) {
		return nonterminalsMap.computeIfAbsent(nonterminal, k -> {
			NonterminalGrammarSlot ntSlot;
			if (config.getGSSType() == GSSType.NEW) {
				ntSlot = new NonterminalGrammarSlot(id++, nonterminal, getNodeLookup());			
			} else {
				ntSlot = new NonterminalGrammarSlot(id++, nonterminal, DummyNodeLookup.getInstance());
			}
			add(ntSlot);
			return ntSlot;
		});
	}
	
	private BodyGrammarSlot getFirstGrammarSlot(Rule rule,  NonterminalGrammarSlot nonterminal) {
		BodyGrammarSlot slot;
		
		if (rule.size() == 0) {
			slot = new EpsilonGrammarSlot(id++, rule.getPosition(0,0), nonterminal, epsilonSlot, DummyNodeLookup.getInstance(), ConditionsFactory.DEFAULT, rule.getAction());
		} else {
			// TODO: this is a temporarily solution, which should be re-thought; 
			//       in particular, not any precondition of the first symbol can be moved to the first slot.  
			Set<Condition> preConditions = new HashSet<>();
			preConditions.addAll(rule.symbolAt(0).getPreConditions());
			 
			rule.symbolAt(0).getPreConditions().clear();
			
			slot = new BodyGrammarSlot(id++, rule.getPosition(0,0), DummyNodeLookup.getInstance(), rule.symbolAt(0).getLabel(), null, getConditions(preConditions));
		}
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable) {
		assert i < rule.size();
		
		BodyGrammarSlot slot;
		if (config.getGSSType() == GSSType.NEW)
			slot = new BodyGrammarSlot(id++, position, DummyNodeLookup.getInstance(), label, variable, getConditions(rule.symbolAt(i - 1).getPostConditions()));
		else
			slot = new BodyGrammarSlot(id++, position, getNodeLookup(), label, variable, getConditions(rule.symbolAt(i - 1).getPostConditions()));
		
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getEndGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable) {
		assert i == rule.size();
		
		BodyGrammarSlot slot;
		if (config.getGSSType() == GSSType.NEW)
			slot = new EndGrammarSlot(id++, position, nonterminal, DummyNodeLookup.getInstance(), label, variable, getConditions(rule.symbolAt(i - 1).getPostConditions()), rule.getAction());				
		else
			slot = new EndGrammarSlot(id++, position, nonterminal, getNodeLookup(), label, variable, getConditions(rule.symbolAt(i - 1).getPostConditions()), rule.getAction());
		
		add(slot);
		return slot;
	}

	private void add(GrammarSlot slot) {
		names.put(slot.toString(), slot);
		slots.add(slot);
	}
	
	private GSSNodeLookup getNodeLookup() {
		if (config.getGSSLookupImpl() == LookupImpl.HASH_MAP) {
			return new HashMapNodeLookup();
		} else {
			return new ArrayNodeLookup(input);
		}
	}
	
	static private void validateNumberOfArguments(Nonterminal nonterminal, Expression[] arguments) {
		String[] parameters = nonterminal.getParameters();
		if ((parameters == null && arguments == null) 
				|| (parameters.length == arguments.length)) return;
		
		throw new IncorrectNumberOfArgumentsException(nonterminal, arguments);
	}

	public void reset(Input input) {
		slots.forEach(s -> s.reset(input));
	}

	private Conditions getConditions(Set<Condition> conditions) {
		if (conditions.isEmpty())
			return ConditionsFactory.DEFAULT;
		return ConditionsFactory.getConditions(conditions, matcherFactory);
	}
	
}
