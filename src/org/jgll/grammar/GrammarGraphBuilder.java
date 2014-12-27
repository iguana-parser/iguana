package org.jgll.grammar;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
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
import org.jgll.util.logging.LoggerWrapper;

public class GrammarGraphBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarGraphBuilder.class);

	Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	Map<String, GrammarSlot> slots;

	String name;
	
	Grammar grammar;
	
	public GrammarGraphBuilder(Grammar grammar) {
		this("no-name", grammar);
	}
	
	public GrammarGraphBuilder(String name, Grammar grammar) {
		this.name = name;
		this.grammar = grammar;
		this.slots = new LinkedHashMap<>();
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		terminalsMap.put(Epsilon.getInstance(), new TerminalGrammarSlot(Epsilon.getInstance()));
	}

	public GrammarGraph build() {
		
		long start = System.nanoTime();
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal);
		}

		long end = System.nanoTime();
		log.info("Grammar Graph is composed in %d ms", (end - start) / 1000_000);
				
		return new GrammarGraph(this);
	}
	
	private void convert(Nonterminal head) {
		
		List<Rule> rules = grammar.getAlternatives(head);
		
		NonterminalGrammarSlot nonterminalSlot = nonterminalsMap.computeIfAbsent(head, k -> new NonterminalGrammarSlot(head));
		
		rules.forEach(r -> addAlternative(nonterminalSlot, r));
	}
	
	private void addAlternative(NonterminalGrammarSlot head, Rule rule) {
		if (rule.size() == 0) {
			EpsilonGrammarSlot epsilonSlot = new EpsilonGrammarSlot(head);
			slots.put(epsilonSlot.toString(), epsilonSlot);
		} 
		else {
			
			GrammarSlot currentSlot = head;
			
			for (Symbol symbol : rule.getBody()) {
				// Terminal
				if (symbol instanceof RegularExpression) {
					RegularExpression regex = (RegularExpression) symbol;
					TerminalGrammarSlot terminalSlot = terminalsMap.computeIfAbsent(regex, k -> new TerminalGrammarSlot(regex));
					GrammarSlot slot = new BodyGrammarSlot();
					Set<Condition> preConditions = symbol.getPreConditions();
					Set<Condition> postConditions = symbol.getPreConditions().stream().filter(c -> c.getType() != ConditionType.NOT_MATCH).collect(Collectors.toSet());
					currentSlot.addTransition(new TerminalTransition(terminalSlot, currentSlot, slot, preConditions, postConditions));
					currentSlot = slot;
					
				} 
				else if (symbol instanceof Nonterminal) {
					Nonterminal nonterminal = (Nonterminal) symbol;
					NonterminalGrammarSlot nonterminalSlot = nonterminalsMap.computeIfAbsent(nonterminal, k -> new NonterminalGrammarSlot(nonterminal));
					GrammarSlot slot = new BodyGrammarSlot();
					Set<Condition> preConditions = symbol.getPreConditions();
					currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, preConditions));
					currentSlot = slot;
				}
				
				slots.put(currentSlot.toString(), currentSlot);
			}			
		}
	}
	
}