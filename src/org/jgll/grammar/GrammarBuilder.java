package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.grammar.condition.CharacterClassCondition;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.condition.KeywordCondition;
import org.jgll.grammar.grammaraction.LongestTerminalChainAction;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DummySlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.FirstKeywordGrammarSlot;
import org.jgll.grammar.slot.FirstNonterminalGrammarSlot;
import org.jgll.grammar.slot.FirstTerminalGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.parser.GSSEdge;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.logging.LoggerWrapper;

public class GrammarBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarBuilder.class);

	Map<String, HeadGrammarSlot> nonterminalsMap;

	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> nonterminals;

	int longestTerminalChain;

	int maximumNumAlternates;

	int maxDescriptors;
	
	int averageDescriptors;
	
	double stDevDescriptors;

	String name;

	// Fields related to filtering
	List<HeadGrammarSlot> newNonterminals;

	private Map<Set<Alternate>, HeadGrammarSlot> existingAlternates;

	private Map<String, Set<Filter>> filtersMap;

	private Set<Filter> oneLevelOnlyFilters;
	
	private Map<Rule, LastGrammarSlot> ruleToLastSlotMap;

	Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph;
	
	private List<BodyGrammarSlot> conditionSlots;
	
	private final DummySlot dummySlot = new DummySlot();

	public GrammarBuilder() {
		this("no-name");
	}
	
	public GrammarBuilder(String name) {
		this.name = name;
		nonterminals = new ArrayList<>();
		slots = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		filtersMap = new HashMap<>();
		existingAlternates = new HashMap<>();
		newNonterminals = new ArrayList<>();
		
		oneLevelOnlyFilters = new HashSet<>();
		ruleToLastSlotMap = new HashMap<>();
		conditionSlots = new ArrayList<>();
	}

	public Grammar build() {
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
				if (slot.getNonterminal() == null) {
					throw new GrammarValidationException("No nonterminal defined for " + slot.getLabel());
				}
				if (slot.getNonterminal().getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + slot.getNonterminal().getLabel());
				}
			}

			@Override
			public void visit(HeadGrammarSlot head) {
				if (head.getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + head.getLabel());
				}
			}

			@Override
			public void visit(KeywordGrammarSlot slot) {
				
			}
		};

		for (HeadGrammarSlot head : nonterminals) {
			GrammarVisitor.visit(head, action);
		}
	}

	public GrammarBuilder addRule(Rule rule) {

		if (rule == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}

		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();

		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);

		BodyGrammarSlot currentSlot = null;

		if (body.size() == 0) {
			currentSlot = new EpsilonGrammarSlot(grammarSlotToString(head, body, 0), 0, headGrammarSlot, rule.getObject());
			headGrammarSlot.addAlternate(new Alternate(currentSlot, 0));
			slots.add(currentSlot);
		}

		else {
			int symbolIndex = 0;
			BodyGrammarSlot firstSlot = null;
			for (Symbol symbol : body) {
				String label = grammarSlotToString(head, body, symbolIndex);
				if (symbol instanceof Terminal) {
					if(symbolIndex == 0) {
						currentSlot = new FirstTerminalGrammarSlot(label, (Terminal) symbol, headGrammarSlot);
					} else {
						currentSlot = new TerminalGrammarSlot(label, symbolIndex, currentSlot, (Terminal) symbol, headGrammarSlot);
					}
				}
				else if (symbol instanceof Nonterminal){
					HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
					if(symbolIndex == 0) {
						currentSlot = new FirstNonterminalGrammarSlot(label, nonterminal, headGrammarSlot);
					} else {
						currentSlot = new NonterminalGrammarSlot(label, symbolIndex, currentSlot, nonterminal, headGrammarSlot);
					}
				} 
				else {
					Keyword keyword = (Keyword) symbol;
					HeadGrammarSlot keywordHead = getKeywordHeadGrammarSlot(keyword);
					if(symbolIndex == 0) {
						currentSlot = new FirstKeywordGrammarSlot(label, keywordHead, (Keyword) symbol, headGrammarSlot);
					} else {
						currentSlot = new KeywordGrammarSlot(label, symbolIndex, keywordHead, (Keyword) symbol, currentSlot, headGrammarSlot);
					}
				}
				slots.add(currentSlot);

				if (symbolIndex == 0) {
					firstSlot = currentSlot;
				}
				symbolIndex++;
			}

			String label = grammarSlotToString(head, body, symbolIndex);
			LastGrammarSlot lastGrammarSlot = new LastGrammarSlot(label, symbolIndex, currentSlot, headGrammarSlot, rule.getObject());

			slots.add(lastGrammarSlot);
			ruleToLastSlotMap.put(rule, lastGrammarSlot);
			Alternate alternate = new Alternate(firstSlot, headGrammarSlot.getAlternates().size());
			headGrammarSlot.addAlternate(alternate);
			
			for(Condition condition : rule.getConditions()) {
				addCondition(lastGrammarSlot, condition);
			}
		}
		
		return this;
	}
	
	private void addPrecedeRestriction(BodyGrammarSlot slot, final List<CharacterClass> characterClasses) {
		log.debug("Precede restriction added %s <<! %s", characterClasses, slot);
		slot.addPreCondition(new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				int ci = parser.getCurrentInputIndex();
				if (ci == 0) {
					return true;
				}
				
				for(CharacterClass characterClass : characterClasses) {
					if(characterClass.match(input.charAt(ci - 1))) {
						return false;
					}
				}

				return true;
			}
		});
	}
	
	private void addPrecedeRestriction2(BodyGrammarSlot slot, final List<Keyword> list) {
		log.debug("Precede restriction added %s <<! %s", list, slot);
		
		slot.addPreCondition(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				int ci = parser.getCurrentInputIndex();
				if (ci == 0) {
					return true;
				}
				
				for(Keyword keyword : list) {
					if(input.matchBackward(ci, keyword.getChars())) {
						return false;
					}
				}
				
				return true;
			}
		});

	}
	
	private void addCondition(LastGrammarSlot slot, final Condition condition) {
		switch (condition.getType()) {
			case FOLLOW:
				break;
				
			case NOT_FOLLOW:
				if(condition instanceof ContextFreeCondition) {
					addNotFollowRestriction(slot, convertCondition((ContextFreeCondition) condition));
				} else {
					
				}
				break;
				
			case PRECEDE:
				assert !(condition instanceof ContextFreeCondition);
				
				if(condition instanceof KeywordCondition) {
					KeywordCondition literalCondition = (KeywordCondition) condition;
					addPrecedeRestriction2(slot, literalCondition.getKeywords());
				} else {
					CharacterClassCondition characterClassCondition = (CharacterClassCondition) condition;
					addPrecedeRestriction(slot, characterClassCondition.getCharacterClasses());
				}
				
				break;
				
			case NOT_PRECEDE:
				break;
				
			case MATCH:
				break;
					
			case NOT_MATCH:
				if(condition instanceof ContextFreeCondition) {
					addNotMatch(slot, convertCondition((ContextFreeCondition) condition));
				} else {
					KeywordCondition simpleCondition = (KeywordCondition) condition;
					addNotMatch(slot, simpleCondition.getKeywords());
				}
				break;
		}
	}
	
	private void addNotMatch(LastGrammarSlot slot, final BodyGrammarSlot ifNot) {

		slot.addPopAction(new PopAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean execute(GSSEdge edge, int inputIndex, Input input) {
				GLLRecognizer recognizer = RecognizerFactory.contextFreeRecognizer();
				return !recognizer.recognize(input, edge.getDestination().getInputIndex(), inputIndex, ifNot);
			}
		});
	}
	
	private void addNotMatch(LastGrammarSlot slot, final List<Keyword> list) {
		
		if(list.size() == 1) {
			final Keyword s = list.get(0);
			
			slot.addPopAction(new PopAction() {

				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean execute(GSSEdge edge, int inputIndex, Input input) {
					return input.match(edge.getDestination().getInputIndex(), inputIndex, s.getChars());
				}
			});			
		} 
		
		else if(list.size() == 2) {
			final Keyword s1 = list.get(0);
			final Keyword s2 = list.get(1);
			
			slot.addPopAction(new PopAction() {

				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean execute(GSSEdge edge, int inputIndex, Input input) {
					return input.match(edge.getDestination().getInputIndex(), inputIndex, s1.getChars()) ||
						   input.match(edge.getDestination().getInputIndex(), inputIndex, s2.getChars())	;
				}
			});			
		} 
		
		else {
			final CuckooHashSet<Keyword> set = new CuckooHashSet<>(Keyword.externalHasher);
			for(Keyword s : list) {
				set.add(s);
			}
			
			slot.addPopAction(new PopAction() {

				private static final long serialVersionUID = 1L;
				
				@Override
				public boolean execute(GSSEdge edge, int inputIndex, Input input) {
					Keyword subInput = new Keyword(input.subInput(edge.getDestination().getInputIndex(), inputIndex));
					return !set.contains(subInput);
				}
			});			
		}
		
	}

	private void addNotFollowRestriction(LastGrammarSlot slot, final BodyGrammarSlot firstSlot) {
		
		slot.addPopAction(new PopAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean execute(GSSEdge edge, int inputIndex, Input input) {
				GLLRecognizer recognizer = RecognizerFactory.prefixContextFreeRecognizer();
				return !recognizer.recognize(input, inputIndex, input.size(), firstSlot);
			}
		});
	}
	
	private void addNotFollowRestriction(LastGrammarSlot slot, final List<int[]> list) {
		
		slot.addPopAction(new PopAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean execute(GSSEdge edge, int inputIndex, Input input) {
				for(int[] s : list) {
					if(input.match(inputIndex, s)) {
						return false;
					}
				}
				 
				return true;
			}
		});
	}
	
	private void addNotFollowRestriction2(LastGrammarSlot slot, List<CharacterClass> list) {
		
		BitSet testSet = new BitSet();
		
		for(CharacterClass c : list) {
			testSet.or(c.asBitSet());
		}
		
		final BitSet set = testSet;
		
		slot.addPopAction(new PopAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean execute(GSSEdge edge, int inputIndex, Input input) {
				return !set.get(input.charAt(inputIndex));
			}
		});
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
				currentSlot = new NonterminalGrammarSlot(grammarSlotToString(null, condition.getSymbols(), index), index, currentSlot, nonterminal, null);
			} 
			else if(symbol instanceof Terminal) {
				currentSlot = new TerminalGrammarSlot(grammarSlotToString(null, condition.getSymbols(), index), index, currentSlot, (Terminal) symbol, null);
			}
			
			if(index == 0) {
				firstSlot = currentSlot;
			}
			index++;
		}
		
		new LastGrammarSlot(grammarSlotToString(null, condition.getSymbols(), index), index, currentSlot, null, null);
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
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal.getName());

		if (headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminal);
			nonterminalsMap.put(nonterminal.getName(), headGrammarSlot);
			nonterminals.add(headGrammarSlot);
		}

		return headGrammarSlot;
	}
	
	private HeadGrammarSlot getKeywordHeadGrammarSlot(Keyword keyword) {
		Nonterminal nonterminal = new Nonterminal(keyword.getName());
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal.getName());

		if (headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminal);
			headGrammarSlot.addAlternate(new Alternate(dummySlot, 0));
			nonterminalsMap.put(nonterminal.getName(), headGrammarSlot);
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
		setIds();
		calculateReachabilityGraph();
		calculateExpectedDescriptors();
	}

	private static String grammarSlotToString(Nonterminal head, List<? extends Symbol> body, int index) {
		StringBuilder sb = new StringBuilder();

		sb.append(head == null ? "" : head.getName()).append(" ::= ");

		for (int i = 0; i < body.size(); i++) {
			if (i == index) {
				sb.append(". ");
			}
			sb.append(body.get(i)).append(" ");
		}

		sb.delete(sb.length() - 1, sb.length());

		if (index == body.size()) {
			sb.append(" .");
		}

		return sb.toString();
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
			
			Set<HeadGrammarSlot> indirectReachableNonterminals = new HashSet<>(head.getReachableNonterminals());
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
			if(alt.getBodyGrammarSlotAt(0) instanceof NonterminalGrammarSlot) {
				set.add(((NonterminalGrammarSlot)alt.getBodyGrammarSlotAt(0)).getNonterminal());
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

		else if (currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			
			changed = set.addAll(nonterminalGrammarSlot.getNonterminal().getFirstSet()) || changed;
			if (nonterminalGrammarSlot.getNonterminal().isNullable()) {
				return addFirstSet(set, currentSlot.next(), changed) || changed;
			}
			return changed;
		}

		else if(currentSlot instanceof KeywordGrammarSlot) {
			return set.add(((KeywordGrammarSlot) currentSlot).getFirstTerminal()) || changed;
		}
		
		// ignore LastGrammarSlot
		else {
			return changed;
		}
	}

	private boolean isChainNullable(BodyGrammarSlot slot) {
		if (!(slot instanceof LastGrammarSlot)) {
			if (slot instanceof TerminalGrammarSlot || slot instanceof KeywordGrammarSlot) {
				return false;
			} 

			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return ntGrammarSlot.getNonterminal().isNullable() && isChainNullable(ntGrammarSlot.next());
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

	private void setIds() {
		int i = 0;
		for (HeadGrammarSlot head : nonterminals) {
			head.setId(i++);
		}
		i = 0;
		for (BodyGrammarSlot slot : slots) {
			slot.setId(i++);
		}
	}

	public void filter() {
		for (Entry<String, Set<Filter>> entry : filtersMap.entrySet()) {
			log.debug("Filtering {} with {} filters.", entry.getKey(), entry.getValue().size());

			filterFirstLevel(nonterminalsMap.get(entry.getKey()), entry.getValue());
			filterDeep(nonterminalsMap.get(entry.getKey()), entry.getValue());
		}

		for (Filter filter : oneLevelOnlyFilters) {
			onlyFirstLevelFilter(nonterminalsMap.get(filter.getNonterminal()), oneLevelOnlyFilters);
		}

		nonterminals.addAll(newNonterminals);
	}

	private void onlyFirstLevelFilter(HeadGrammarSlot head, Set<Filter> filters) {
		for (Alternate alt : head.getAlternates()) {
			for (Filter filter : filters) {
				if (match(filter, alt)) {

					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filter.getChild()));

					if (newNonterminal == null || newNonterminal.getNonterminal().getName() != filteredNonterminal.getNonterminal().getName()) {
						newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
						newNonterminals.add(newNonterminal);

						List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.getAlternates());
						newNonterminal.setAlternates(copy);
						newNonterminal.remove(filter.getChild());
						existingAlternates.put(new HashSet<>(copyAlternates(newNonterminal, copy)), newNonterminal);
						onlyFirstLevelFilter(newNonterminal, filters);
					} else {
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
					}
				}
			}
		}
	}

	private Map<Set<Integer>, HeadGrammarSlot> firstLevels = new HashMap<>();

	private void filterFirstLevel(HeadGrammarSlot head, Set<Filter> filters) {
		for (Alternate alt : head.getAlternates()) {
			for (Filter filter : filters) {
				if (!filter.isDirect()) {
					continue;
				}
				if (match(filter, alt)) {

					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());

					Set<Alternate> without = filteredNonterminal.without(filter.getChild());
					Set<Integer> intSet = new HashSet<>();
					for (Alternate a : without) {
						intSet.add(a.getIndex());
					}
					HeadGrammarSlot newNonterminal = firstLevels.get(intSet);

					if (newNonterminal == null || newNonterminal.getNonterminal().getName() != filteredNonterminal.getNonterminal().getName()) {
						newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
						newNonterminals.add(newNonterminal);

						newNonterminal.setAlternates(filteredNonterminal.getAlternates());
						newNonterminal.remove(filter.getChild());
						firstLevels.put(newNonterminal.getAlternateIndices(), newNonterminal);
					} else {
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
					}

				}
			}
		}

		for (HeadGrammarSlot newNonterminal : newNonterminals) {
			List<Alternate> copy = copyAlternates(newNonterminal,
					newNonterminal.getAlternates());
			newNonterminal.setAlternates(copy);
		}
	}

	private void filterDeep(HeadGrammarSlot head, Set<Filter> filters) {
		for (Alternate alt : head.getAlternates()) {
			for (Filter filter : filters) {

				if (alt.match(filter.getParent())) {

					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());

					// Indirect filtering
					if (!filter.isDirect()) {
						List<Integer> nontemrinalIndices = new ArrayList<>();
						List<Alternate> alternates = new ArrayList<>();
						getRightEnds(filteredNonterminal, filter.getNonterminal(), nontemrinalIndices, alternates);
						for (int i = 0; i < nontemrinalIndices.size(); i++) {
							HeadGrammarSlot rightEndNonterminal = alternates.get(i).getNonterminalAt(nontemrinalIndices.get(i));
							HeadGrammarSlot newNonterminal = existingAlternates.get(rightEndNonterminal.without(filter.getChild()));

							if (newNonterminal == null) {
								rewrite(alternates.get(i), nontemrinalIndices.get(i), filter.getChild());
							} else {
								alternates.get(i).setNonterminalAt(nontemrinalIndices.get(i), newNonterminal);
							}
						}
					}

					if (filter.isLeftMost() && !filter.isChildBinary()) {
						rewriteRightEnds(filteredNonterminal, filter.getChild());
					}

					if (filter.isRightMost() && !filter.isChildBinary()) {
						rewriteLeftEnds(filteredNonterminal, filter.getChild());
					}
				}
			}
		}
	}

	private HeadGrammarSlot rewrite(Alternate alt, int position, List<Symbol> filteredAlternate) {
		
		HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(position);
		HeadGrammarSlot newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
		alt.setNonterminalAt(position, newNonterminal);
		newNonterminals.add(newNonterminal);

		List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.getAlternates());
		newNonterminal.setAlternates(copy);
		newNonterminal.remove(filteredAlternate);
		existingAlternates.put(new HashSet<>(copyAlternates(newNonterminal, copy)), newNonterminal);
		return newNonterminal;
	}

	private boolean match(Filter filter, Alternate alt) {
		if (alt.match(filter.getParent())) {

			HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());

			// If it the filtered nonterminal is an indirect one
			if (!oneLevelOnlyFilters.contains(filter) && !filter.getNonterminal().equals(filteredNonterminal.getNonterminal().getName())) {
				List<Integer> nonterminals = new ArrayList<>();
				List<Alternate> alternates = new ArrayList<>();
				getRightEnds(filteredNonterminal, filter.getNonterminal(),
						nonterminals, alternates);
				for (int i = 0; i < alternates.size(); i++) {
					if (alternates.get(i).getNonterminalAt(nonterminals.get(i)).contains(filter.getChild())) {
						return true;
					}
				}
			} else {
				if (filteredNonterminal.contains(filter.getChild())) {
					return true;
				}
			}
		}
		return false;
	}

	private void rewriteRightEnds(HeadGrammarSlot head, List<Symbol> filteredAlternate) {
		for (Alternate alternate : head.getAlternates()) {
			if (alternate.isBinary(head) || alternate.isUnaryPrefix(head)) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alternate.getLastSlot()).getNonterminal();

				if (nonterminal.contains(filteredAlternate)) {
					HeadGrammarSlot filteredNonterminal = alternate.getNonterminalAt(alternate.size() - 1);

					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternate));
					if (newNonterminal == null) {
						newNonterminal = rewrite(alternate, alternate.size() - 1, filteredAlternate);
						rewriteRightEnds(newNonterminal, filteredAlternate);
					} else {
						alternate.setNonterminalAt(alternate.size() - 1, newNonterminal);
					}
				}
			}
		}
	}

	private void rewriteLeftEnds(HeadGrammarSlot head, List<Symbol> filteredAlternate) {
		for (Alternate alternate : head.getAlternates()) {
			if (alternate.isBinary(head) || alternate.isUnaryPostfix(head)) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alternate.getFirstSlot()).getNonterminal();

				if (nonterminal.contains(filteredAlternate)) {
					HeadGrammarSlot filteredNonterminal = alternate.getNonterminalAt(0);

					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternate));
					if (newNonterminal == null) {
						newNonterminal = rewrite(alternate, 0, filteredAlternate);
						rewriteLeftEnds(newNonterminal, filteredAlternate);
					} else {
						alternate.setNonterminalAt(0, newNonterminal);
					}
				}
			}
		}
	}

	/**
	 * 
	 * Returns a list of all nonterminals with the given name which are
	 * reachable from head and are on the right-most end.
	 * 
	 * @param head
	 * @param name
	 * @param nonterminals
	 */
	private void getRightEnds(HeadGrammarSlot head, String name,
			List<Integer> nonterminals, List<Alternate> alternates) {
		for (Alternate alt : head.getAlternates()) {
			if (alt.getLastSlot() instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alt.getLastSlot()).getNonterminal();
				if (nonterminal.getNonterminal().getName().equals(name)) {
					nonterminals.add(alt.size() - 1);
					alternates.add(alt);
				} else {
					getRightEnds(nonterminal, name, nonterminals, alternates);
				}
			}
		}
	}

	private List<Alternate> copyAlternates(HeadGrammarSlot head, List<Alternate> list) {
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

		return new Alternate(copyFirstSlot, alternate.getIndex());
	}

	private BodyGrammarSlot copySlot(BodyGrammarSlot slot, BodyGrammarSlot previous, HeadGrammarSlot head) {

		BodyGrammarSlot copy;

		if (slot instanceof LastGrammarSlot) {
			copy = new LastGrammarSlot(slot.getLabel(), slot.getPosition(), previous, head, ((LastGrammarSlot) slot).getObject());
		} else if (slot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) slot;
			copy = new NonterminalGrammarSlot(slot.getLabel(), slot.getPosition(), previous, ntSlot.getNonterminal(), head);
		} else {
			copy = new TerminalGrammarSlot(slot.getLabel(), slot.getPosition(), previous, ((TerminalGrammarSlot) slot).getTerminal(), head);
		}

		copy.setPreConditions(slot.getPreConditions());
		slots.add(copy);
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
	public void addFilter(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		String name = nonterminal.getName();
		Filter filter = new Filter(name, parent.getBody(), position, child.getBody());

		if (name.equals(child.getHead().getName())) {
			if (filtersMap.containsKey(name)) {
				filtersMap.get(name).add(filter);
			} else {
				Set<Filter> set = new HashSet<>();
				set.add(filter);
				filtersMap.put(name, set);
			}
			log.debug("Filter added {} (deep)", filter);
		} else {
			oneLevelOnlyFilters.add(filter);
			log.debug("Filter added {} (one level only)", filter);
		}
	}

	private void calculateReachabilityGraph() {
		boolean changed = true;

		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : nonterminals) {

				for (Alternate alternate : head.getAlternates()) {
					changed |= calculateReachabilityGraph(head.getReachableNonterminals(), alternate.getFirstSlot(), changed);
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
			changed = set.addAll(nonterminalGrammarSlot.getNonterminal().getReachableNonterminals()) || changed;
			if (nonterminalGrammarSlot.getNonterminal().isNullable()) {
				return calculateReachabilityGraph(set, currentSlot.next(), changed) || changed;
			}
			return changed;
		}

		// ignore LastGrammarSlot
		else {
			return changed;
		}	
	}
}