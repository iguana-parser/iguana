package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.AbstractTerminalTransition;
import org.jgll.grammar.slot.BeforeLastTerminalTransition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.FirstAndLastTerminalTransition;
import org.jgll.grammar.slot.FirstTerminalTransition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.NonterminalTransition;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slot.TerminalTransition;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.gss.lookup.ArrayNodeLookup;
import org.jgll.parser.gss.lookup.DummyNodeLookup;
import org.jgll.parser.gss.lookup.HashMapNodeLookup;
import org.jgll.parser.gss.lookup.GSSNodeLookup;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupImpl;
import org.jgll.util.Input;

public class GrammarGraphBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	List<GrammarSlot> slots;

	String name;
	
	Grammar grammar;

	private Configuration config;

	private Input input;
	
	private int id = 1;
	
	private TerminalGrammarSlot epsilon = new TerminalGrammarSlot(0, Epsilon.getInstance());
	
	public GrammarGraphBuilder(Grammar grammar, Input input, Configuration config) {
		this("no-name", grammar, input, config);
	}
	
	public GrammarGraphBuilder(String name, Grammar grammar, Input input, Configuration config) {
		this.name = name;
		this.grammar = grammar;
		this.input = input;
		this.config = config;
		this.slots = new ArrayList<>();
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		terminalsMap.put(Epsilon.getInstance(), epsilon);
	}

	public GrammarGraph build() {
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal);
		}
		return new GrammarGraph(this);
	}
	
	private void convert(Nonterminal nonterminal) {
		List<Rule> rules = grammar.getAlternatives(nonterminal);
		NonterminalGrammarSlot nonterminalSlot = nonterminalsMap.computeIfAbsent(nonterminal, k -> new NonterminalGrammarSlot(id++, nonterminal, getNodeLookup()));
		rules.forEach(r -> addAlternative(nonterminalSlot, r));
	}
	
	private void addAlternative(NonterminalGrammarSlot head, Rule rule) {
		
		if (rule.getBody() == null)
			return;
		
		if (rule.size() == 0) {
			EpsilonGrammarSlot epsilonSlot = new EpsilonGrammarSlot(id, rule.getPosition(0), head, epsilon, DummyNodeLookup.getInstance(), Collections.emptySet());
			head.addFirstSlot(epsilonSlot);
			slots.add(epsilonSlot);
		} 
		else {
			
			BodyGrammarSlot firstSlot = new BodyGrammarSlot(id, rule.getPosition(0), getNodeLookup(), Collections.emptySet());
			head.addFirstSlot(firstSlot);
			
			BodyGrammarSlot currentSlot = firstSlot;
			
			for (int i = 0; i < rule.size(); i++) {
				Symbol symbol = rule.symbolAt(i);
				
				// Terminal
				if (symbol instanceof RegularExpression) {
					RegularExpression regex = (RegularExpression) symbol;
					TerminalGrammarSlot terminalSlot = terminalsMap.computeIfAbsent(regex, k -> new TerminalGrammarSlot(id++, regex));
					BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, head);
					Set<Condition> preConditions = symbol.getPreConditions();
					Set<Condition> postConditions = symbol.getPostConditions();
					currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, postConditions));
					currentSlot = slot;
				} 
				else if (symbol instanceof Nonterminal) {
					Nonterminal nonterminal = (Nonterminal) symbol;
					NonterminalGrammarSlot nonterminalSlot = nonterminalsMap.computeIfAbsent(nonterminal, k -> getNonterminalGrammarSlot(nonterminal));
					BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, head);
					Set<Condition> preConditions = symbol.getPreConditions();
					currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, preConditions));
					currentSlot = slot;
				}
				
				slots.add(currentSlot);
			}		
		}
	}

	private AbstractTerminalTransition getTerminalTransition(Rule rule, int i, TerminalGrammarSlot slot, 
															 BodyGrammarSlot origin, BodyGrammarSlot dest,
															 Set<Condition> preConditions, Set<Condition> postConditions) {
		
		if (i == 1 && rule.size() > 1) {
			return new FirstTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else if (i == 1 && rule.size() == 1) {
			return new FirstAndLastTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else if (i == rule.size())  {
			return new BeforeLastTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else {
			return new TerminalTransition(slot, origin, dest, preConditions, postConditions);
		}
	}
	
	private NonterminalGrammarSlot getNonterminalGrammarSlot(Nonterminal nonterminal) {
		if (config.getGSSType() == GSSType.NEW) {
			return new NonterminalGrammarSlot(id++, nonterminal, getNodeLookup());			
		} else {
			return new NonterminalGrammarSlot(id++, nonterminal, DummyNodeLookup.getInstance());
		}
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, NonterminalGrammarSlot nonterminal) {
		if (i == rule.size()) {
			if (config.getGSSType() == GSSType.NEW) {
				return new EndGrammarSlot(id++, rule.getPosition(i), nonterminal, DummyNodeLookup.getInstance(), rule.symbolAt(i - 1).getPostConditions());				
			} else {
				return new EndGrammarSlot(id++, rule.getPosition(i), nonterminal, getNodeLookup(), rule.symbolAt(i - 1).getPostConditions());
			}
		} else {
			if (config.getGSSType() == GSSType.NEW) {
				// With new GSS we don't lookup in body grammarSlots
				return new BodyGrammarSlot(id++, rule.getPosition(i), DummyNodeLookup.getInstance(), rule.symbolAt(i - 1).getPostConditions());
			} else {
				return new BodyGrammarSlot(id++, rule.getPosition(i), getNodeLookup(), rule.symbolAt(i - 1).getPostConditions());				
			}
		}
	}
	
	private GSSNodeLookup getNodeLookup() {
		if (config.getGSSLookupImpl() == LookupImpl.HASH_MAP) {
			return new HashMapNodeLookup();
		} else {
			return new ArrayNodeLookup(input);
		}
	}
	
}