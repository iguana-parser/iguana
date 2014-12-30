package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
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
import org.jgll.regex.RegularExpression;

public class GrammarGraphBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	List<GrammarSlot> slots;

	String name;
	
	Grammar grammar;
	
	public GrammarGraphBuilder(Grammar grammar) {
		this("no-name", grammar);
	}
	
	public GrammarGraphBuilder(String name, Grammar grammar) {
		this.name = name;
		this.grammar = grammar;
		this.slots = new ArrayList<>();
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		terminalsMap.put(Epsilon.getInstance(), new TerminalGrammarSlot(Epsilon.getInstance()));
	}

	public GrammarGraph build() {
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal);
		}
		return new GrammarGraph(this);
	}
	
	private void convert(Nonterminal nonterminal) {
		List<Rule> rules = grammar.getAlternatives(nonterminal);
		NonterminalGrammarSlot nonterminalSlot = nonterminalsMap.computeIfAbsent(nonterminal, k -> new NonterminalGrammarSlot(nonterminal));
		rules.forEach(r -> addAlternative(nonterminalSlot, r));
	}
	
	private void addAlternative(NonterminalGrammarSlot head, Rule rule) {
		
		if (rule.size() == 0) {
			EpsilonGrammarSlot epsilonSlot = new EpsilonGrammarSlot(rule.getPosition(0), head);
			head.addFirstSlot(epsilonSlot);
			slots.add(epsilonSlot);
		} 
		else {
			
			BodyGrammarSlot firstSlot = new BodyGrammarSlot(rule.getPosition(0));
			head.addFirstSlot(firstSlot);
			
			GrammarSlot currentSlot = firstSlot;
			
			for (int i = 0; i < rule.size(); i++) {
				Symbol symbol = rule.symbolAt(i);
				
				// Terminal
				if (symbol instanceof RegularExpression) {
					RegularExpression regex = (RegularExpression) symbol;
					TerminalGrammarSlot terminalSlot = terminalsMap.computeIfAbsent(regex, k -> new TerminalGrammarSlot(regex));
					GrammarSlot slot = getBodyGrammarSlot(rule, i + 1, head);
					Set<Condition> preConditions = symbol.getPreConditions();
					Set<Condition> postConditions = symbol.getPreConditions().stream().filter(c -> c.getType() != ConditionType.NOT_MATCH).collect(Collectors.toSet());
					currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, postConditions));
					currentSlot = slot;
				} 
				else if (symbol instanceof Nonterminal) {
					Nonterminal nonterminal = (Nonterminal) symbol;
					NonterminalGrammarSlot nonterminalSlot = nonterminalsMap.computeIfAbsent(nonterminal, k -> new NonterminalGrammarSlot(nonterminal));
					GrammarSlot slot = getBodyGrammarSlot(rule, i + 1, head);
					Set<Condition> preConditions = symbol.getPreConditions();
					currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, preConditions));
					currentSlot = slot;
				}
				
				slots.add(currentSlot);
			}		
		}
	}
	
	private AbstractTerminalTransition getTerminalTransition(Rule rule, int i, TerminalGrammarSlot slot, 
															 GrammarSlot origin, GrammarSlot dest,
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
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, NonterminalGrammarSlot nonterminal) {
		if (i == rule.size()) {
			return new EndGrammarSlot(rule.getPosition(i), nonterminal);
		} else {
			return new BodyGrammarSlot(rule.getPosition(i));
		}
	}
	
}