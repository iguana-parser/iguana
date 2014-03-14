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
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.slotaction.FollowActions;
import org.jgll.grammar.slotaction.LineActions;
import org.jgll.grammar.slotaction.NotFollowActions;
import org.jgll.grammar.slotaction.NotMatchActions;
import org.jgll.grammar.slotaction.NotPrecedeActions;
import org.jgll.grammar.slotaction.PrecedeActions;
import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.Automaton;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Tuple;
import org.jgll.util.logging.LoggerWrapper;

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
	
	private List<BodyGrammarSlot> conditionSlots;
	
	Map<RegularExpression, Integer> tokenIDMap;
	
	List<RegularExpression> tokens;
	
	List<Nonterminal> nonterminals;
	
	Automaton[] automatons;
	
	Matcher[] dfas;
	
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
		conditionSlots = new ArrayList<>();
		
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
		
		Set<Rule> newRules = operatorPrecedence.rewrite(definitions);
		addRules(newRules);
		
		objects = new Object[definitions.size()][];
		for(Nonterminal nonterminal : definitions.keySet()) {
			objects[nonterminalIds.get(nonterminal.getName())] = new Object[definitions.get(nonterminal).size()];
		}
		for(Entry<Tuple<Integer, Integer>, Object> e : objectMap.entrySet()) {
			objects[(e.getKey().getFirst())][e.getKey().getSecond()] = e.getValue();
		}
		
		long start = System.nanoTime();
		createAutomatonsMap();
		long end = System.nanoTime();
		log.info("Automatons created in %d ms", (end - start) / 1000_000);
		
		start = System.nanoTime();
		firstSets = GrammarProperties.calculateFirstSets(definitions);
		followSets = GrammarProperties.calculateFollowSets(definitions, firstSets);
		predictionSets = GrammarProperties.getPredictionSets(definitions, firstSets, followSets);
		end = System.nanoTime();
		log.info("First and follow set calculation in %d ms", (end - start) / 1000_000);
		
		start = System.nanoTime();
		Map<Nonterminal, Set<Nonterminal>> reachabilityGraph = GrammarProperties.calculateReachabilityGraph(definitions);
		ll1SubGrammarNonterminals = new HashSet<>();
//		ll1SubGrammarNonterminals = GrammarProperties.calculateLLNonterminals(definitions, firstSets, followSets, reachabilityGraph);
		end = System.nanoTime();
		log.info("LL1 property is calcuated in in %d ms", (end - start) / 1000_000);
				
		
		for(Entry<Nonterminal, List<List<Symbol>>> e : definitions.entrySet()) {
			convert(e.getKey(), e.getValue());
		}
		
		// related to rewriting the patterns
		removeUnusedNewNonterminals();
		
		validateGrammar();
		
