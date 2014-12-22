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
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
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

	Map<Nonterminal, HeadGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	Map<String, BodyGrammarSlot> slots;

	String name;
	
	Grammar grammar;
	
	private GrammarSlotFactory grammarSlotFactory;
	
	public GrammarGraphBuilder(Grammar grammar, GrammarSlotFactory grammarSlotFactory) {
		this("no-name", grammar, grammarSlotFactory);
	}
	
	public GrammarGraphBuilder(String name, Grammar grammar, 
							   GrammarSlotFactory grammarSlotFactory) {
		this.name = name;
		this.grammarSlotFactory = grammarSlotFactory;
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
		List<List<Symbol>> alternates = grammar.getAlternatives(head);
		popActions.clear();
		
		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);
		
		int alternateIndex = 0;
		
		for(List<Symbol> body : alternates) {
			
			if(body == null) {
				alternateIndex++;
				continue;
			}
			
			BodyGrammarSlot currentSlot = null;
	
			if (body.size() == 0) {
				EpsilonGrammarSlot epsilonSlot = grammarSlotFactory.createEpsilonGrammarSlot(headGrammarSlot);
				slots.put(epsilonSlot.toString(), epsilonSlot);
				headGrammarSlot.setFirstGrammarSlotForAlternate(epsilonSlot, alternateIndex);
			} 
			else {
				BodyGrammarSlot firstSlot = null;
				int symbolIndex = 0;
				for (; symbolIndex < body.size(); symbolIndex++) {
					
					currentSlot = getBodyGrammarSlot(new Rule(head, body), symbolIndex, currentSlot);
					slots.put(currentSlot.toString(), currentSlot);
	
					if (symbolIndex == 0) {
						firstSlot = currentSlot;
					}
				}
	
				LastGrammarSlot lastSlot = grammarSlotFactory.createLastGrammarSlot(new Rule(head, body), symbolIndex, currentSlot, headGrammarSlot, popActions);
				slots.put(lastSlot.toString(), lastSlot);
				headGrammarSlot.setFirstGrammarSlotForAlternate(firstSlot, alternateIndex);
			}
			alternateIndex++;
		}
	}
	
	Set<Condition> popActions = new HashSet<>();
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int symbolIndex, BodyGrammarSlot currentSlot) {
		
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
			
			HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return grammarSlotFactory.createNonterminalGrammarSlot(rule, symbolIndex, currentSlot, nonterminal, symbol.getConditions(), popActions);						
		}		
	}

	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		return nonterminalsMap.computeIfAbsent(nonterminal, k -> grammarSlotFactory.createHeadGrammarSlot(nonterminal, grammar.getAlternatives(nonterminal), grammar.getFirstSets(), grammar.getFollowSets(), grammar.getPredictionSets()));		
	}
	
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		return terminalsMap.computeIfAbsent(regex, k -> new TerminalGrammarSlot(regex));
	}
		
	/**
	 * Creates the corresponding grammar rule for the given keyword.
	 * For example, for the keyword "if", a rule If ::= [i][f]
	 * is returned.
	 * 
	 * @param keyword
	 * @return
	 */
	public static Rule fromKeyword(Keyword keyword) {
		Rule.Builder builder = new Rule.Builder(Nonterminal.withName(keyword.getName()));
		for(Character c : keyword.getSequence()) {
			builder.addSymbol(c);
		}
		return builder.build();
	}

	
	
//	/**
//	 * Removes non-reachable nonterminals from the given nonterminal
//	 * 
//	 * @param head
//	 * @return
//	 */
//	public GrammarGraphBuilder removeUnusedNonterminals(Nonterminal nonterminal) {
//
//		Set<HeadGrammarSlot> referedNonterminals = new HashSet<>();
//		Deque<HeadGrammarSlot> queue = new ArrayDeque<>();
//		queue.add(nonterminalsMap.get(nonterminal));
//		
//		while(!queue.isEmpty()) {
//			HeadGrammarSlot head = queue.poll();
//			referedNonterminals.add(head);
//			
//			for(BodyGrammarSlot slot : head.getFirstSlots()) {
//				BodyGrammarSlot currentSlot = slot;
//				
//				while(currentSlot.next() != null) {
//					if(currentSlot instanceof NonterminalGrammarSlot) {
//						if(!referedNonterminals.contains(((NonterminalGrammarSlot) currentSlot).getNonterminal())) {
//							queue.add(((NonterminalGrammarSlot) currentSlot).getNonterminal());
//						}
//					}
//					currentSlot = currentSlot.next();
//				}
//			}
//		}
//
//		headGrammarSlots.retainAll(referedNonterminals);
//
//		return this;
//	}
//	
	
//	/**
//	 * The reason that we only remove unused new nonterminals, instead of
//	 * all nonterminals, is that each nonterminal can be a potential start
//	 * symbol of the grammar.
//	 * 
//	 * New nonterminals are generated during the parser generation time and
//	 * are not visible to the outside. 
//	 */
//	private void removeUnusedNewNonterminals() {
//		Set<HeadGrammarSlot> reachableNonterminals = new HashSet<>();
//		Deque<HeadGrammarSlot> queue = new ArrayDeque<>();
//
//		for(HeadGrammarSlot head : headGrammarSlots) {
//			queue.add(head);			
//		}
//		
//		while(!queue.isEmpty()) {
//			HeadGrammarSlot head = queue.poll();
//			reachableNonterminals.add(head);
//			
//			for(BodyGrammarSlot slot : head.getFirstSlots()) {
//				
//				if(slot == null) continue;
//				
//				BodyGrammarSlot currentSlot = slot;
//				
//				while(currentSlot.next() != null) {
//					if(currentSlot instanceof NonterminalGrammarSlot) {
//						HeadGrammarSlot reachableHead = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
//						if(!reachableNonterminals.contains(reachableHead)) {
//							queue.add(reachableHead);
//						}
//					}
//					currentSlot = currentSlot.next();
//				}
//			}
//		}

		// Remove new nonterminals
//		for(List<HeadGrammarSlot> list : newNonterminalsMap.values()) {
//			list.retainAll(reachableNonterminals);
//		}
//	}
	
}