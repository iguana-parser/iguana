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
import org.jgll.grammar.condition.KeywordCondition;
import org.jgll.grammar.condition.TerminalCondition;
import org.jgll.grammar.grammaraction.LongestTerminalChainAction;
import org.jgll.grammar.patterns.AbstractPattern;
import org.jgll.grammar.patterns.ExceptPattern;
import org.jgll.grammar.patterns.PrecedencePattern;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DirectNullableNonterminalGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.RegularListGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slotaction.LineActions;
import org.jgll.grammar.slotaction.NotFollowActions;
import org.jgll.grammar.slotaction.NotMatchActions;
import org.jgll.grammar.slotaction.NotPrecedeActions;
import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.grammar.symbols.Alternate;
import org.jgll.grammar.symbols.Character;
import org.jgll.grammar.symbols.EOF;
import org.jgll.grammar.symbols.Epsilon;
import org.jgll.grammar.symbols.Keyword;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.RegularList;
import org.jgll.grammar.symbols.Rule;
import org.jgll.grammar.symbols.Symbol;
import org.jgll.grammar.symbols.Terminal;
import org.jgll.util.logging.LoggerWrapper;
import org.jgll.util.trie.Edge;
import org.jgll.util.trie.Node;
import org.jgll.util.trie.ExternalEqual;
import org.jgll.util.trie.Trie;

