package org.jgll.grammar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import org.jgll.grammar.symbol.Position;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.gss.lookup.ArrayNodeLookup;
import org.jgll.parser.gss.lookup.DummyNodeLookup;
import org.jgll.parser.gss.lookup.GSSNodeLookup;
import org.jgll.parser.gss.lookup.HashMapNodeLookup;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupImpl;
import org.jgll.util.Input;

public class GrammarGraph {

	private Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	private Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	private Map<String, GrammarSlot> names;
	
	private Configuration config;

	private Input input;
	
	private final Nonterminal layout;
	
	private int id = 1;
	
	private TerminalGrammarSlot epsilon = new TerminalGrammarSlot(0, Epsilon.getInstance());
	
	public GrammarGraph(Grammar grammar, Input input, Configuration config) {
		this.input = input;
		this.config = config;
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		this.names = new HashMap<>();
		this.layout = grammar.getLayout();
		
		terminalsMap.put(Epsilon.getInstance(), epsilon);
		names.put(Epsilon.getInstance().getName(), epsilon);
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal, grammar);
		}
	}
	
	public NonterminalGrammarSlot getHead(Nonterminal start) {
		if (start instanceof Start) {
			Nonterminal nt = ((Start)start).getNonterminal();

			if (layout == null) {
				return nonterminalsMap.get(nt);
			}
			
			Rule startRule = Rule.withHead(start)
								 .addSymbol(layout).addSymbol(nt).addSymbol(layout).build();
			NonterminalGrammarSlot nonterminalGrammarSlot = getNonterminalGrammarSlot(start);
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
	}
	
	private void addRule(NonterminalGrammarSlot head, Rule rule) {
		
		BodyGrammarSlot firstSlot = getFirstGrammarSlot(rule, head);
		head.addFirstSlot(firstSlot);
		
		BodyGrammarSlot currentSlot = firstSlot;
		
		for (int i = 0; i < rule.size(); i++) {
			Symbol symbol = rule.symbolAt(i);
			
			// Terminal
			if (symbol instanceof RegularExpression) {
				RegularExpression regex = (RegularExpression) symbol;
				TerminalGrammarSlot terminalSlot = getTerminalGrammarSlot(regex);
				BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head);
				Set<Condition> preConditions = symbol.getPreConditions();
				Set<Condition> postConditions = symbol.getPostConditions();
				currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, postConditions));
				currentSlot = slot;
			} 
			else if (symbol instanceof Nonterminal) {
				Nonterminal nonterminal = (Nonterminal) symbol;
				NonterminalGrammarSlot nonterminalSlot = getNonterminalGrammarSlot(nonterminal);
				BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head);
				Set<Condition> preConditions = symbol.getPreConditions();
				currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, preConditions));
				currentSlot = slot;
			}

//				currentSlot = addLayout(currentSlot, rule, i);
		}
	}

//	private BodyGrammarSlot addLayout(BodyGrammarSlot currentSlot, Rule rule, int i) {
//		if (rule.hasLayout() && rule.size() > 1) {
//			NonterminalGrammarSlot layout = getNonterminalGrammarSlot(rule.getLayout());
//			BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, new LayoutPosition(rule.getPosition(i + 1)), layout);
//			currentSlot.addTransition(new NonterminalTransition(layout, currentSlot, slot, Collections.emptySet()));
//			return slot;
//		}
//		return currentSlot;
//	}
	
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
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		TerminalGrammarSlot terminalSlot = new TerminalGrammarSlot(id++, regex);
		names.put(terminalSlot.toString(), terminalSlot);
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
			names.put(ntSlot.toString(), ntSlot);
			return ntSlot;
		});
	}
	
	private BodyGrammarSlot getFirstGrammarSlot(Rule rule,  NonterminalGrammarSlot nonterminal) {
		BodyGrammarSlot slot;
		
		if (rule.size() == 0) {
			slot = new EpsilonGrammarSlot(id++, rule.getPosition(0), nonterminal, epsilon, DummyNodeLookup.getInstance(), Collections.emptySet());
		} else {
			slot = new BodyGrammarSlot(id++, rule.getPosition(0), DummyNodeLookup.getInstance(), rule.symbolAt(0).getPostConditions());
		}
		
		names.put(slot.toString(), slot);
		return slot;
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal) {
		BodyGrammarSlot slot;
		if (i == rule.size()) {
			if (config.getGSSType() == GSSType.NEW) {
				slot = new EndGrammarSlot(id++, position, nonterminal, DummyNodeLookup.getInstance(), rule.symbolAt(i - 1).getPostConditions());				
			} else {
				slot = new EndGrammarSlot(id++, position, nonterminal, getNodeLookup(), rule.symbolAt(i - 1).getPostConditions());
			}
		} else {
			if (config.getGSSType() == GSSType.NEW) {
				// With new GSS we don't lookup in body grammarSlots
				slot = new BodyGrammarSlot(id++, position, DummyNodeLookup.getInstance(), rule.symbolAt(i - 1).getPostConditions());
			} else {
				slot = new BodyGrammarSlot(id++, position, getNodeLookup(), rule.symbolAt(i - 1).getPostConditions());				
			}
		}
		names.put(slot.toString(), slot);
		return slot;
	}
	
	private GSSNodeLookup getNodeLookup() {
		if (config.getGSSLookupImpl() == LookupImpl.HASH_MAP) {
			return new HashMapNodeLookup();
		} else {
			return new ArrayNodeLookup(input);
		}
	}

}