//		GrammarProperties.setPredictionSetsForConditionals(conditionSlots);

		directReachabilityGraph = GrammarProperties.calculateDirectReachabilityGraph(headGrammarSlots, firstSets);
		
		slots = GrammarProperties.setSlotIds(headGrammarSlots, conditionSlots);
		
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
				if (slot.getNonterminal().getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + slot.getNonterminal());
				}
				
				if(!headGrammarSlots.contains(slot.getNonterminal())) {
					throw new GrammarValidationException("Undefined nonterminal " + slot.getNonterminal());
				}
			}

			@Override
			public void visit(HeadGrammarSlot head) {
				if (head.getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + head);
				}
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

		rules.add(rule);
		
		if(rule.getObject() != null) {
			objectMap.put(Tuple.of(nonterminalIds.get(head.getName()), definitions.get(head).size() - 1), rule.getObject());
		}

		if(!nonterminalIds.containsKey(head.getName())) {
			nonterminalIds.put(head.getName(), nonterminalId++);
			nonterminals.add(head);
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
		
		// Hack for keeping the alternate index of rewritten nonterminals intact.
		
		Map<BodyGrammarSlot, Iterable<Condition>> conditions = new HashMap<>();

		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);
		
		int alternateIndex = 0;
		
		for(List<Symbol> body : alternates) {
			
			if(body == null) {
				alternateIndex++;
				continue;
			}
			
			BodyGrammarSlot currentSlot = null;
	
			if (body.size() == 0) {
				EpsilonGrammarSlot epsilonSlot = grammarSlotFactory.createEpsilonGrammarSlot(getSlotId(body, 0), getSlotName(head, body, 0), headGrammarSlot);
				epsilonSlot.setAlternateIndex(0);
				headGrammarSlot.addAlternate(new Alternate(epsilonSlot));
			} 
			else {
				int symbolIndex = 0;
				BodyGrammarSlot firstSlot = null;
				for (Symbol symbol : body) {
					
					currentSlot = getBodyGrammarSlot(head, body, symbol, symbolIndex, currentSlot, headGrammarSlot);
	
					if (symbolIndex == 0) {
						firstSlot = currentSlot;
					}
					symbolIndex++;
					
					conditions.put(currentSlot, symbol.getConditions());
				}
	
				LastGrammarSlot lastGrammarSlot = grammarSlotFactory.createLastGrammarSlot(getSlotId(body, symbolIndex), getSlotName(head, body, symbolIndex), currentSlot, headGrammarSlot);
	
				Alternate alternate = new Alternate(firstSlot);
				lastGrammarSlot.setAlternateIndex(alternateIndex);
				headGrammarSlot.addAlternate(alternate);
				
				for(Entry<BodyGrammarSlot, Iterable<Condition>> e : conditions.entrySet()) {
					for(Condition condition : e.getValue()) {
						addCondition(e.getKey(), condition);
					}
				}
			}
			alternateIndex++;
		}
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
	
	private BodyGrammarSlot getBodyGrammarSlot(Nonterminal head, List<Symbol> body, Symbol symbol, int symbolIndex, BodyGrammarSlot currentSlot, HeadGrammarSlot headGrammarSlot) {
		
		if(symbol instanceof RegularExpression) {
			RegularExpression token = (RegularExpression) symbol;
			return grammarSlotFactory.createTokenGrammarSlot(getSlotId(body, symbolIndex), getSlotName(head, body, symbolIndex), currentSlot, token, headGrammarSlot, getTokenID(token));
		}
		
		// Nonterminal
		else {
			HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return grammarSlotFactory.createNonterminalGrammarSlot(getSlotId(body, symbolIndex), getSlotName(head, body, symbolIndex), currentSlot, nonterminal, headGrammarSlot);						
		}		
	}

	private void addCondition(BodyGrammarSlot slot, final Condition condition) {

		switch (condition.getType()) {
		
			case FOLLOW:
				if (condition instanceof RegularExpressionCondition) {
					FollowActions.fromRegularExpression(slot.next(), ((RegularExpressionCondition) condition).getRegularExpression(), condition);
				} 
				else {
					FollowActions.fromGrammarSlot(slot.next(), convertCondition((ContextFreeCondition) condition), condition);
				}
				break;
				
			case NOT_FOLLOW:
				if (condition instanceof RegularExpressionCondition) {
					NotFollowActions.fromRegularExpression(slot.next(), ((RegularExpressionCondition) condition).getRegularExpression(), condition);
				} 
				else {
					NotFollowActions.fromGrammarSlot(slot.next(), convertCondition((ContextFreeCondition) condition), condition);
				}
				break;
				
			case PRECEDE:
				assert !(condition instanceof ContextFreeCondition);
				
				if(condition instanceof RegularExpressionCondition) {
					PrecedeActions.fromRegularExpression(slot, ((RegularExpressionCondition) condition).getRegularExpression(), condition);
				} 

				break;
				
			case NOT_PRECEDE:
				assert !(condition instanceof ContextFreeCondition);
				
				if(condition instanceof RegularExpressionCondition) {
					NotPrecedeActions.fromRegularExpression(slot, ((RegularExpressionCondition) condition).getRegularExpression(), condition);
				} 
				break;
				
			case MATCH:
				break;
					
			case NOT_MATCH:
				if(condition instanceof ContextFreeCondition) {
					NotMatchActions.fromGrammarSlot(slot.next(), convertCondition((ContextFreeCondition) condition), condition);
				} 
				else {
					NotMatchActions.fromRegularExpression(slot.next(), ((RegularExpressionCondition) condition).getRegularExpression(), condition);
				}
				break;
				
			case START_OF_LINE:
			  LineActions.addStartOfLine(slot, condition);
			  break;
			  
			case END_OF_LINE:
			  LineActions.addEndOfLine(slot.next(), condition);
			  break;
		}
	}
		
	// TODO: context-free conditions should have a head, to calculate first 
	// and follow sets.
	private BodyGrammarSlot convertCondition(ContextFreeCondition condition) {
		
		if(condition == null) {
			return null;
		}
		
		if(condition.getSymbols().size() == 0) {
			throw new IllegalArgumentException("The list of symbols cannot be empty.");
		}
		
		BodyGrammarSlot currentSlot = null;
		BodyGrammarSlot firstSlot = null;

		int index = 0;
		Rule rule = new Rule(new Nonterminal("Dummy"), condition.getSymbols());
		for(Symbol symbol : condition.getSymbols()) {
			if(symbol instanceof Nonterminal) {
				HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
				currentSlot = grammarSlotFactory.createNonterminalGrammarSlot(getSlotId(rule.getBody(), index), getSlotName(rule.getHead(), rule.getBody(), index), currentSlot, nonterminal, null);
			} 
			else if(symbol instanceof RegularExpression) {
				RegularExpression token = (RegularExpression) symbol;
				currentSlot = grammarSlotFactory.createTokenGrammarSlot(getSlotId(rule.getBody(), index), getSlotName(rule.getHead(), rule.getBody(), index), currentSlot, token, null, getTokenID(token));
			}
			
			if(index == 0) {
				firstSlot = currentSlot;
			}
			index++;
		}
		
		grammarSlotFactory.createLastGrammarSlot(getSlotId(rule.getBody(), index), getSlotName(rule.getHead(), rule.getBody(), index), currentSlot, null);
		conditionSlots.add(firstSlot);
		return firstSlot;
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
		dfas = new Matcher[tokens.size()];
		automatons = new Automaton[tokens.size()];
		
		for(RegularExpression regex : tokens) {
			
			Integer id = tokenIDMap.get(regex);
			Automaton a = regex.toAutomaton().minimize();
			
			automatons[id] = a;

			Matcher dfa = a.getMatcher();
			dfa.setId(id);
			dfas[id] = dfa;
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
			
			for(Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
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
			
			for(Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
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