public class GrammarBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarBuilder.class);

	Map<Nonterminal, HeadGrammarSlot> nonterminalsMap;

	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> nonterminals;

	int longestTerminalChain;

	int maximumNumAlternates;

	int maxDescriptors;
	
	int averageDescriptors;
	
	double stDevDescriptors;

	String name;

	// Fields related to filtering
	Map<Nonterminal, List<HeadGrammarSlot>> newNonterminalsMap;

	private Map<Set<Alternate>, HeadGrammarSlot> existingAlternates;
	
	private Map<Nonterminal, List<PrecedencePattern>> precednecePatternsMap;

	private List<ExceptPattern> exceptPatterns;
	
	private Map<Rule, LastGrammarSlot> ruleToLastSlotMap;

	private Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph;
	
	private List<BodyGrammarSlot> conditionSlots;
	
	public GrammarBuilder() {
		this("no-name");
	}
	
	public GrammarBuilder(String name) {
		this.name = name;
		nonterminals = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		precednecePatternsMap = new HashMap<>();
		existingAlternates = new HashMap<>();
		exceptPatterns = new ArrayList<>();
		ruleToLastSlotMap = new HashMap<>();
		conditionSlots = new ArrayList<>();
		reachabilityGraph = new HashMap<>();
		newNonterminalsMap = new LinkedHashMap<>();
	}

	public Grammar build() {
		
		// related to rewriting the patterns
		removeUnusedNewNonterminals();
		
		for(List<HeadGrammarSlot> newNonterminals : newNonterminalsMap.values()) {
			nonterminals.addAll(newNonterminals);			
		}
		
		nonterminals.addAll(collapsibleNonterminals);
		
		initializeGrammarProrperties();
		validate();
		return new Grammar(this);
	}

	public void validate() {
		GrammarVisitAction action = new GrammarVisitAction() {

			@Override
			public void visit(LastGrammarSlot slot) {
			}

			@Override
			public void visit(TerminalGrammarSlot slot) {
			}

			@Override
			public void visit(NonterminalGrammarSlot slot) {
				if (slot.getNonterminal().getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + slot.getNonterminal());
				}
			}

			@Override
			public void visit(HeadGrammarSlot head) {
				if (head.getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + head);
				}
			}
			
			@Override
			public void visit(KeywordGrammarSlot slot) {
			}

			@Override
			public void visit(RegularListGrammarSlot slot) {
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

		if (rule == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		Map<BodyGrammarSlot, Iterable<Condition>> conditions = new HashMap<>();

		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();

		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);

		BodyGrammarSlot currentSlot = null;

		if (body.size() == 0) {
			currentSlot = new EpsilonGrammarSlot(0, headGrammarSlot, rule.getObject());
			headGrammarSlot.addAlternate(new Alternate(currentSlot));
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
			headGrammarSlot.addAlternate(alternate);

			for(Entry<BodyGrammarSlot, Iterable<Condition>> e : conditions.entrySet()) {
				for(Condition condition : e.getValue()) {
					addCondition(e.getKey(), condition);
				}
			}
		}
		
		return this;
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Symbol symbol, int symbolIndex, BodyGrammarSlot currentSlot, HeadGrammarSlot headGrammarSlot) {
		
		if(symbol instanceof Keyword) {
			Keyword keyword = (Keyword) symbol;
			HeadGrammarSlot keywordHead = getHeadGrammarSlot(new Nonterminal(keyword.getName()));
			return new KeywordGrammarSlot(symbolIndex, keywordHead, (Keyword) symbol, currentSlot, headGrammarSlot);
		}
		
		else if (symbol instanceof Terminal) {
			return new TerminalGrammarSlot(symbolIndex, currentSlot, (Terminal) symbol, headGrammarSlot);
		}
		
		else if (symbol instanceof RegularList) {
			RegularList regularList = (RegularList) symbol;
			HeadGrammarSlot regularHead = getHeadGrammarSlot(new Nonterminal(regularList.getName()));
			return new RegularListGrammarSlot(symbolIndex, currentSlot, regularList, regularHead, headGrammarSlot);
		}
		
		// Nonterminal
		else {
			HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
			return new NonterminalGrammarSlot(symbolIndex, currentSlot, nonterminal, headGrammarSlot);						
		} 
	}

	private void addCondition(BodyGrammarSlot slot, final Condition condition) {

		switch (condition.getType()) {
		
			case FOLLOW:
				break;
				
			case NOT_FOLLOW:
				if (condition instanceof TerminalCondition) {
					NotFollowActions.fromTerminalList(slot.next(), ((TerminalCondition) condition).getTerminals(), condition);
				} else if (condition instanceof KeywordCondition) {
					NotFollowActions.fromKeywordList(slot.next(), ((KeywordCondition) condition).getKeywords(), condition);
				} else {
					NotFollowActions.fromGrammarSlot(slot.next(), convertCondition((ContextFreeCondition) condition), condition);
				}
				break;
				
			case PRECEDE:
				break;
				
			case NOT_PRECEDE:
				assert !(condition instanceof ContextFreeCondition);
				
				if(condition instanceof KeywordCondition) {
					KeywordCondition literalCondition = (KeywordCondition) condition;
					NotPrecedeActions.fromKeywordList(slot, literalCondition.getKeywords(), condition);
				} else {
					TerminalCondition terminalCondition = (TerminalCondition) condition;
					NotPrecedeActions.fromTerminalList(slot, terminalCondition.getTerminals(), condition);
				}
				break;
				
			case MATCH:
				break;
					
			case NOT_MATCH:
				if(condition instanceof ContextFreeCondition) {
					NotMatchActions.fromGrammarSlot(slot.next(), convertCondition((ContextFreeCondition) condition), condition);
				} else {
					KeywordCondition simpleCondition = (KeywordCondition) condition;
					NotMatchActions.fromKeywordList(slot.next(), simpleCondition.getKeywords(), condition);
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
				currentSlot = new NonterminalGrammarSlot(index, currentSlot, nonterminal, null);
			} 
			else if(symbol instanceof Terminal) {
				currentSlot = new TerminalGrammarSlot(index, currentSlot, (Terminal) symbol, null);
			}
			else if(symbol instanceof Keyword) {
				currentSlot = new KeywordGrammarSlot(index, getHeadGrammarSlot(new Nonterminal(symbol.getName())), (Keyword) symbol, currentSlot, null);
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
	
	private void setTestSets(List<BodyGrammarSlot> slots) {

		for(BodyGrammarSlot slot : slots) {
			BodyGrammarSlot currentSlot = slot;
			
			while (!(currentSlot instanceof LastGrammarSlot)) {
				if (currentSlot instanceof NonterminalGrammarSlot) {
					((NonterminalGrammarSlot) currentSlot).setTestSet();
				}
				currentSlot = currentSlot.next();
			}			
		}
	}

	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal);

		if (headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminal);
			nonterminalsMap.put(nonterminal, headGrammarSlot);
			nonterminals.add(headGrammarSlot);
		}

		return headGrammarSlot;
	}
	
	private void initializeGrammarProrperties() {
		calculateLongestTerminalChain();
		calculateMaximumNumAlternates();
		calculateFirstSets();
		calculateFollowSets();
		setTestSets();
		setTestSets(conditionSlots);
		setSlotIds();
		setDirectNullables();
		
		calculateReachabilityGraph();
		calculateExpectedDescriptors();
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
		for(int i : keyword.getChars()) {
			builder.addSymbol(new Character(i));
		}
		return builder.build();
	}

	/**
	 * Calculates the length of the longest chain of terminals in a body of production rules.
	 */
	private void calculateLongestTerminalChain() {
		LongestTerminalChainAction action = new LongestTerminalChainAction();
		GrammarVisitor.visit(nonterminals, action);
		longestTerminalChain = action.getLongestTerminalChain();
	}

	private void calculateMaximumNumAlternates() {
		int max = 0;
		for (HeadGrammarSlot head : nonterminals) {
			if (head.getCountAlternates() > max) {
				max = head.getCountAlternates();
			}
		}
		this.maximumNumAlternates = max;
	}

	/**
	 * 
	 * Calculates an under approximation of the maximum number of descriptors
	 * that can be created by a nonterminal head. To calculate the actual value
	 * we need to know how many reachable nonterminals are there. Now, because
	 * we use a hash set, only one of them is counted.
	 * This under approximation won't matter as we use the maximum value for
	 * all created hash maps and in some case a lower value may be closer
	 * to the reality for nonterminals having fewer alternates.
	 * 
	 * Note:
	 * To have an exact value for hash sets, we need to run an LR-like table along to 
	 * keep the aggregate of all the states, otherwise it is very expensive to
	 * dynamically calculate the exact number of expected descriptors at each point.
	 * 
	 */
	private void calculateExpectedDescriptors() {
		
		List<Integer> expectedDescriptors = new ArrayList<>();
		
		for (HeadGrammarSlot head : nonterminals) {
			
			int num = head.getCountAlternates();
			Set<HeadGrammarSlot> directReachableNonterminals = getDirectReachableNonterminals(head);
			for(HeadGrammarSlot nt : directReachableNonterminals) {
				num += nt.getCountAlternates();
			}
			
			Set<HeadGrammarSlot> indirectReachableNonterminals = new HashSet<>(reachabilityGraph.get(head));
			indirectReachableNonterminals.remove(directReachableNonterminals);
			
			for(HeadGrammarSlot nt : indirectReachableNonterminals) {
				num+= nt.getCountAlternates();
			}
			expectedDescriptors.add(num);
		}
		
		averageDescriptors = 0;
		maxDescriptors = 0;
		for(int i : expectedDescriptors) {
			averageDescriptors += i;
			if(i > maxDescriptors) {
				maxDescriptors = i;
			}
		}
		
		averageDescriptors /= expectedDescriptors.size();
		
		stDevDescriptors = 0;
		for(int i : expectedDescriptors) {
			stDevDescriptors += Math.sqrt(Math.abs(i - averageDescriptors));
		}
		
		stDevDescriptors /= expectedDescriptors.size();
	}
	
	private Set<HeadGrammarSlot> getDirectReachableNonterminals(HeadGrammarSlot head) {
		Set<HeadGrammarSlot> set = new HashSet<>();
		for(Alternate alt : head.getAlternates()) {
			if(alt.getSlotAt(0) instanceof NonterminalGrammarSlot) {
				set.add(((NonterminalGrammarSlot)alt.getSlotAt(0)).getNonterminal());
			}
		}
		return set;
	}

	private void calculateFirstSets() {
		boolean changed = true;

		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : nonterminals) {

				for (Alternate alternate : head.getAlternates()) {
					changed |= addFirstSet(head.getFirstSet(), alternate.getFirstSlot(), changed);
				}
			}
		}
	}

	/**
	 * Adds the first set of the current slot to the given set.
	 * 
	 * @param set
	 * @param currentSlot
	 * @param changed
	 * 
	 * @return true if adding any new terminals are added to the first set.
	 */
	private boolean addFirstSet(Set<Terminal> set, BodyGrammarSlot currentSlot, boolean changed) {

		if (currentSlot instanceof EpsilonGrammarSlot) {
			return set.add(Epsilon.getInstance()) || changed;
		}

		else if (currentSlot instanceof TerminalGrammarSlot) {
			return set.add(((TerminalGrammarSlot) currentSlot).getTerminal()) || changed;
		}
		
		else if (currentSlot instanceof KeywordGrammarSlot) {
			return set.add(((KeywordGrammarSlot) currentSlot).getKeyword().getFirstTerminal()) || changed;
		}

		else if (currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			
			changed = set.addAll(nonterminalGrammarSlot.getNonterminal().getFirstSet()) || changed;
			if (isNullable(nonterminalGrammarSlot.getNonterminal())) {
				return addFirstSet(set, currentSlot.next(), changed) || changed;
			}
			return changed;
		}
		
		else if(currentSlot instanceof RegularListGrammarSlot) {
			return set.add(((RegularListGrammarSlot) currentSlot).getRegularList().getCharacterClass()) || changed;
		}

		// ignore LastGrammarSlot
		else {
			return changed;
		}
	}
	
	private boolean isNullable(HeadGrammarSlot nt) {
		return nt.getFirstSet().contains(Epsilon.getInstance());
	}

	private boolean isChainNullable(BodyGrammarSlot slot) {
		if (!(slot instanceof LastGrammarSlot)) {
			if (slot instanceof TerminalGrammarSlot || slot instanceof KeywordGrammarSlot) {
				return false;
			} 

			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return isNullable(ntGrammarSlot.getNonterminal()) && isChainNullable(ntGrammarSlot.next());
		}

		return true;
	}

	private void calculateFollowSets() {
		boolean changed = true;

		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : nonterminals) {

				for (Alternate alternate : head.getAlternates()) {
					BodyGrammarSlot currentSlot = alternate.getFirstSlot();

					while (!(currentSlot instanceof LastGrammarSlot)) {

						if (currentSlot instanceof NonterminalGrammarSlot) {

							NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
							BodyGrammarSlot next = currentSlot.next();

							// For rules of the form X ::= alpha B, add the
							// follow set of X to the
							// follow set of B.
							if (next instanceof LastGrammarSlot) {
								changed |= nonterminalGrammarSlot.getNonterminal().getFollowSet().addAll(head.getFollowSet());
								break;
							}

							// For rules of the form X ::= alpha B beta, add the
							// first set of beta to
							// the follow set of B.
							Set<Terminal> followSet = nonterminalGrammarSlot.getNonterminal().getFollowSet();
							changed |= addFirstSet(followSet, currentSlot.next(), changed);

							// If beta is nullable, then add the follow set of X
							// to the follow set of B.
							if (isChainNullable(next)) {
								changed |= nonterminalGrammarSlot.getNonterminal().getFollowSet().addAll(head.getFollowSet());
							}
						}

						currentSlot = currentSlot.next();
					}
				}
			}
		}

		for (HeadGrammarSlot head : nonterminals) {
			// Remove the epsilon which may have been added from nullable
			// nonterminals
			head.getFollowSet().remove(Epsilon.getInstance());

			// Add the EOF to all nonterminals as each nonterminal can be used
			// as the start symbol.
			head.getFollowSet().add(EOF.getInstance());
		}
	}

	private void setTestSets() {
		for (HeadGrammarSlot head : nonterminals) {
			boolean nullable = head.getFirstSet().contains(Epsilon.getInstance());
			boolean directNullable = false;
			if(nullable) {
				for(Alternate alt : head.getAlternates()) {
					if(alt.isEmpty()) {
						directNullable = true;
						head.setEpsilonAlternate(alt);
						break;
					}
				}
			}
			head.setNullable(nullable, directNullable);
			
			for (Alternate alternate : head.getAlternates()) {

				BodyGrammarSlot currentSlot = alternate.getFirstSlot();

				while (!(currentSlot instanceof LastGrammarSlot)) {
					if (currentSlot instanceof NonterminalGrammarSlot) {
						((NonterminalGrammarSlot) currentSlot).setTestSet();
					}
					currentSlot = currentSlot.next();
				}
			}
		}
	}
	
	private void setSlotIds() {
		
		slots = new ArrayList<>();
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			for (Alternate alternate : nonterminal.getAlternates()) {
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
				while(currentSlot != null) {
					slots.add(currentSlot);
					currentSlot = currentSlot.next();
				}
			}
		}
		
		int i = 0;
		for (HeadGrammarSlot head : nonterminals) {
			head.setId(i++);
		}
		for (BodyGrammarSlot slot : slots) {
			slot.setId(i++);
		}
		for(BodyGrammarSlot slot : conditionSlots) {
			slot.setId(i++);
		}
	}
	
	private void setDirectNullables() {
		
		for (HeadGrammarSlot head : nonterminals) {
						
			for (Alternate alternate : head.getAlternates()) {

				BodyGrammarSlot currentSlot = alternate.getFirstSlot();

				while (!(currentSlot instanceof LastGrammarSlot)) {
					if (currentSlot instanceof NonterminalGrammarSlot) {
						// Replaces a nonterminal with an instance of direct nullable nonterminal.
						NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) currentSlot;
						if(ntSlot.getNonterminal().isDirectNullable()) {
							NonterminalGrammarSlot directNullableSlot = 
									new DirectNullableNonterminalGrammarSlot(ntSlot.getPosition(), ntSlot.previous(), ntSlot.getNonterminal(), ntSlot.getHead());
							ntSlot.next().setPrevious(directNullableSlot);
							directNullableSlot.setNext(ntSlot.next());
							directNullableSlot.setTestSet();
							directNullableSlot.setId(ntSlot.getId());
							slots.remove(ntSlot);
							slots.add(directNullableSlot);
							copyActions(ntSlot, directNullableSlot);
						}
					}
					currentSlot = currentSlot.next();
				}
			}
		}
	}
	
	public void rewritePatterns() {
		rewritePrecedencePatterns();
		rewriteExceptPatterns();
	}
	
	public void rewriteExceptPatterns() {
		rewriteExceptPatterns(groupPatterns(exceptPatterns));
	}

	public void rewritePrecedencePatterns() {
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
				freshNonterminal = new HeadGrammarSlot(pattern.getNonterminal());
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
			
			newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
			
			addNewNonterminal(newNonterminal);
			
			alt.setNonterminalAt(position, newNonterminal);
			
			List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.without(filteredAlternates));
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
		
		copy = new HeadGrammarSlot(head.getNonterminal());
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
		
		copy = new HeadGrammarSlot(head.getNonterminal());
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
		} else if (slot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) slot;
			copy = ntSlot.copy(previous, ntSlot.getNonterminal(), head);
		} else if(slot instanceof TerminalGrammarSlot) {
			copy = ((TerminalGrammarSlot) slot).copy(previous, head);
		// Keyword
		} else  {
			Keyword keyword = ((KeywordGrammarSlot) slot).getKeyword();
			copy = ((KeywordGrammarSlot) slot).copy(getHeadGrammarSlot(new Nonterminal(keyword.getName())), previous, head);
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
	
	public Set<HeadGrammarSlot> getReachableNonterminals(String name) {
		return reachabilityGraph.get(nonterminalsMap.get(new Nonterminal(name)));
	}

	private void calculateReachabilityGraph() {
		boolean changed = true;

		List<HeadGrammarSlot> allNonterminals = new ArrayList<>(nonterminals);
		for(List<HeadGrammarSlot> newNonterminals : newNonterminalsMap.values()) {
			allNonterminals.addAll(newNonterminals);
		}
		
		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : allNonterminals) {

				Set<HeadGrammarSlot> set = reachabilityGraph.get(head);
				if(set == null) {
					set = new HashSet<>();
				}
				reachabilityGraph.put(head, set);
				
				for (Alternate alternate : head.getAlternates()) {
					changed |= calculateReachabilityGraph(set, alternate.getFirstSlot(), changed);
				}
			}
		}
	}

	private boolean calculateReachabilityGraph(Set<HeadGrammarSlot> set, BodyGrammarSlot currentSlot, boolean changed) {
		if (currentSlot instanceof EpsilonGrammarSlot) {
			return false;
		}

		else if (currentSlot instanceof TerminalGrammarSlot) {
			return false;
		}

		else if (currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			
			changed = set.add(nonterminalGrammarSlot.getNonterminal()) || changed;
			
			Set<HeadGrammarSlot> set2 = reachabilityGraph.get(nonterminalGrammarSlot.getNonterminal());
			if(set2 == null) {
				set2 = new HashSet<>();
			}
			reachabilityGraph.put(nonterminalGrammarSlot.getNonterminal(), set2);
			
			changed = set.addAll(set2) || changed;
			
			if (isNullable(nonterminalGrammarSlot.getNonterminal())) {
				return calculateReachabilityGraph(set, currentSlot.next(), changed) || changed;
			}
			return changed;
		}

		// ignore LastGrammarSlot
		else {
			return changed;
		}	
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
	
	private void removeUnusedNewNonterminals() {
		Set<HeadGrammarSlot> reachableNonterminals = new HashSet<>();
		Deque<HeadGrammarSlot> queue = new ArrayDeque<>();

		for(Nonterminal nonterminal : newNonterminalsMap.keySet()) {
			queue.add(nonterminalsMap.get(nonterminal));
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
	
	
	public GrammarBuilder leftFactorize() {
		for(HeadGrammarSlot head : nonterminals) {
			leftFactorize(head);
		}
		
		for(List<HeadGrammarSlot> list : newNonterminalsMap.values()) {
			for(HeadGrammarSlot head : list) {
				leftFactorize(head);
			}
		}
		
		return this;
	}
	
	public void leftFactorize(String nonterminalName) {
		HeadGrammarSlot head = nonterminalsMap.get(new Nonterminal(nonterminalName));
		leftFactorize(head);
	}
	
	public void leftFactorize(HeadGrammarSlot head) {

		Trie<BodyGrammarSlot> trie = new Trie<>(new ExternalEqual<BodyGrammarSlot>() {

			@Override
			public boolean isEqual(BodyGrammarSlot s1, BodyGrammarSlot s2) {
				
				if(s1.getClass() != s2.getClass()) {
					return false;
				}
				
				if(s1 instanceof NonterminalGrammarSlot) {
					NonterminalGrammarSlot ntSlot1 = (NonterminalGrammarSlot) s1;
					NonterminalGrammarSlot ntSlot2 = (NonterminalGrammarSlot) s2;
					return ntSlot1.getNonterminal() == ntSlot2.getNonterminal() && 
						   ntSlot1.getPreConditions().equals(ntSlot2.getPreConditions()) &&
						   ntSlot1.next().getPopActions().equals(ntSlot2.next().getPopActions()); 
				}
				
				if(s1 instanceof KeywordGrammarSlot) {
					KeywordGrammarSlot keywordSlot1 = (KeywordGrammarSlot) s1;
					KeywordGrammarSlot keywordSlot2 = (KeywordGrammarSlot) s2;
					return keywordSlot1.getKeyword().equals(keywordSlot2.getKeyword()) && 
						   keywordSlot1.getPreConditions().equals(keywordSlot2.getPreConditions()) &&
						   keywordSlot1.next().getPopActions().equals(keywordSlot2.next().getPopActions()); 
				}

				if(s1 instanceof LastGrammarSlot) {
					return s1 == s2;
				}

				return s1.getSymbol().equals(s2.getSymbol());
			}
		});
		
		for(Alternate alt : head.getAlternates()) {
			trie.add(alt.getSymbols());
		}
		
		Node<BodyGrammarSlot> node = trie.getRoot();
		
		head.removeAllAlternates();
		
		for(Edge<BodyGrammarSlot> edge : node.getEdges()) {
			
			int symbolIndex = 0;
			BodyGrammarSlot firstSlot = null;
			
			BodyGrammarSlot currentSlot = getBodyGrammarSlot(edge.getLabel(), symbolIndex, null, head);
			
			if(symbolIndex == 0) {
				firstSlot = currentSlot;
			}
			
			test(currentSlot, edge.getDestination(), symbolIndex, head);
			Alternate alternate = new Alternate(firstSlot);
			head.addAlternate(alternate);
		}
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(BodyGrammarSlot slot, int symbolIndex, BodyGrammarSlot previous, HeadGrammarSlot head) {
		
		if(slot instanceof KeywordGrammarSlot) {
			Keyword keyword = ((KeywordGrammarSlot) slot).getKeyword();
			HeadGrammarSlot keywordHead = ((KeywordGrammarSlot) slot).getKeywordHead();
			KeywordGrammarSlot newSlot = new KeywordGrammarSlot(symbolIndex, keywordHead, keyword, previous, head);
			copyActions(slot, newSlot);
			return newSlot;
		}
		
		else if (slot instanceof TerminalGrammarSlot) {
			TerminalGrammarSlot newSlot = new TerminalGrammarSlot(symbolIndex, previous, ((TerminalGrammarSlot) slot).getTerminal(), head);
			copyActions(slot, newSlot);
			return newSlot;
		}

		// Nonterminal
		else if (slot instanceof NonterminalGrammarSlot){
			NonterminalGrammarSlot newSlot = new NonterminalGrammarSlot(symbolIndex, previous, ((NonterminalGrammarSlot) slot).getNonterminal(), head);
			copyActions(slot, newSlot);
			return newSlot;
		}
		
		else if(slot instanceof EpsilonGrammarSlot) {
			EpsilonGrammarSlot newSlot = new EpsilonGrammarSlot(symbolIndex, head, ((EpsilonGrammarSlot) slot).getObject());
			copyActions(slot, newSlot);
			return newSlot;
		}
		
		else if(slot instanceof LastGrammarSlot) {
			LastGrammarSlot newSlot = new LastGrammarSlot(symbolIndex, previous, head, ((LastGrammarSlot) slot).getObject());
			copyActions(slot, newSlot);
			return newSlot;
		}
		
		else if(slot instanceof RegularListGrammarSlot) {
			HeadGrammarSlot regularHead = ((RegularListGrammarSlot) slot).getRegularHead();
			RegularListGrammarSlot newSlot = new RegularListGrammarSlot(symbolIndex, previous, (RegularList) slot.getSymbol(), regularHead, head);
			copyActions(slot, newSlot);
			return newSlot;
		}
		
		else {
			throw new RuntimeException("Should not be here!");
		}
	}
	
	private void copyActions(BodyGrammarSlot original, BodyGrammarSlot copy) {
		for(SlotAction<Boolean> popAction : original.getPopActions()) {
			copy.addPopAction(popAction);
		}
		
		for(SlotAction<Boolean> preCondition : original.getPreConditions()) {
			copy.addPreCondition(preCondition);
		}
	}
	
	private int count;
	
	private List<HeadGrammarSlot> collapsibleNonterminals = new ArrayList<>();
	
	private void test(BodyGrammarSlot slot, Node<BodyGrammarSlot> node, int symbolIndex, HeadGrammarSlot headGrammarSlot) {
		
		if(node.size() == 0) {
			return;
		}
		
		if(node.size() == 1) {
			BodyGrammarSlot s = node.getEdges().get(0).getLabel();
			BodyGrammarSlot currentSlot = getBodyGrammarSlot(s, symbolIndex, slot, headGrammarSlot);
			copyActions(s, currentSlot);
			test(currentSlot, node.getEdges().get(0).getDestination(), symbolIndex + 1, headGrammarSlot);
			return;
		}
		
		Nonterminal nonterminal = new Nonterminal("C_" + ++count);
		nonterminal.setCollapsible(true);
		HeadGrammarSlot newHead = new HeadGrammarSlot(nonterminal);
		
		nonterminalsMap.put(nonterminal, newHead);
		collapsibleNonterminals.add(newHead);
		
		NonterminalGrammarSlot ntSlot = new NonterminalGrammarSlot(symbolIndex + 1, slot, newHead, headGrammarSlot);
		new LastGrammarSlot(symbolIndex + 2, ntSlot, headGrammarSlot, null);
		
		// Create the body of the collapsible node.
		symbolIndex = 0;
		BodyGrammarSlot firstSlot = null;
		
		for(Edge<BodyGrammarSlot> edge : node.getEdges()) {
			BodyGrammarSlot s = edge.getLabel();
			BodyGrammarSlot currentSlot;
			if(s instanceof LastGrammarSlot) {
				currentSlot = new EpsilonGrammarSlot(symbolIndex, newHead, ((LastGrammarSlot) s).getObject());
				copyActions(s, currentSlot);
				newHead.addAlternate(new Alternate(currentSlot));
			} else {
				currentSlot = getBodyGrammarSlot(edge.getLabel(), symbolIndex, null, newHead);
				if(symbolIndex == 0) {
					firstSlot = currentSlot;
				}
				test(currentSlot, edge.getDestination(), symbolIndex, newHead);
				
				Alternate alternate = new Alternate(firstSlot);
				newHead.addAlternate(alternate);
			}
			
			// Execute the pop actions of the last nonterminal before a collapsible 
			// nonterminal as preconditions of the first symbol in the alternates of
			// the collapsible nonterminal.
			for(SlotAction<Boolean> action : s.getPopActions()) {
				currentSlot.addPreCondition(action);
			}
		}
	}
}