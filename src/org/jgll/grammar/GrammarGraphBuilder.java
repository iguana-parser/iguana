package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.IntermediateNodeIds;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.OriginalIntermediateNodeIds;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.util.logging.LoggerWrapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class GrammarGraphBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarGraphBuilder.class);

	Map<Nonterminal, HeadGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;

	BiMap<BodyGrammarSlot, Integer> slots;
	
	List<HeadGrammarSlot> headGrammarSlots;

	String name;
	
	Grammar grammar;
	
	BiMap<RegularExpression, Integer> regularExpressions;
	
	BiMap<Nonterminal, Integer> nonterminals;
	
	Set<Nonterminal> ll1SubGrammarNonterminals;

	private GrammarSlotFactory grammarSlotFactory;
	
	/**
	 * Indexed by nonterminal index and alternate index
	 */
	Object[][] objects;
	
	private Conditions conditions;
	
	public GrammarGraphBuilder(Grammar grammar, GrammarSlotFactory grammarSlotFactory) {
		this("no-name", grammar, grammarSlotFactory, new OriginalIntermediateNodeIds(grammar));
	}
	
	public GrammarGraphBuilder(String name, Grammar grammar, GrammarSlotFactory grammarSlotFactory,
							   IntermediateNodeIds intermediateNodeIds) {
		this.name = name;
		this.grammarSlotFactory = grammarSlotFactory;
		this.grammar = grammar;

		headGrammarSlots = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		
		regularExpressions = HashBiMap.create();
		regularExpressions.put(Epsilon.getInstance(), 0);
		regularExpressions.put(EOF.getInstance(), 1);
		
		slots = HashBiMap.create();
		nonterminals = HashBiMap.create();
		conditions = new DefaultConditionsImpl();
	}

	public GrammarGraph build() {
		
		long start;
		long end;
		
		
//		start = System.nanoTime();
//		end = System.nanoTime();
//		log.info("First and follow set calculation in %d ms", (end - start) / 1000_000);
		
//		start = System.nanoTime();
//		Map<Nonterminal, Set<Nonterminal>> reachabilityGraph = GrammarProperties.calculateReachabilityGraph(definitions);
		ll1SubGrammarNonterminals = new HashSet<>();
//		ll1SubGrammarNonterminals = GrammarProperties.calculateLLNonterminals(definitions, firstSets, followSets, reachabilityGraph);
//		end = System.nanoTime();
//		log.info("LL1 property is calcuated in in %d ms", (end - start) / 1000_000);
				
		
		start = System.nanoTime();
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal);
		}

		end = System.nanoTime();
		log.info("Grammar Graph is composed in %d ms", (end - start) / 1000_000);
		
		start = System.nanoTime();
		end = System.nanoTime();
		log.info("Automatons created in %d ms", (end - start) / 1000_000);
		
		// related to rewriting the patterns
		removeUnusedNewNonterminals();
		
