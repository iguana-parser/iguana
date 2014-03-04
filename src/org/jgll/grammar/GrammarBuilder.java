package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.patterns.AbstractPattern;
import org.jgll.grammar.patterns.ExceptPattern;
import org.jgll.grammar.patterns.PrecedencePattern;
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
import org.jgll.util.logging.LoggerWrapper;

public class GrammarBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarBuilder.class);

	Map<Nonterminal, HeadGrammarSlot> nonterminalsMap;

	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> nonterminals;

	int maximumNumAlternates;

	int maxDescriptors;
	
	int averageDescriptors;
	
	double stDevDescriptors;

	String name;
	
	private Map<Nonterminal, Set<List<Symbol>>> definitions;
	
	private List<Rule> rules;
	
	// Fields related to filtering
	Map<Nonterminal, List<HeadGrammarSlot>> newNonterminalsMap;

	private Map<Set<Alternate>, HeadGrammarSlot> existingAlternates;
	
	private Map<Nonterminal, List<PrecedencePattern>> precednecePatternsMap;

	private List<ExceptPattern> exceptPatterns;
	
	private Map<Rule, LastGrammarSlot> ruleToLastSlotMap;

	Map<HeadGrammarSlot, Set<HeadGrammarSlot>> directReachabilityGraph;
	
	private List<BodyGrammarSlot> conditionSlots;
	
	Map<RegularExpression, Integer> tokenIDMap;
	
	List<RegularExpression> tokens;
	
	Automaton[] automatons;
	
	Matcher[] dfas;
	
	Map<Nonterminal, Set<RegularExpression>> firstSets;

	Map<Nonterminal, Set<RegularExpression>> followSets;
	
	Set<Nonterminal> ll1SubGrammarNonterminals;

	private GrammarSlotFactory grammarSlotFactory;

	
	public GrammarBuilder(GrammarSlotFactory grammarSlotFactory) {
		this("no-name", grammarSlotFactory);
	}
	
	public GrammarBuilder(String name, GrammarSlotFactory grammarSlotFactory) {
		this.name = name;
		this.grammarSlotFactory = grammarSlotFactory;
		nonterminals = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		precednecePatternsMap = new HashMap<>();
		existingAlternates = new HashMap<>();
		exceptPatterns = new ArrayList<>();
		ruleToLastSlotMap = new HashMap<>();
		conditionSlots = new ArrayList<>();
		newNonterminalsMap = new LinkedHashMap<>();
		
		definitions = new HashMap<>();
		rules = new ArrayList<>();
		
		tokenIDMap = new HashMap<>();
		tokenIDMap.put(Epsilon.getInstance(), 0);
		tokenIDMap.put(EOF.getInstance(), 1);
		
		tokens = new ArrayList<>();
		tokens.add(Epsilon.getInstance());
		tokens.add(EOF.getInstance());
	}

	public Grammar build() {
		
		long start = System.nanoTime();
		createAutomatonsMap();
		long end = System.nanoTime();
		log.info("Automatons created in %d ms", (end - start) / 1000_000);
		
		start = System.nanoTime();
		firstSets = GrammarProperties.calculateFirstSets(definitions);
		followSets = GrammarProperties.calculateFollowSets(definitions, firstSets);
		end = System.nanoTime();
		log.info("First and follow set calculation in %d ms", (end - start) / 1000_000);
		
		start = System.nanoTime();
		Map<Nonterminal, Set<Nonterminal>> reachabilityGraph = GrammarProperties.calculateReachabilityGraph(definitions);
		ll1SubGrammarNonterminals = GrammarProperties.calculateLLNonterminals(definitions, firstSets, followSets, reachabilityGraph);
		end = System.nanoTime();
		log.info("LL1 property is calcuated in in %d ms", (end - start) / 1000_000);
		
		for(Rule rule : rules) {
			convert(rule);
		}
		
		rewritePatterns();
		
		// related to rewriting the patterns
		removeUnusedNewNonterminals();
		
		for(List<HeadGrammarSlot> newNonterminals : newNonterminalsMap.values()) {
			nonterminals.addAll(newNonterminals);			
		}
		
		validateGrammar();
		
		GrammarProperties.setNullableHeads(nonterminals, firstSets);
		
		start = System.nanoTime();
		
		GrammarProperties.setPredictionSets(nonterminals, firstSets, followSets);
		end = System.nanoTime();
		log.info("Prediction sets are calcuated in in %d ms", (end - start) / 1000_000);
		
//		GrammarProperties.setPredictionSetsForConditionals(conditionSlots);

		directReachabilityGraph = GrammarProperties.calculateDirectReachabilityGraph(nonterminals, firstSets);
		
		slots = GrammarProperties.setSlotIds(nonterminals, conditionSlots);
		
		return new Grammar(this).init();
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
				
				if(!nonterminals.contains(slot.getNonterminal())) {
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

		for (HeadGrammarSlot head : nonterminals) {
			GrammarVisitor.visit(head, action);
		}
	}
	
	public GrammarBuilder addRules(Iterable<Rule> rules) {
		for(Rule rule : rules) {
			addRule(rule);
		}
		return this;
	}
	
	public GrammarBuilder addRule(Rule rule) {
		Nonterminal head = rule.getHead();
		Set<List<Symbol>> definition = definitions.get(head);
		if(definition == null) {
			definition = new HashSet<>();
			definitions.put(head, definition);
		}
		definition.add(rule.getBody());
		rules.add(rule);
		return this;
	}
 
	public void convert(Rule rule) {
		
		if (rule == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		Map<BodyGrammarSlot, Iterable<Condition>> conditions = new HashMap<>();

		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();

		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);

		BodyGrammarSlot currentSlot = null;

		if (body.size() == 0) {
			EpsilonGrammarSlot epsilonSlot = new EpsilonGrammarSlot(0, headGrammarSlot, rule.getObject());
			epsilonSlot.setAlternateIndex(headGrammarSlot.getCountAlternates());
			headGrammarSlot.addAlternate(new Alternate(epsilonSlot));
		} 
		else {
			int symbolIndex = 0;
			BodyGrammarSlot firstSlot = null;
			for (Symbol symbol : body) {
				
				currentSlot = getBodyGrammarSlot(symbol, symbolIndex, currentSlot, headGrammarSlot);

				if (symbolIndex == 0) {
					firstSlot = currentSlot;
				}
				symbolIndex++;
				
				conditions.put(currentSlot, symbol.getConditions());
			}

			LastGrammarSlot lastGrammarSlot = new LastGrammarSlot(symbolIndex, currentSlot, headGrammarSlot, rule.getObject());

			ruleToLastSlotMap.put(rule, lastGrammarSlot);
			Alternate alternate = new Alternate(firstSlot);
			lastGrammarSlot.setAlternateIndex(headGrammarSlot.getCountAlternates());
			headGrammarSlot.addAlternate(alternate);
			
			for(Entry<BodyGrammarSlot, Iterable<Condition>> e : conditions.entrySet()) {
				for(Condition condition : e.getValue()) {
					addCondition(e.getKey(), condition);
				}
			}
		}
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Symbol symbol, int symbolIndex, BodyGrammarSlot currentSlot, HeadGrammarSlot headGrammarSlot) {
		
		if(symbol instanceof RegularExpression) {
			RegularExpression token = (RegularExpression) symbol;
			return grammarSlotFactory.createTokenGrammarSlot(symbolIndex, currentSlot, token, headGrammarSlot, getTokenID(token));
		}
		
		// Nonterminal
		else {
			HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return grammarSlotFactory.createNonterminalGrammarSlot(symbolIndex, currentSlot, nonterminal, headGrammarSlot);						
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
		for(Symbol symbol : condition.getSymbols()) {
			if(symbol instanceof Nonterminal) {
				HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
				currentSlot = grammarSlotFactory.createNonterminalGrammarSlot(index, currentSlot, nonterminal, null);
			} 
			else if(symbol instanceof RegularExpression) {
				RegularExpression token = (RegularExpression) symbol;
				currentSlot = grammarSlotFactory.createTokenGrammarSlot(index, currentSlot, token, null, getTokenID(token));
			}
			
			if(index == 0) {
				firstSlot = currentSlot;
			}
			index++;
		}
		
		new LastGrammarSlot(index, currentSlot, null, null);
		conditionSlots.add(firstSlot);
		return firstSlot;
	}

	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal);

		if (headGrammarSlot == null) {
			headGrammarSlot = grammarSlotFactory.createHeadGrammarSlot(nonterminal, firstSets, followSets);
			nonterminalsMap.put(nonterminal, headGrammarSlot);
			nonterminals.add(headGrammarSlot);
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

	private void rewritePatterns() {
		rewritePrecedencePatterns();
		rewriteExceptPatterns();
	}
	
	private void rewriteExceptPatterns() {
		rewriteExceptPatterns(groupPatterns(exceptPatterns));
	}

	private void rewritePrecedencePatterns() {
		for (Entry<Nonterminal, List<PrecedencePattern>> entry : precednecePatternsMap.entrySet()) {
			log.debug("Applying the pattern %s with %d.", entry.getKey(), entry.getValue().size());

			HeadGrammarSlot nonterminal = nonterminalsMap.get(entry.getKey());
			Map<PrecedencePattern, Set<List<Symbol>>> groupPatterns = groupPatterns(entry.getValue());
			
			rewriteFirstLevel(nonterminal, groupPatterns);
			rewriteDeeperLevels(nonterminal, groupPatterns);
		}
	}

	private void rewriteExceptPatterns(Map<ExceptPattern, Set<List<Symbol>>> patterns) {
		for(Entry<ExceptPattern, Set<List<Symbol>>> e : patterns.entrySet()) {
			ExceptPattern pattern = e.getKey();
			
			for(Alternate alt : nonterminalsMap.get(pattern.getNonterminal()).getAlternates()) {
				if (alt.match(pattern.getParent())) {
					createNewNonterminal(alt, pattern.getPosition(), e.getValue());
				}
			}

			List<HeadGrammarSlot> list = newNonterminalsMap.get(pattern.getNonterminal());
			if(list != null) {
				for(HeadGrammarSlot head : list) {
					for(Alternate alt : head.getAlternates()) {
						if (alt.match(pattern.getParent())) {
							createNewNonterminal(alt, pattern.getPosition(), e.getValue());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Groups filters based on their parent and position.
	 * For example, two filters (E, E * .E, E + E) and
	 * (E, E * .E, E * E) will be grouped as:
	 * (E, E * .E, {E * E, E + E}) 
	 * 
	 * @param patterns
	 * @return
	 */
	private <T extends AbstractPattern> Map<T, Set<List<Symbol>>> groupPatterns(Iterable<T> patterns) {
		Map<T, Set<List<Symbol>>> group = new LinkedHashMap<>();
		
		for(T pattern : patterns) {
			Set<List<Symbol>> set = group.get(pattern);
			if(set == null) {
				set = new LinkedHashSet<>();
				group.put(pattern, set);
			}
			set.add(pattern.getChild());
		}
		
		return group;
	}

	private void rewriteFirstLevel(HeadGrammarSlot head, Map<PrecedencePattern, Set<List<Symbol>>> patterns) {
		
		// This is complicated shit! Document it for the future reference.
		Map<PrecedencePattern, HeadGrammarSlot> freshNonterminals = new LinkedHashMap<>();
		
		Map<Set<List<Symbol>>, HeadGrammarSlot> map = new HashMap<>();
		
		// Creating fresh nonterminals
		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : patterns.entrySet()) {
			
			PrecedencePattern pattern = e.getKey();
			
			HeadGrammarSlot freshNonterminal = map.get(e.getValue());
			
			if(freshNonterminal == null) {
				freshNonterminal = grammarSlotFactory.createHeadGrammarSlot(pattern.getNonterminal(), firstSets, followSets);
				addNewNonterminal(freshNonterminal);
				map.put(e.getValue(), freshNonterminal);
			}

			freshNonterminals.put(pattern, freshNonterminal);
		}
		
		// Replacing nonterminals with their fresh ones
		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : patterns.entrySet()) {
			
			for(Alternate alt : head.getAlternates()) {
				
				PrecedencePattern pattern = e.getKey();
				
				if(!alt.match(pattern.getParent())) {
					continue;
				}
				
				log.trace("Applying the pattern %s on %s.", pattern, alt);
				
				if (!pattern.isDirect()) {
					
					HeadGrammarSlot copy;
					
					List<Alternate> alternates = new ArrayList<>();
					if(pattern.isLeftMost()) {
						copy = copyIndirectAtLeft(alt.getNonterminalAt(pattern.getPosition()), pattern.getNonterminal());
						getLeftEnds(copy, pattern.getNonterminal(), alternates);
						for(Alternate a : alternates) {
							a.setNonterminalAt(0, freshNonterminals.get(pattern));
						}
					} else {
						copy = copyIndirectAtRight(alt.getNonterminalAt(pattern.getPosition()), pattern.getNonterminal());
						getRightEnds(copy, pattern.getNonterminal(), alternates);
						for(Alternate a : alternates) {
							a.setNonterminalAt(a.size() - 1, freshNonterminals.get(pattern));
						}
					}
					
					alt.setNonterminalAt(pattern.getPosition(), copy);
					
				} else {
					alt.setNonterminalAt(pattern.getPosition(), freshNonterminals.get(pattern));
				}
			}
		}
		
		// creating the body of fresh direct nonterminals
		for(Entry<PrecedencePattern, HeadGrammarSlot> e : freshNonterminals.entrySet()) {
			PrecedencePattern pattern = e.getKey();
			
			HeadGrammarSlot freshNontermianl = e.getValue();
			
			Set<Alternate> alternates = head.without(patterns.get(pattern));
			List<Alternate> copyAlternates = copyAlternates(freshNontermianl, alternates);

			for(Alternate a : copyAlternates) {
				((LastGrammarSlot) a.getLastSlot().next()).setAlternateIndex(copyAlternates.indexOf(a));
			}
			
			freshNontermianl.setAlternates(copyAlternates);
			existingAlternates.put(new HashSet<>(copyAlternates), freshNontermianl);
		}
	}
	
	private void addNewNonterminal(HeadGrammarSlot nonterminal) {
		List<HeadGrammarSlot> list = newNonterminalsMap.get(nonterminal.getNonterminal());
		if(list == null) {
			list = new ArrayList<>();
			newNonterminalsMap.put(nonterminal.getNonterminal(), list);
		}
		list.add(nonterminal);
	}
	
	private void rewriteDeeperLevels(HeadGrammarSlot head, Map<PrecedencePattern, Set<List<Symbol>>> patterns) {

		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : patterns.entrySet()) {
			
			PrecedencePattern pattern = e.getKey();
			Set<List<Symbol>> children = e.getValue();
			
			for (Alternate alt : head.getAlternates()) {
				if (pattern.isLeftMost() && alt.match(pattern.getParent())) {
					rewriteRightEnds(alt.getNonterminalAt(0), pattern, children);
				}

				if (pattern.isRightMost() && alt.match(pattern.getParent()) ){
					rewriteLeftEnds(alt.getNonterminalAt(alt.size() - 1), pattern, children);
				}
			}
		}
	}
	
	private HeadGrammarSlot createNewNonterminal(Alternate alt, int position, Set<List<Symbol>> filteredAlternates) {
		
		HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(position);
		
		HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternates));
		
		if(newNonterminal == null) {
			
			newNonterminal = grammarSlotFactory.createHeadGrammarSlot(filteredNonterminal.getNonterminal(), firstSets, followSets);
			
			addNewNonterminal(newNonterminal);
			
			alt.setNonterminalAt(position, newNonterminal);
			
			List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.without(filteredAlternates));
			
			for(Alternate a : copy) {
				((LastGrammarSlot) a.getLastSlot().next()).setAlternateIndex(copy.indexOf(a));
			}
			
			existingAlternates.put(new HashSet<>(copy), newNonterminal);
			newNonterminal.setAlternates(copy);

		} else {
			alt.setNonterminalAt(position, newNonterminal);
		}
		
		return newNonterminal;
	}

	
	/**
	 * Rewrites the right ends of the first nonterminal in the given alternate.
	 * 
	 * @param alt
	 * @param pattern
	 */
	private void rewriteRightEnds(HeadGrammarSlot nonterminal, PrecedencePattern pattern, Set<List<Symbol>> children) {
		
		// Direct filtering
		if(nonterminal.getNonterminal().equals(pattern.getNonterminal())) {
			
			for(Alternate alternate : nonterminal.getAlternates()) {
				if(!(alternate.getLastSlot() instanceof NonterminalGrammarSlot)) {
					continue;
				}

				HeadGrammarSlot last = ((NonterminalGrammarSlot) alternate.getLastSlot()).getNonterminal();
				
				if(last.contains(children)) {
					HeadGrammarSlot newNonterminal = createNewNonterminal(alternate, alternate.size() - 1, children);
					rewriteRightEnds(newNonterminal, pattern, children);
				}				
			}
		} else {
			
			assert pattern.isLeftMost();

			List<Alternate> alternates = new ArrayList<>(); 
			getLeftEnds(nonterminal, pattern.getNonterminal(), alternates);

			for(Alternate alt : alternates) {
				rewriteRightEnds(alt.getNonterminalAt(0), pattern, children);
			}
		}
	}
	
	private void rewriteLeftEnds(HeadGrammarSlot nonterminal, PrecedencePattern pattern, Set<List<Symbol>> children) {
		
		// Direct filtering
		if(nonterminal.getNonterminal().equals(pattern.getNonterminal())) {
			
			for(Alternate alternate : nonterminal.getAlternates()) {
				if(!(alternate.getFirstSlot() instanceof NonterminalGrammarSlot)) {
					continue;
				}

				HeadGrammarSlot first = ((NonterminalGrammarSlot) alternate.getFirstSlot()).getNonterminal();
				
				if(first.contains(children)) {
					HeadGrammarSlot newNonterminal = createNewNonterminal(alternate, 0, children);
					rewriteLeftEnds(newNonterminal, pattern, children);
				}				
			}
			
		} else {
			assert pattern.isRightMost();

			List<Alternate> alternates = new ArrayList<>(); 
			getRightEnds(nonterminal, pattern.getNonterminal(), alternates);

			for(Alternate alt : alternates) {
				rewriteLeftEnds(alt.getNonterminalAt(alt.size() - 1), pattern, children);
			}
		}
	}
		
	/**
	 * 
	 * Returns a list of all nonterminals with the given name which are
	 * reachable from the given head and are on the right-most end.
	 * 
	 * @param head
	 * @param directNonterminal
	 * @param alternates
	 */
	private void getRightEnds(HeadGrammarSlot head, Nonterminal directNonterminal, List<Alternate> alternates) {
		getRightEnds(head, directNonterminal, alternates, new HashSet<HeadGrammarSlot>());
	}
	
	private void getRightEnds(HeadGrammarSlot head, Nonterminal directNonterminal, List<Alternate> alternates, Set<HeadGrammarSlot> visited) {
		
		if(visited.contains(head)) {
			return;
		}
		
		for (Alternate alt : head.getAlternates()) {
			if (alt.getLastSlot() instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot last = ((NonterminalGrammarSlot) alt.getLastSlot()).getNonterminal();
				if (last.getNonterminal().equals(directNonterminal)) {
					alternates.add(alt);
				} else {
					visited.add(last);
					getRightEnds(last, directNonterminal, alternates, visited);
				}
			}
		}
	}
	
	/**
	 * 
	 * Returns a list of all nonterminals with the given name which are
	 * reachable from the given head and are on the left-most end.
	 * 
	 * @param head
	 * @param directName
	 * @param alternates
	 */
	private void getLeftEnds(HeadGrammarSlot head, Nonterminal nonterminal, List<Alternate> nonterminals) {
		getLeftEnds(head, nonterminal, nonterminals, new HashSet<HeadGrammarSlot>());
	}
	
	private void getLeftEnds(HeadGrammarSlot head, Nonterminal nonterminal, List<Alternate> nonterminals, Set<HeadGrammarSlot> visited) {
		
		if(visited.contains(head)) {
			return;
		}
		
		for (Alternate alt : head.getAlternates()) {
			if (alt.getFirstSlot() instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot first = ((NonterminalGrammarSlot) alt.getFirstSlot()).getNonterminal();
				if (first.getNonterminal().equals(nonterminal)) {
					nonterminals.add(alt);
				} else {
					visited.add(first);
					getLeftEnds(first, nonterminal, nonterminals, visited);
				}
			}
		}
	}
	
	private HeadGrammarSlot copyIndirectAtLeft(HeadGrammarSlot head, Nonterminal directNonterminal) {
		return copyIndirectAtLeft(head, directNonterminal, new HashMap<HeadGrammarSlot, HeadGrammarSlot>());
	}

	private HeadGrammarSlot copyIndirectAtRight(HeadGrammarSlot head, Nonterminal directNonterminal) {
		return copyIndirectAtRight(head, directNonterminal, new HashMap<HeadGrammarSlot, HeadGrammarSlot>());
	}
	
	private HeadGrammarSlot copyIndirectAtLeft(HeadGrammarSlot head, Nonterminal directName, HashMap<HeadGrammarSlot, HeadGrammarSlot> map) {
		
		HeadGrammarSlot copy = map.get(head);
		if(copy != null) {
			return copy;
		}
		
		copy = grammarSlotFactory.createHeadGrammarSlot(head.getNonterminal(), firstSets, followSets);
		addNewNonterminal(copy);
		map.put(head, copy);
		
		List<Alternate> copyAlternates = copyAlternates(copy, head.getAlternates());
		copy.setAlternates(copyAlternates);
		
		for(Alternate alt : copyAlternates) {
			if(alt.getSlotAt(0) instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot)alt.getSlotAt(0)).getNonterminal();
				// Leave the direct nonterminal, copy indirect ones
				if(!nonterminal.getNonterminal().equals(directName)) {
					alt.setNonterminalAt(0, copyIndirectAtLeft(nonterminal, directName, map));
				}
			}
		}
		
		return copy;
	}
	
	private HeadGrammarSlot copyIndirectAtRight(HeadGrammarSlot head, Nonterminal directNonterminal, HashMap<HeadGrammarSlot, HeadGrammarSlot> map) {
		
		HeadGrammarSlot copy = map.get(head);
		if(copy != null) {
			return copy;
		}
		
		copy = grammarSlotFactory.createHeadGrammarSlot(head.getNonterminal(), firstSets, followSets);
		addNewNonterminal(copy);
		map.put(head, copy);
		
		List<Alternate> copyAlternates = copyAlternates(copy, head.getAlternates());
		copy.setAlternates(copyAlternates);
		
		for(Alternate alt : copyAlternates) {
			if(alt.getSlotAt(alt.size() - 1) instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot)alt.getSlotAt(alt.size() - 1)).getNonterminal();
				// Leave the direct nonterminal, copy indirect ones
				if(!nonterminal.getNonterminal().equals(directNonterminal)) {
					alt.setNonterminalAt(alt.size() - 1, copyIndirectAtLeft(nonterminal, directNonterminal, map));
				}
			}
		}
		
		return copy;
	}

	
	private List<Alternate> copyAlternates(HeadGrammarSlot head, Iterable<Alternate> list) {
		List<Alternate> copyList = new ArrayList<>();
		for (Alternate alt : list) {
			copyList.add(copyAlternate(alt, head));
		}
		return copyList;
	}

	private Alternate copyAlternate(Alternate alternate, HeadGrammarSlot head) {
		BodyGrammarSlot copyFirstSlot = copySlot(alternate.getFirstSlot(), null, head);

		BodyGrammarSlot current = alternate.getFirstSlot().next();
		BodyGrammarSlot copy = copyFirstSlot;

		while (current != null) {
			copy = copySlot(current, copy, head);
			current = current.next();
		}

		return new Alternate(copyFirstSlot);
	}

	private BodyGrammarSlot copySlot(BodyGrammarSlot slot, BodyGrammarSlot previous, HeadGrammarSlot head) {

		BodyGrammarSlot copy;

		if (slot instanceof LastGrammarSlot) {
			copy = ((LastGrammarSlot) slot).copy(previous, head);
		} 
		else if (slot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) slot;
			copy = ntSlot.copy(previous, ntSlot.getNonterminal(), head);
		}
		else if(slot instanceof TokenGrammarSlot) {
			copy = ((TokenGrammarSlot) slot).copy(previous, head);
		}
		else {
			throw new IllegalStateException("Unexpected grammar slot type encountered.");
		}

		return copy;
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
	 * 
	 * Adds the given filter to the set of filters. If a filter with the same
	 * nonterminal, alternate index, and alternate index already exists, only
	 * the given filter alternates are added to the existing filter, effectively
	 * updating the filter.
	 * 
	 * @param nonterminal
	 * @param alternateIndex
	 * @param position
	 * @param filterdAlternates
	 * 
	 */
	public void addPrecedencePattern(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		PrecedencePattern pattern = new PrecedencePattern(nonterminal, parent.getBody(), position, child.getBody());

		if (precednecePatternsMap.containsKey(nonterminal)) {
			precednecePatternsMap.get(nonterminal).add(pattern);
		} else {
			List<PrecedencePattern> set = new ArrayList<>();
			set.add(pattern);
			precednecePatternsMap.put(nonterminal, set);
		}
		log.debug("Precedence pattern added %s", pattern);
	}
	
	public void addExceptPattern(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		ExceptPattern pattern = new ExceptPattern(nonterminal, parent.getBody(), position, child.getBody());
		exceptPatterns.add(pattern);
		log.debug("Except pattern added %s", pattern);
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

		nonterminals.retainAll(referedNonterminals);

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

		for(HeadGrammarSlot head : nonterminals) {
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
		
		for(List<HeadGrammarSlot> list : newNonterminalsMap.values()) {
			list.retainAll(reachableNonterminals);
		}
	}
	
}