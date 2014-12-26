package org.jgll.grammar;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Keyword;
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
		popActions.clear();
		
		NonterminalGrammarSlot nonterminalSlot = getHeadGrammarSlot(head);
		
		int alternateIndex = 0;
		
		for(Rule rule : rules) {
			
			if(rule == null) {
				alternateIndex++;
				continue;
			}
			
			addAlternative(nonterminalSlot, rule);
			
			GrammarSlot currentSlot = null;
	
			alternateIndex++;
		}
	}
	
	private void addAlternative(NonterminalGrammarSlot head, Rule rule) {
		if (rule.size() == 0) {
			EpsilonGrammarSlot epsilonSlot = new EpsilonGrammarSlot(head);
			slots.put(epsilonSlot.toString(), epsilonSlot);
		} 
		else {

			for (Symbol symbol : rule.getBody()) {
				
			}
			
			GrammarSlot firstSlot = null;
			int symbolIndex = 0;
			for (; symbolIndex < body.size(); symbolIndex++) {
				
				currentSlot = getBodyGrammarSlot(new Rule(head, body), symbolIndex, currentSlot);
				slots.put(currentSlot.toString(), currentSlot);

				if (symbolIndex == 0) {
					firstSlot = currentSlot;
				}
			}

			EndGrammarSlot lastSlot = grammarSlotFactory.createLastGrammarSlot(new Rule(head, body), symbolIndex, currentSlot, nonterminalSlot, popActions);
			slots.put(lastSlot.toString(), lastSlot);
			nonterminalSlot.setFirstGrammarSlotForAlternate(firstSlot, alternateIndex);
		}
	}
	
	Set<Condition> popActions = new HashSet<>();
	
	private GrammarSlot getBodyGrammarSlot(Rule rule, int symbolIndex, GrammarSlot currentSlot) {
		
		Symbol symbol = rule.getBody().get(symbolIndex);
		
		if(symbol instanceof RegularExpression) {
			RegularExpression regex = (RegularExpression) symbol;
			
			Set<Condition> preConditionsTest = symbol.getConditions();
			Set<Condition> postConditionsTest = symbol.getConditions().stream().filter(c -> c.getType() != ConditionType.NOT_MATCH).collect(Collectors.toSet());
			
			return grammarSlotFactory.createTokenGrammarSlot(rule, symbolIndex, currentSlot, getTerminalGrammarSlot(regex), preConditionsTest, postConditionsTest, popActions);
		}
		
		// Nonterminal
		else {
			popActions = new HashSet<>(symbol.getConditions());
			
			NonterminalGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return grammarSlotFactory.createNonterminalGrammarSlot(rule, symbolIndex, currentSlot, nonterminal, symbol.getConditions(), popActions);						
		}		
	}

	private NonterminalGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		return nonterminalsMap.computeIfAbsent(nonterminal, k -> grammarSlotFactory.createHeadGrammarSlot(nonterminal, grammar.getAlternatives(nonterminal), grammar.getFirstSets(), grammar.getFollowSets(), grammar.getPredictionSets()));		
	}
	
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		return terminalsMap.computeIfAbsent(regex, k -> new TerminalGrammarSlot(regex));
	}
		
}