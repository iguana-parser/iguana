package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
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
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DirectNullableNonterminalGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.GLLParserInternals;
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
	Map<String, List<HeadGrammarSlot>> newNonterminalsMap;

	private Map<Set<Alternate>, HeadGrammarSlot> existingAlternates;

	private Map<String, Set<PrecedencePattern>> filtersMap;

	private Set<PrecedencePattern> oneLevelOnlyFilters;
	
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
		filtersMap = new HashMap<>();
		existingAlternates = new HashMap<>();
		
		oneLevelOnlyFilters = new HashSet<>();
		ruleToLastSlotMap = new HashMap<>();
		conditionSlots = new ArrayList<>();
		reachabilityGraph = new HashMap<>();
		newNonterminalsMap = new LinkedHashMap<>();
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
				
				if(symbol instanceof Keyword) {
					Keyword keyword = (Keyword) symbol;
					HeadGrammarSlot keywordHead = getHeadGrammarSlot(new Nonterminal(keyword.getName()));
					currentSlot = new KeywordGrammarSlot(symbolIndex, keywordHead, (Keyword) symbol, currentSlot, headGrammarSlot);
				}
				
				else if (symbol instanceof Terminal) {
					currentSlot = new TerminalGrammarSlot(symbolIndex, currentSlot, (Terminal) symbol, headGrammarSlot);
				}
				// Nonterminal
				else {
					HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
					currentSlot = new NonterminalGrammarSlot(symbolIndex, currentSlot, nonterminal, headGrammarSlot);						
				} 

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

	private void addNotPrecede(BodyGrammarSlot slot, final List<Terminal> terminals) {
		log.debug("Precede restriction added %s <<! %s", terminals, slot);
		
		BitSet testSet = new BitSet();
		
		for(Terminal t : terminals) {
			testSet.or(t.asBitSet());
		}
		
		final BitSet set = testSet;
		
		
		slot.addPreCondition(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				
				int ci = parser.getCurrentInputIndex();
				if (ci == 0) {
					return false;
				}
			
				return set.get(input.charAt(ci - 1));
			}
		});
	}
	
	private void addNotPrecede2(BodyGrammarSlot slot, final List<Keyword> list) {
		log.debug("Precede restriction added %s <<! %s", list, slot);
		
		slot.addPreCondition(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				int ci = parser.getCurrentInputIndex();
				if (ci == 0) {
					return false;
				}
				
				for(Keyword keyword : list) {
					if(input.matchBackward(ci, keyword.getChars())) {
						return true;
					}
				}
				
				return false;
			}
		});

	}
	
	private void addCondition(BodyGrammarSlot slot, final Condition condition) {

		switch (condition.getType()) {
		
			case FOLLOW:
				break;
				
			case NOT_FOLLOW:
				if (condition instanceof TerminalCondition) {
					addNotFollow1(slot.next(), ((TerminalCondition) condition).getTerminals());
				} else if (condition instanceof KeywordCondition) {
					addNotFollow2(slot.next(), ((KeywordCondition) condition).getKeywords());
				} else {
					addNotFollow(slot.next(), convertCondition((ContextFreeCondition) condition));
				}
				break;
				
			case PRECEDE:
				break;
				
			case NOT_PRECEDE:
				assert !(condition instanceof ContextFreeCondition);
				
				if(condition instanceof KeywordCondition) {
					KeywordCondition literalCondition = (KeywordCondition) condition;
					addNotPrecede2(slot, literalCondition.getKeywords());
				} else {
					TerminalCondition terminalCondition = (TerminalCondition) condition;
					addNotPrecede(slot, terminalCondition.getTerminals());
				}
				break;
				
			case MATCH:
				break;
					
			case NOT_MATCH:
				if(condition instanceof ContextFreeCondition) {
					addNotMatch(slot.next(), convertCondition((ContextFreeCondition) condition));
				} else {
					KeywordCondition simpleCondition = (KeywordCondition) condition;
					addNotMatch(slot.next(), simpleCondition.getKeywords());
				}
				break;
				
			case START_OF_LINE:
			  addStartOfLine(slot);
			  break;
			  
			case END_OF_LINE:
			  addEndOfLine(slot.next());
			  break;
		}
	}
	
	private void addEndOfLine(BodyGrammarSlot slot) {
    slot.addPopAction(new SlotAction<Boolean>() {
      private static final long serialVersionUID = 1L;
      
      @Override
      public Boolean execute(GLLParserInternals parser, Input input) {
        return input.isEndOfLine(parser.getCurrentInputIndex());
      }
    });
  }

  private void addStartOfLine(BodyGrammarSlot slot) {
    slot.addPopAction(new SlotAction<Boolean>() {
      private static final long serialVersionUID = 1L;
      
      @Override
      public Boolean execute(GLLParserInternals parser, Input input) {
        return input.isStartOfLine(parser.getCurrentInputIndex());
      }
    });
    
  }

  private void addNotMatch(BodyGrammarSlot slot, final BodyGrammarSlot ifNot) {

		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				GLLRecognizer recognizer = RecognizerFactory.contextFreeRecognizer();
				return recognizer.recognize(input, parser.getCurrentGSSNode().getInputIndex(), parser.getCurrentInputIndex(), ifNot);
			}
		});
		
	}
	
	private void addNotMatch(BodyGrammarSlot slot, final List<Keyword> list) {
		
		if(list.size() == 1) {
			final Keyword s = list.get(0);
			
			slot.addPopAction(new SlotAction<Boolean>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public Boolean execute(GLLParserInternals parser, Input input) {
					return input.match(parser.getCurrentGSSNode().getInputIndex(), parser.getCurrentInputIndex() - 1, s.getChars());
				}
			});
			
		} 
		
		else if(list.size() == 2) {
			final Keyword s1 = list.get(0);
			final Keyword s2 = list.get(1);
			
			slot.addPopAction(new SlotAction<Boolean>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public Boolean execute(GLLParserInternals parser, Input input) {
					int begin = parser.getCurrentGSSNode().getInputIndex();
					int end = parser.getCurrentInputIndex() - 1;
					return input.match(begin, end, s1.getChars()) ||
						   input.match(begin, end, s2.getChars())	;
				}
			});
		} 
		
		else {
			final CuckooHashSet<Keyword> set = new CuckooHashSet<>(Keyword.externalHasher);
			for(Keyword s : list) {
				set.add(s);
			}
			
			slot.addPopAction(new SlotAction<Boolean>() {

				private static final long serialVersionUID = 1L;
				
				@Override
				public Boolean execute(GLLParserInternals parser, Input input) {
					int begin = parser.getCurrentGSSNode().getInputIndex();
					int end = parser.getCurrentInputIndex() - 1;
					Keyword subInput = new Keyword("", input.subInput(begin, end));
					return set.contains(subInput);
				}
			});
			
		}
		
	}

	private void addNotFollow(BodyGrammarSlot slot, final BodyGrammarSlot firstSlot) {
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				GLLRecognizer recognizer = RecognizerFactory.prefixContextFreeRecognizer();
				return recognizer.recognize(input, parser.getCurrentInputIndex(), input.size(), firstSlot);
			}
		});
	}
	
	private void addNotFollow2(BodyGrammarSlot slot, final List<Keyword> list) {
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				for(Keyword s : list) {
					if(input.match(parser.getCurrentInputIndex(), s.getChars())) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	private void addNotFollow1(BodyGrammarSlot slot, List<Terminal> list) {
		
		BitSet testSet = new BitSet();
		
		for(Terminal t : list) {
			testSet.or(t.asBitSet());
		}
		
		final BitSet set = testSet;
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				return set.get(input.charAt(parser.getCurrentInputIndex()));
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
				currentSlot = new NonterminalGrammarSlot(index, currentSlot, nonterminal, null);
			} 
			else if(symbol instanceof Terminal) {
				currentSlot = new TerminalGrammarSlot(index, currentSlot, (Terminal) symbol, null);
			}
			else if(symbol instanceof Keyword) {
				currentSlot = new KeywordGrammarSlot(index, 
						nonterminalsMap.get(new Nonterminal(symbol.getName())), (Keyword) symbol, currentSlot, null);
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
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal.getName());

		if (headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminal);
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
		setSlotIds();
		setDirectNullables();
		
		calculateReachabilityGraph();
		calculateExpectedDescriptors();
	}
	
	
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
						}
					}
					currentSlot = currentSlot.next();
				}
			}
		}

	}

	public void rewritePrecedenceRules() {
		for (Entry<String, Set<PrecedencePattern>> entry : filtersMap.entrySet()) {
			log.debug("Filtering %s with %d.", entry.getKey(), entry.getValue().size());

			rewriteFirstLevel(nonterminalsMap.get(entry.getKey()), processFilters(entry.getValue()));
			filterDeep(nonterminalsMap.get(entry.getKey()), entry.getValue());
		}

		for (PrecedencePattern filter : oneLevelOnlyFilters) {
			onlyFirstLevelFilter(nonterminalsMap.get(filter.getNonterminal()), oneLevelOnlyFilters);
		}
		
		for(String s : getFilteredNonterminals()) {
			removeUnusedFilteredNonterminals(s);
		}

		for(List<HeadGrammarSlot> newNonterminals : newNonterminalsMap.values()) {
			nonterminals.addAll(newNonterminals);			
		}
	}
	
	private Set<String> getFilteredNonterminals() {
		Set<String> names = new HashSet<>();
		for (Set<PrecedencePattern> filters : filtersMap.values()) {
			for(PrecedencePattern filter : filters) {
				names.add(filter.getFilteredNontemrinalName());
			}
		}
		return names;
	}

	private void onlyFirstLevelFilter(HeadGrammarSlot head, Set<PrecedencePattern> filters) {
		for (Alternate alt : head.getAlternates()) {
			for (PrecedencePattern filter : filters) {
				if (match(filter, alt)) {

					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					HeadGrammarSlot newNonterminal = createNewNonterminal(alt, filter.getPosition(), filter.getChild());
					alt.setNonterminalAt(filter.getPosition(), newNonterminal);

					if (newNonterminal == null || newNonterminal.getNonterminal().getName() != filteredNonterminal.getNonterminal().getName()) {
						
						
						newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
						
						List<HeadGrammarSlot> newNonterminals = newNonterminalsMap.get(newNonterminal.getNonterminal().getName());
						if(newNonterminals == null) {
							newNonterminals = new ArrayList<>();
							newNonterminalsMap.put(newNonterminal.getNonterminal().getName(), newNonterminals);
						}
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
	
	private Map<PrecedencePattern, Set<List<Symbol>>> processFilters(Set<PrecedencePattern> filters) {
		Map<PrecedencePattern, Set<List<Symbol>>> group = new LinkedHashMap<>();
		
		for(PrecedencePattern filter : filters) {
			Set<List<Symbol>> set = group.get(filter);
			if(set == null) {
				set = new LinkedHashSet<>();
				group.put(filter, set);
			}
			set.add(filter.getChild());
		}
		
		return group;
	}


	private void rewriteFirstLevel(HeadGrammarSlot head, Map<PrecedencePattern, Set<List<Symbol>>> processFilters) {
		
		// This is complicated shit! Document it for the future reference.
		Map<PrecedencePattern, HeadGrammarSlot> freshNonterminals = new LinkedHashMap<>();
		
		Map<PrecedencePattern, HeadGrammarSlot> freshIndirectNonterminals = new LinkedHashMap<>();
		
		Map<HeadGrammarSlot, HeadGrammarSlot> IndirectToDirectMap = new LinkedHashMap<>();
		
		Map<Set<List<Symbol>>, HeadGrammarSlot> map = new HashMap<>();
		
		
		// Creating fresh nonterminals
		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : processFilters.entrySet()) {
			PrecedencePattern pattern = e.getKey();
			
			HeadGrammarSlot freshNonterminal = map.get(e.getValue());
			if(map.get(e.getValue()) == null) {
				freshNonterminal = new HeadGrammarSlot(new Nonterminal(pattern.getNonterminal()));
				map.put(e.getValue(), freshNonterminal);
			}

			freshNonterminals.put(pattern, map.get(e.getValue()));
			
			if(!pattern.isDirect()) {
				HeadGrammarSlot indirectFreshNonterminal = new HeadGrammarSlot(new Nonterminal(pattern.getFilteredNontemrinalName()));
				IndirectToDirectMap.put(indirectFreshNonterminal, freshNonterminal);
			}
		}
		
		// Replacing nonterminals with their fresh ones
		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : processFilters.entrySet()) {
			for(Alternate alt : head.getAlternates()) {
				PrecedencePattern pattern = e.getKey();
				log.trace("Apply the pattern %s on %s.", pattern, alt);
				
				if (!pattern.isDirect()) {
					HeadGrammarSlot indirectNonterminal = freshIndirectNonterminals.get(pattern);
					alt.setNonterminalAt(pattern.getPosition(), indirectNonterminal);	
					createIndirectPaths(alt.getNonterminalAt(pattern.getPosition()), indirectNonterminal, IndirectToDirectMap.get(indirectNonterminal));
				}

				alt.setNonterminalAt(pattern.getPosition(), freshNonterminals.get(pattern));				
			}
		}
		
		// creating the body of fresh nonterminals
		for(Entry<PrecedencePattern, HeadGrammarSlot> e : freshNonterminals.entrySet()) {
			PrecedencePattern pattern = e.getKey();
			HeadGrammarSlot freshNontermianl = e.getValue();
			
			Set<Alternate> alternates = head.without(processFilters.get(pattern));
			List<Alternate> copyAlternates = copyAlternates(head, alternates);
			freshNontermianl.setAlternates(copyAlternates);
		}
	}
	
	private void createIndirectPaths(HeadGrammarSlot head, HeadGrammarSlot freshIndirectNonterminal, HeadGrammarSlot freshNontemrinal) {
		List<Alternate> copyAlternates = copyAlternates(freshIndirectNonterminal, head.getAlternates());
		freshIndirectNonterminal.setAlternates(copyAlternates);
		
		for(Alternate alt : copyAlternates) {
			if(alt.getLastBodySlot() instanceof NonterminalGrammarSlot) {
				NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) alt.getLastBodySlot();
				if(ntSlot.getNonterminal().equals(freshNontemrinal.getNonterminal())) {
					alt.setNonterminalAt(alt.size() - 1, freshNontemrinal);
				} else {
					HeadGrammarSlot newNt = new HeadGrammarSlot(ntSlot.getNonterminal().getNonterminal());
					createIndirectPaths(ntSlot.getNonterminal(), newNt, freshNontemrinal);
				}
			}
		}
	}

	private void filterDeep(HeadGrammarSlot head, Set<PrecedencePattern> filters) {
		for (Alternate alt : head.getAlternates()) {
			for (PrecedencePattern filter : filters) {

				if (alt.match(filter.getParent())) {

					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());

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

	private HeadGrammarSlot createNewNonterminal(Alternate alt, int position, List<Symbol> filteredAlternate) {
		
		HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(position);
		
		HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternate));
		if(newNonterminal == null) {
			newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
			
			List<HeadGrammarSlot> set = newNonterminalsMap.get(filteredNonterminal.getNonterminal().getName());
			if(set == null) {
				set = new ArrayList<>();
				newNonterminalsMap.put(filteredNonterminal.getNonterminal().getName(), set);
			}
			set.add(newNonterminal);
			
			alt.setNonterminalAt(position, newNonterminal);
			
			List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.without(filteredAlternate));
			existingAlternates.put(new HashSet<>(copy), newNonterminal);
			newNonterminal.setAlternates(copy);
		} else {
			alt.setNonterminalAt(position, newNonterminal);						
		}		
			
		return newNonterminal;
	}

	private boolean match(PrecedencePattern filter, Alternate alt) {
		if (alt.match(filter.getParent())) {

			HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());

			// Indirect filter
			if (!oneLevelOnlyFilters.contains(filter) && !filter.getNonterminal().equals(filteredNonterminal.getNonterminal().getName())) {
				List<HeadGrammarSlot> nonterminals = new ArrayList<>();
				getRightEnds(filteredNonterminal, filter.getNonterminal(), nonterminals);
				for(HeadGrammarSlot nonterminal : nonterminals) {
					if(nonterminal.contains(filter.getChild())) {
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
				HeadGrammarSlot newNonterminal = createNewNonterminal(alternate, alternate.size() - 1, filteredAlternate);
				if(newNonterminal != ((NonterminalGrammarSlot) alternate.getBodyGrammarSlotAt(alternate.size() - 1)).getNonterminal()) {
					rewriteRightEnds(newNonterminal, filteredAlternate);
				}
			}
		}
	}	

	private void rewriteLeftEnds(HeadGrammarSlot head, List<Symbol> filteredAlternate) {
		for (Alternate alternate : head.getAlternates()) {
			if (alternate.isBinary(head) || alternate.isUnaryPostfix(head)) {
				HeadGrammarSlot newNonterminal = createNewNonterminal(alternate, 0, filteredAlternate);
				if(newNonterminal != ((NonterminalGrammarSlot) alternate.getBodyGrammarSlotAt(0)).getNonterminal()) {
					rewriteLeftEnds(newNonterminal, filteredAlternate);
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
	private void getRightEnds(HeadGrammarSlot head, String name, List<HeadGrammarSlot> nonterminals) {
		for (Alternate alt : head.getAlternates()) {
			if (alt.getLastBodySlot() instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alt.getLastBodySlot()).getNonterminal();
				if (nonterminal.getNonterminal().getName().equals(name)) {
					nonterminals.add(nonterminal);
				} else {
					getRightEnds(nonterminal, name, nonterminals);
				}
			}
		}
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
			copy = ((KeywordGrammarSlot) slot).copy(nonterminalsMap.get(keyword.getName()), previous, head);
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
		String name = nonterminal.getName();
		PrecedencePattern filter = new PrecedencePattern(name, parent.getBody(), position, child.getBody());

		if (name.equals(child.getHead().getName())) {
			if (filtersMap.containsKey(name)) {
				filtersMap.get(name).add(filter);
			} else {
				Set<PrecedencePattern> set = new LinkedHashSet<>();
				set.add(filter);
				filtersMap.put(name, set);
			}
			log.debug("Filter added %s (deep)", filter);
		} else {
			oneLevelOnlyFilters.add(filter);
			log.debug("Filter added %s (one level only)", filter);
		}
	}
	
	public Set<HeadGrammarSlot> getReachableNonterminals(String name) {
		return reachabilityGraph.get(nonterminalsMap.get(name));
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
		queue.add(nonterminalsMap.get(nonterminal.getName()));
		
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
	
	private void removeUnusedFilteredNonterminals(String nonterminalName) {

		Set<HeadGrammarSlot> referedNonterminals = new HashSet<>();
		Deque<HeadGrammarSlot> queue = new ArrayDeque<>();
		queue.add(nonterminalsMap.get(nonterminalName));
		
		while(!queue.isEmpty()) {
			HeadGrammarSlot head = queue.poll();
			referedNonterminals.add(head);
			
			for(Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
				while(currentSlot.next() != null) {
					if(currentSlot instanceof NonterminalGrammarSlot) {
						HeadGrammarSlot reachableHead = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!referedNonterminals.contains(reachableHead)) {
							queue.add(reachableHead);
						}
					}
					currentSlot = currentSlot.next();
				}
			}
		}

		List<HeadGrammarSlot> set = newNonterminalsMap.get(nonterminalName);
		if(set != null) {
			newNonterminalsMap.get(nonterminalName).retainAll(referedNonterminals);
		}
	}

	
}