//		GrammarProperties.setPredictionSetsForConditionals(conditionSlots);
		
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
				slots.put(epsilonSlot, epsilonSlot.getId());
				headGrammarSlot.setFirstGrammarSlotForAlternate(epsilonSlot, alternateIndex);
			} 
			else {
				BodyGrammarSlot firstSlot = null;
				int symbolIndex = 0;
				for (; symbolIndex < body.size(); symbolIndex++) {
					
					currentSlot = getBodyGrammarSlot(new Rule(head, body), symbolIndex, currentSlot);
					slots.put(currentSlot, currentSlot.getId());
	
					if (symbolIndex == 0) {
						firstSlot = currentSlot;
					}
				}
	
				ConditionTest popCondition = conditions.getPostConditions(popActions);
				LastGrammarSlot lastSlot = grammarSlotFactory.createLastGrammarSlot(new Rule(head, body), symbolIndex, currentSlot, headGrammarSlot, popCondition);
				slots.put(lastSlot, lastSlot.getId());
				headGrammarSlot.setFirstGrammarSlotForAlternate(firstSlot, alternateIndex);
			}
			alternateIndex++;
		}
	}
	
	/**
	 * Removes unnecssary follow restrictions
	 * @return 
	 */
	private ConditionTest getPostConditionsForRegularExpression(Set<Condition> c) {
		Set<Condition> set = new HashSet<>(c);
		
		for (Condition condition : c) {
			if(condition.getType() != ConditionType.NOT_MATCH) {
				set.add(condition);
			} 
//			else if (condition.getType() != ConditionType.NOT_FOLLOW) {
//				set.add(condition);
//			}
		}
		
		// Make RegularExpression completely immutable. Now this works because
		// getConditons can be modified.
		return conditions.getPostConditions(set);
	}
	
	
	
	Set<Condition> popActions = new HashSet<>();
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int symbolIndex, BodyGrammarSlot currentSlot) {
		
		Symbol symbol = rule.getBody().get(symbolIndex);
		
		if(symbol instanceof RegularExpression) {
			RegularExpression regex = (RegularExpression) symbol;
			
			ConditionTest preConditionsTest = conditions.getPreConditions(symbol.getConditions());
			ConditionTest postConditionsTest = getPostConditionsForRegularExpression(symbol.getConditions());
			ConditionTest popConditionsTest = conditions.getPostConditions(popActions);
			
			return grammarSlotFactory.createTokenGrammarSlot(rule, symbolIndex, currentSlot, getTerminalGrammarSlot(regex), preConditionsTest, postConditionsTest, popConditionsTest);
		}
		
		// Nonterminal
		else {
			ConditionTest preConditionsTest = conditions.getPreConditions(symbol.getConditions());
			ConditionTest popConditionsTest = conditions.getPostConditions(popActions);
			
			popActions = new HashSet<>(symbol.getConditions());
			
			HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return grammarSlotFactory.createNonterminalGrammarSlot(rule, symbolIndex, currentSlot, nonterminal, preConditionsTest, popConditionsTest);						
		}		
	}

	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal);

		if (headGrammarSlot == null) {
			headGrammarSlot = grammarSlotFactory.createHeadGrammarSlot(nonterminal, grammar.getAlternatives(nonterminal), grammar.getFirstSets(), grammar.getFollowSets(), grammar.getPredictionSets());
			nonterminalsMap.put(nonterminal, headGrammarSlot);
			
			if (!nonterminals.containsKey(nonterminal)) {
				nonterminals.put(nonterminal, headGrammarSlot.getId());
			}
			
			headGrammarSlots.add(headGrammarSlot);
		}

		return headGrammarSlot;
	}
	
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		TerminalGrammarSlot slot = terminalsMap.get(regex);
		if (slot == null) {
			slot = new TerminalGrammarSlot(regex);
			terminalsMap.put(regex, slot);
		}
		return slot;
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

	
	@SafeVarargs
	protected static <T> Set<T> set(T... objects) {
		Set<T> set = new HashSet<>();
		for (T t : objects) {
			set.add(t);
		}
		return set;
	}
	
	/**
	 * Removes non-reachable nonterminals from the given nonterminal
	 * 
	 * @param head
	 * @return
	 */
	public GrammarGraphBuilder removeUnusedNonterminals(Nonterminal nonterminal) {

		Set<HeadGrammarSlot> referedNonterminals = new HashSet<>();
		Deque<HeadGrammarSlot> queue = new ArrayDeque<>();
		queue.add(nonterminalsMap.get(nonterminal));
		
		while(!queue.isEmpty()) {
			HeadGrammarSlot head = queue.poll();
			referedNonterminals.add(head);
			
			for(BodyGrammarSlot slot : head.getFirstSlots()) {
				BodyGrammarSlot currentSlot = slot;
				
				while(currentSlot.next() != null) {
					if(currentSlot instanceof NonterminalGrammarSlot) {
						if(!referedNonterminals.contains(((NonterminalGrammarSlot) currentSlot).getNonterminal())) {
							queue.add(((NonterminalGrammarSlot) currentSlot).getNonterminal());
						}
					}
					currentSlot = currentSlot.next();
				}
			}
		}

		headGrammarSlots.retainAll(referedNonterminals);

		return this;
	}
	
	
	/**
	 * The reason that we only remove unused new nonterminals, instead of
	 * all nonterminals, is that each nonterminal can be a potential start
	 * symbol of the grammar.
	 * 
	 * New nonterminals are generated during the parser generation time and
	 * are not visible to the outside. 
	 */
	private void removeUnusedNewNonterminals() {
		Set<HeadGrammarSlot> reachableNonterminals = new HashSet<>();
		Deque<HeadGrammarSlot> queue = new ArrayDeque<>();

		for(HeadGrammarSlot head : headGrammarSlots) {
			queue.add(head);			
		}
		
		while(!queue.isEmpty()) {
			HeadGrammarSlot head = queue.poll();
			reachableNonterminals.add(head);
			
			for(BodyGrammarSlot slot : head.getFirstSlots()) {
				
				if(slot == null) continue;
				
				BodyGrammarSlot currentSlot = slot;
				
				while(currentSlot.next() != null) {
					if(currentSlot instanceof NonterminalGrammarSlot) {
						HeadGrammarSlot reachableHead = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!reachableNonterminals.contains(reachableHead)) {
							queue.add(reachableHead);
						}
					}
					currentSlot = currentSlot.next();
				}
			}
		}

		// Remove new nonterminals
//		for(List<HeadGrammarSlot> list : newNonterminalsMap.values()) {
//			list.retainAll(reachableNonterminals);
//		}
	}
	
}