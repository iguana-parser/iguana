package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
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
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Tuple;
import org.jgll.util.logging.LoggerWrapper;

import static org.jgll.grammar.Conditions.*;

public class GrammarBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarBuilder.class);

	Map<Nonterminal, HeadGrammarSlot> nonterminalsMap;

	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> headGrammarSlots;

	int maximumNumAlternates;

	int maxDescriptors;
	
	int averageDescriptors;
	
	double stDevDescriptors;

	String name;
	
	Map<Nonterminal, List<List<Symbol>>> definitions;
	
	private List<Rule> rules;
	
	Map<HeadGrammarSlot, Set<HeadGrammarSlot>> directReachabilityGraph;
	
	Map<RegularExpression, Integer> tokenIDMap;
	
	List<RegularExpression> tokens;
	
	List<Nonterminal> nonterminals;
	
	RunnableAutomaton[] dfas;
	
	Map<Nonterminal, Set<RegularExpression>> firstSets;

	Map<Nonterminal, Set<RegularExpression>> followSets;
	
	Set<Nonterminal> ll1SubGrammarNonterminals;

	private GrammarSlotFactory grammarSlotFactory;
	
	Map<String, Integer> nonterminalIds;
	
	Map<List<Symbol>, Integer> intermediateNodeIds;
	
	Map<Tuple<Nonterminal, List<Symbol>>, Integer> packedNodeIds;
	
	Map<Nonterminal, List<Set<RegularExpression>>> predictionSets;
	
	private OperatorPrecedence operatorPrecedence;
	
	/**
	 * Indexed by nonterminal index and alternate index
	 */
	Object[][] objects;
	
	/**
	 * (Nonterminal id, alternate id) => object
	 */
	Map<Tuple<Integer, Integer>, Object> objectMap;
	
	public GrammarBuilder(GrammarSlotFactory grammarSlotFactory) {
		this("no-name", grammarSlotFactory);
	}
	
	public GrammarBuilder(String name, GrammarSlotFactory grammarSlotFactory) {
		this.name = name;
		this.grammarSlotFactory = grammarSlotFactory;
		headGrammarSlots = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		
		nonterminalIds = new HashMap<>();
		intermediateNodeIds = new HashMap<>();
		packedNodeIds = new HashMap<>();
		
		definitions = new HashMap<>();
		rules = new ArrayList<>();
		objectMap = new HashMap<>();
		
		operatorPrecedence = new OperatorPrecedence();
		
		tokenIDMap = new HashMap<>();
		tokenIDMap.put(Epsilon.getInstance(), 0);
		tokenIDMap.put(EOF.getInstance(), 1);
		
		tokens = new ArrayList<>();
		tokens.add(Epsilon.getInstance());
		tokens.add(EOF.getInstance());
		
		nonterminals = new ArrayList<>();
	}

	public Grammar build() {
		
		List<Rule> newRules = operatorPrecedence.rewrite(definitions);
		addRules(newRules);
		
		objects = new Object[definitions.size()][];
		for(Nonterminal nonterminal : definitions.keySet()) {
			objects[nonterminalIds.get(nonterminal.getName())] = new Object[definitions.get(nonterminal).size()];
		}
		for(Entry<Tuple<Integer, Integer>, Object> e : objectMap.entrySet()) {
			objects[(e.getKey().getFirst())][e.getKey().getSecond()] = e.getValue();
		}
		
		long start;
		long end;
		
		start = System.nanoTime();
		firstSets = GrammarProperties.calculateFirstSets(definitions);
		followSets = GrammarProperties.calculateFollowSets(definitions, firstSets);
		predictionSets = GrammarProperties.getPredictionSets(definitions, firstSets, followSets);
		end = System.nanoTime();
		log.info("First and follow set calculation in %d ms", (end - start) / 1000_000);
		
//		start = System.nanoTime();
//		Map<Nonterminal, Set<Nonterminal>> reachabilityGraph = GrammarProperties.calculateReachabilityGraph(definitions);
		ll1SubGrammarNonterminals = new HashSet<>();
//		ll1SubGrammarNonterminals = GrammarProperties.calculateLLNonterminals(definitions, firstSets, followSets, reachabilityGraph);
//		end = System.nanoTime();
//		log.info("LL1 property is calcuated in in %d ms", (end - start) / 1000_000);
				
		
		start = System.nanoTime();
		for(Entry<Nonterminal, List<List<Symbol>>> e : definitions.entrySet()) {
			convert(e.getKey(), e.getValue());
		}
		end = System.nanoTime();
		log.info("Grammar Graph is composed in %d ms", (end - start) / 1000_000);
		
		start = System.nanoTime();
		createAutomatonsMap();
		end = System.nanoTime();
		log.info("Automatons created in %d ms", (end - start) / 1000_000);
		
		// related to rewriting the patterns
		removeUnusedNewNonterminals();
		
		validateGrammar();
		
//		GrammarProperties.setPredictionSetsForConditionals(conditionSlots);

		directReachabilityGraph = GrammarProperties.calculateDirectReachabilityGraph(headGrammarSlots, firstSets);

		
		slots = new ArrayList<>();
		
		for(HeadGrammarSlot nonterminal : headGrammarSlots) {
			for (BodyGrammarSlot slot : nonterminal.getFirstSlots()) {
				BodyGrammarSlot currentSlot = slot;
				
				while(currentSlot != null) {
					slots.add(currentSlot);
					currentSlot = currentSlot.next();
				}
			}
		}

		
		return new Grammar(this);
	}
	
	public OperatorPrecedence getOperatorPrecedence() {
		return operatorPrecedence;
	}

	public void validateGrammar() {
		GrammarVisitAction action = new GrammarVisitAction() {

			@Override
			public void visit(LastGrammarSlot slot) {
			}

			@Override
			public void visit(NonterminalGrammarSlot slot) {
//				if (slot.getNonterminal().getAlternates().size() == 0) {
//					throw new GrammarValidationException("No alternates defined for " + slot.getNonterminal());
//				}
//				
//				if(!headGrammarSlots.contains(slot.getNonterminal())) {
//					throw new GrammarValidationException("Undefined nonterminal " + slot.getNonterminal());
//				}
			}

			@Override
			public void visit(HeadGrammarSlot head) {
//				if (head.getAlternates().size() == 0) {
//					throw new GrammarValidationException("No alternates defined for " + head);
//				}
			}

			@Override
			public void visit(TokenGrammarSlot slot) {
				// TODO Auto-generated method stub
			}
			
		};

		for (HeadGrammarSlot head : headGrammarSlots) {
			GrammarVisitor.visit(head, action);
		}
	}
	
	public GrammarBuilder addRules(Iterable<Rule> rules) {
		for(Rule rule : rules) {
			addRule(rule);
		}
		return this;
	}
	
	int nonterminalId = 0;
	int intermediateId = 0;
	Map<Nonterminal, Set<List<Symbol>>> addedDefinitions = new HashMap<>();

	public GrammarBuilder addRule(Rule rule) {
		
		if(rule.getBody() != null) {
			Set<List<Symbol>> set = addedDefinitions.get(rule.getHead());
			
			if(set != null && set.contains(rule.getBody())) {
				return this;
			} else {
				if(set == null) {
					set = new HashSet<>();
					addedDefinitions.put(rule.getHead(), set);
				}
				set.add(rule.getBody());
			}			
		}
		
		Nonterminal head = rule.getHead();
		List<List<Symbol>> definition = definitions.get(head);
		if(definition == null) {
			// The order in which alternates are added is important
			definition = new ArrayList<>();
			definitions.put(head, definition);
		}
		definition.add(rule.getBody());
		
		rules.add(rule);
		
		if(!nonterminalIds.containsKey(head.getName())) {
			nonterminalIds.put(head.getName(), nonterminalId++);
			nonterminals.add(head);
		}
		
		if(rule.getObject() != null) {
			objectMap.put(Tuple.of(nonterminalIds.get(head.getName()), definitions.get(head).size() - 1), rule.getObject());
		}

		if(rule.getBody() != null) {
			for(int i = 2; i < rule.getBody().size(); i++) {
				List<Symbol> prefix = rule.getBody().subList(0, i);
				List<Symbol> plain = OperatorPrecedence.plain(prefix);
				if(!intermediateNodeIds.containsKey(plain)) {
					intermediateNodeIds.put(plain, intermediateId++);
				}
			}
			
			packedNodeIds.put(Tuple.of(rule.getHead(), rule.getBody()), definitions.get(head).size() - 1);
		}
			
		return this;
	}
 
	private void convert(Nonterminal head, List<List<Symbol>> alternates) {
		
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
				EpsilonGrammarSlot epsilonSlot = grammarSlotFactory.createEpsilonGrammarSlot(getSlotName(head, body, 0), headGrammarSlot);
				epsilonSlot.setAlternateIndex(alternateIndex);
				headGrammarSlot.setFirstGrammarSlotForAlternate(epsilonSlot, alternateIndex);
			} 
			else {
				BodyGrammarSlot firstSlot = null;
				int symbolIndex = 0;
				for (; symbolIndex < body.size(); symbolIndex++) {
					
					currentSlot = getBodyGrammarSlot(head, body, symbolIndex, currentSlot);
	
					if (symbolIndex == 0) {
						firstSlot = currentSlot;
					}
				}
	
				ConditionTest popCondition = getPostConditions(popActions);
				LastGrammarSlot lastGrammarSlot = grammarSlotFactory.createLastGrammarSlot(body, symbolIndex, getSlotName(head, body, symbolIndex), currentSlot, headGrammarSlot, popCondition);
	
				lastGrammarSlot.setAlternateIndex(alternateIndex);
				headGrammarSlot.setFirstGrammarSlotForAlternate(firstSlot, alternateIndex);
			}
			alternateIndex++;
		}
	}
	
	/**
	 * Removes unnecssary follow restrictions
	 * @return 
	 */
	private ConditionTest getPostConditionsForRegularExpression(Set<Condition> conditions) {
		Set<Condition> set = new HashSet<>(conditions);
		
		for (Condition condition : conditions) {
			if(condition.getType() != ConditionType.NOT_MATCH) {
				set.add(condition);
			} 
//			else if (condition.getType() != ConditionType.NOT_FOLLOW) {
//				set.add(condition);
//			}
		}
		
		// Make RegularExpression completely immutable. Now this works because
		// getConditons can be modified.
		return getPostConditions(set);
	}
	
	private String getSlotName(Nonterminal head, List<Symbol> body, int index) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(head.getName()).append(" ::= ");
		
		for(int i = 0; i < body.size(); i++) {
			Symbol s = body.get(i);
			
			if(i == index) {
				sb.append(". ");
			}
			
			if(s instanceof Nonterminal) {
				sb.append(s.getName()).append(" ");
			} else {
				sb.append(s).append(" ");				
			}
		}

		if(index == body.size()) {
			sb.append(".");
		} else {
			sb.delete(sb.length() - 1, sb.length());			
		}
		
		return sb.toString();
	}
	
	private int getSlotId(List<Symbol> alt, int index) {

		if(alt.size() <= 2 || index <= 1) {
			return -1;
		}

		// Last grammar slot
		if(index == alt.size()) {
			return -1;
		}

		return intermediateNodeIds.get(OperatorPrecedence.plain(alt.subList(0, index)));
	}
	
	Set<Condition> popActions = new HashSet<>();
	
	private BodyGrammarSlot getBodyGrammarSlot(Nonterminal head, List<Symbol> body, int symbolIndex, BodyGrammarSlot currentSlot) {
		Symbol symbol = body.get(symbolIndex);
		
		if(symbol instanceof RegularExpression) {
			RegularExpression token = (RegularExpression) symbol;
			
			ConditionTest preConditionsTest = getPreConditions(symbol.getConditions());
			ConditionTest postConditionsTest = getPostConditionsForRegularExpression(symbol.getConditions());
			ConditionTest popConditionsTest = getPostConditions(popActions);
			
			return grammarSlotFactory.createTokenGrammarSlot(body, symbolIndex, getSlotId(body, symbolIndex), 
					getSlotName(head, body, symbolIndex), currentSlot, getTokenID(token), preConditionsTest, postConditionsTest, popConditionsTest);
		}
		
		// Nonterminal
		else {
			ConditionTest preConditionsTest = getPreConditions(symbol.getConditions());
			ConditionTest popConditionsTest = getPostConditions(popActions);
			
			popActions = symbol.getConditions();
			
			HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return grammarSlotFactory.createNonterminalGrammarSlot(body, symbolIndex, getSlotId(body, symbolIndex), getSlotName(head, body, symbolIndex), currentSlot, nonterminal, preConditionsTest, popConditionsTest);						
		}		
	}

	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal);

		if (headGrammarSlot == null) {
			headGrammarSlot = grammarSlotFactory.createHeadGrammarSlot(nonterminal, nonterminalIds.get(nonterminal.getName()), definitions.get(nonterminal), firstSets, followSets, predictionSets);
			nonterminalsMap.put(nonterminal, headGrammarSlot);
			headGrammarSlots.add(headGrammarSlot);
		}

		return headGrammarSlot;
	}
	
	private void createAutomatonsMap() {
		dfas = new RunnableAutomaton[tokens.size()];
		
		System.out.println(tokens);
		
		for(RegularExpression regex : tokens) {
			
			Integer id = tokenIDMap.get(regex);
			
//			if(regex instanceof CharacterClass) {
//				if(regex.getConditions().isEmpty()) {
//					CharacterClass charClass = (CharacterClass) regex;
//					if(charClass.size() == 1) {
//						Range range = charClass.get(0);
//						
//						if(range.getStart() == range.getEnd()) {
//							Matcher matcher = new CharacterMatcher(range.getStart());
//							dfas[id] = matcher;
//							continue;
//						}
//					}					
//				}
//			}
			Automaton a = regex.getAutomaton();
			dfas[id] = a.getRunnableAutomaton();
			System.out.println(a);
		}
	}
	
	private int getTokenID(RegularExpression token) {
		if(tokenIDMap.containsKey(token)) {
			return tokenIDMap.get(token);
		}
		int id = tokenIDMap.size();
		tokenIDMap.put(token, id);
		tokens.add(token);
		return id;
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
		Rule.Builder builder = new Rule.Builder(new Nonterminal(keyword.getName()));
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

	public void addPrecedencePattern(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		operatorPrecedence.addPrecedencePattern(nonterminal, parent, position, child);
	}
	
	public void addExceptPattern(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		operatorPrecedence.addExceptPattern(nonterminal, parent, position, child);
	}
	
	public Set<HeadGrammarSlot> getDirectReachableNonterminals(String name) {
		return directReachabilityGraph.get(nonterminalsMap.get(new Nonterminal(name)));
	}

	/**
	 * Removes non-reachable nonterminals from the given nonterminal
	 * 
	 * @param head
	 * @return
	 */
	public GrammarBuilder removeUnusedNonterminals(Nonterminal nonterminal) {

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