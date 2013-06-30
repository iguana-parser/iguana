package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.parser.GLLParser;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

public class GrammarBuilder implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarBuilder.class);

	Map<String, HeadGrammarSlot> nonterminalsMap;
	
	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> nonterminals;
	
	int longestTerminalChain;
	
	String name;

	// Fields related to filtering	
	List<HeadGrammarSlot> newNonterminals;
	
	private Map<Set<Alternate>, HeadGrammarSlot> existingAlternates;
	
	private Map<String, Set<Filter>> filtersMap;
	
	private Set<Filter> oneLevelOnlyFilters;
	
	public GrammarBuilder(String name) {
		this.name = name;
		nonterminals = new ArrayList<>();
		slots = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
		filtersMap = new HashMap<>();
		existingAlternates = new HashMap<>();
		newNonterminals = new ArrayList<>();
		
		oneLevelOnlyFilters = new HashSet<>();
	}
	
	public Grammar build() {
		initializeGrammarProrperties();
		validate();
		return new Grammar(this);
	}
	
	public void validate() {
		GrammarVisitor visitor = new GrammarVisitor(new GrammarVisitAction() {
			
			@Override
			public void visit(LastGrammarSlot slot) {
			}
			
			@Override
			public void visit(TerminalGrammarSlot slot) {
			}
			
			@Override
			public void visit(NonterminalGrammarSlot slot) {
				if(slot.getNonterminal() == null) {
					throw new GrammarValidationException("No nonterminal defined for " + slot.getLabel());
				}
				if(slot.getNonterminal().getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + slot.getNonterminal().getLabel());
				}
			}
			
			@Override
			public void visit(HeadGrammarSlot head) {
				if(head.getAlternates().size() == 0) {
					throw new GrammarValidationException("No alternates defined for " + head.getLabel());
				}
			}
		});
		
		for(HeadGrammarSlot head : nonterminals) {
			visitor.visit(head);
		}
	}
	
	public GrammarBuilder addRule(Rule rule, final List<List<List<CharacterClass>>> notFollow, 
								 final List<String> deleteSet, final List<CharacterClass> precedeRestrictions) {

		Map<BodyGrammarSlot, List<List<CharacterClass>>> followRestrionMap = new HashMap<>();
		Map<BodyGrammarSlot, CharacterClass> preConditionMap = new HashMap<>();
		
		if(rule == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();
		
		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);
		
		BodyGrammarSlot currentSlot = null;
		
		if(body.size() == 0) {
			currentSlot = new EpsilonGrammarSlot(grammarSlotToString(head, body, 0), 0, headGrammarSlot, rule.getObject());
			headGrammarSlot.addAlternate(new Alternate(currentSlot, 0));
			slots.add(currentSlot);
		} 
		
		else {
			int symbolIndex = 0;
			BodyGrammarSlot firstSlot = null;
			for (Symbol symbol : body) {
				if (symbol.isTerminal()) {
					currentSlot = new TerminalGrammarSlot(grammarSlotToString(head, body, symbolIndex), symbolIndex, currentSlot, (Terminal) symbol, headGrammarSlot);
				} else {
					HeadGrammarSlot nonterminal = getHeadGrammarSlot((Nonterminal) symbol);
					currentSlot = new NonterminalGrammarSlot(grammarSlotToString(head, body, symbolIndex), symbolIndex, currentSlot, nonterminal, headGrammarSlot);
				}
				slots.add(currentSlot);
				followRestrionMap.put(currentSlot, notFollow.get(symbolIndex));
				preConditionMap.put(currentSlot, precedeRestrictions.get(symbolIndex));

				if (symbolIndex == 0) {
					firstSlot = currentSlot;
				}
				symbolIndex++;
			}
			
			LastGrammarSlot lastGrammarSlot = new LastGrammarSlot(grammarSlotToString(head, body, symbolIndex), symbolIndex, currentSlot, headGrammarSlot, rule.getObject());
			
			if(deleteSet != null && !deleteSet.isEmpty()) {
				lastGrammarSlot.addPopAction(new SlotAction<Boolean>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean execute(GLLParser parser, Input input) {
						int currentIndex = parser.getCi();
						int lastIndex = parser.getCu().getInputIndex();
												
						for(String s : deleteSet) {
							if(match(s, input.subString(lastIndex, currentIndex))) {
								return false;
							}
						}
						
						return true;
					}
				});
			}
			
			slots.add(lastGrammarSlot);
			headGrammarSlot.addAlternate(new Alternate(firstSlot, headGrammarSlot.getAlternates().size()));
		}
		
		for(Entry<BodyGrammarSlot, List<List<CharacterClass>>> e : followRestrionMap.entrySet()) {
			addFollowRestriction(e.getKey().next(), e.getValue());
		}
		
		for(Entry<BodyGrammarSlot, CharacterClass> e : preConditionMap.entrySet()) {
			addPrecedeRestriction(e.getKey(), e.getValue());
		}
		
		return this;
	}
	
	private void addPrecedeRestriction(BodyGrammarSlot slot, final CharacterClass characterClass) {
		if(characterClass != null) {
			
			log.debug("Precede restriction added {} <<! {}", characterClass, slot);
			slot.addPreCondition(new SlotAction<Boolean>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public Boolean execute(GLLParser parser, Input input) {
					int ci = parser.getCi();
					if(ci == 0) {
						return true;
					}
					
					return !characterClass.match(input.charAt(ci - 1));
				}
			});
		}
	}

	private void addFollowRestriction(BodyGrammarSlot slot, final List<List<CharacterClass>> followRestriction) {
		if(followRestriction == null || followRestriction.isEmpty()) {
			return;
		}
		
		log.debug("Follow restriction added {} !>> {}", slot, followRestriction);
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParser parser, Input input) {
				return match(followRestriction, input, parser.getCi());
			}
		});
	}
	
	/**
	 * 
	 * @return true if the given string matches the given substring (as an integer array)
	 */
	private boolean match(String s, int[] subString) {
		if(s.length() != subString.length) {
			return false;
		}
		for(int i = 0; i < subString.length; i++) {
			if(s.charAt(i) != subString[i]) {
				return false;
			}
		}
		return true;
	}
	
	private boolean match(List<List<CharacterClass>> followRestrictions, Input input, int i) {
		if(i >= input.size()) {
			return true;
		}
		
		for(List<CharacterClass> followRestriction : followRestrictions) {
			int index = i;
			
			boolean match = true;
			for(CharacterClass characterClass : followRestriction) {
				if(!characterClass.match(input.charAt(index))) {
					match = false;
					break;
				}
				
				if(index >= input.size()) {
					break;
				}
				index++;
			}
			
			// If one of the follow restrictions matches from the next character, don't pop!
			if(match == true) {
				return false;
			}
		}
		
		return true;
	}
	
	public GrammarBuilder addRule(Rule rule) {
		List<List<List<CharacterClass>>> list = new ArrayList<>();
		List<CharacterClass> precedeRestrictions = new ArrayList<>();
		for(int i = 0; i < rule.size(); i++) {
			list.add(null);
			precedeRestrictions.add(null);
		}
		return addRule(rule, list, null, precedeRestrictions);
	}
	
	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal.getName());
		
		if(headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminal);
			nonterminalsMap.put(nonterminal.getName(), headGrammarSlot);
			nonterminals.add(headGrammarSlot);
		}
		
		return headGrammarSlot;
	}
	
	private void initializeGrammarProrperties() {
		calculateLongestTerminalChain();
		calculateFirstSets();
		calculateFollowSets();
		setTestSets();
		setIds();
	}
	
	private static String grammarSlotToString(Nonterminal head, List<Symbol> body, int index) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(head.getName()).append(" ::= ");
		
		for(int i = 0; i < body.size(); i++) {
			if(i == index) {
				sb.append(". ");
			}
			sb.append(body.get(i)).append(" ");
		}
		
		sb.delete(sb.length() - 1, sb.length());
		
		if(index == body.size()) {
			sb.append(" .");
		}
		
		return sb.toString();
	}
	
	/**
	 * Calculates the longest chain of terminals in a body of a production rule. 
	 */
	private void calculateLongestTerminalChain() {
		
		int longestTerminalChain = 0;
		
		for(HeadGrammarSlot head : nonterminals) {
			for(Alternate alternate : head.getAlternates()) {
				BodyGrammarSlot slot = alternate.getFirstSlot();
				int length = 0; // The length of the longest terminal chain for this rule
				while(!(slot.isLastSlot())) {
					if(slot.isTerminalSlot()) {
						length++;
					} 
					else {
						// If a terminal is seen reset the length of the longest chain
						if(length > longestTerminalChain) {
							longestTerminalChain = length;
						}
						length = 0;
					}
					slot = slot.next;
				}
				if(length > longestTerminalChain) {
					longestTerminalChain = length;
				}
			}
		}
		
		this.longestTerminalChain = longestTerminalChain;
	}
	
	private void calculateFirstSets() {
		boolean changed = true;
		
		while(changed) {
			changed = false;
			for(HeadGrammarSlot head : nonterminals) {
				
				for(Alternate alternate : head.getAlternates()) {
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
		
		if(currentSlot instanceof EpsilonGrammarSlot) {
			return set.add(Epsilon.getInstance()) || changed;
		}
		
		else if(currentSlot instanceof TerminalGrammarSlot) {
			return set.add(((TerminalGrammarSlot) currentSlot).getTerminal()) || changed;
		}
		
		else if(currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			changed = set.addAll(nonterminalGrammarSlot.getNonterminal().getFirstSet()) || changed;
			if(nonterminalGrammarSlot.getNonterminal().isNullable()) {
				return addFirstSet(set, currentSlot.next, changed) || changed;
			}
			return changed;
		} 
		
		// ignore LastGrammarSlot
		else {
			return changed;
		}
	}
	
	
	private boolean isChainNullable(BodyGrammarSlot slot) {
		if(!(slot instanceof LastGrammarSlot)) {
			if(slot instanceof TerminalGrammarSlot) {
				return false;
			}
			
			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return ntGrammarSlot.getNonterminal().isNullable() && isChainNullable(ntGrammarSlot.next);
		}
		
		return true;
	}
	
	private void calculateFollowSets() {
		boolean changed = true;
		
		while(changed) {
			changed = false;
			for(HeadGrammarSlot head : nonterminals) {
				
				for(Alternate alternate : head.getAlternates()) {
					BodyGrammarSlot currentSlot = alternate.getFirstSlot();
					
					while(!(currentSlot instanceof LastGrammarSlot)) {
						
						if(currentSlot instanceof NonterminalGrammarSlot) {
							
							NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
							BodyGrammarSlot next = currentSlot.next;
							
							// For rules of the form X ::= alpha B, add the follow set of X to the
							// follow set of B.
							if(next instanceof LastGrammarSlot) {
								changed |= nonterminalGrammarSlot.getNonterminal().getFollowSet().addAll(head.getFollowSet());
								break;
							}
							
							// For rules of the form X ::= alpha B beta, add the first set of beta to
							// the follow set of B.
							Set<Terminal> followSet = nonterminalGrammarSlot.getNonterminal().getFollowSet();
							changed |= addFirstSet(followSet, currentSlot.next, changed);
							
							// If beta is nullable, then add the follow set of X to the follow set of B.
							if(isChainNullable(next)) {
								changed |= nonterminalGrammarSlot.getNonterminal().getFollowSet().addAll(head.getFollowSet());
							}
						}
						
						currentSlot = currentSlot.next;
					}
				}
			}
		}
		
		for(HeadGrammarSlot head : nonterminals) {
			// Remove the epsilon which may have been added from nullable nonterminals 
			head.getFollowSet().remove(Epsilon.getInstance());
			
			// Add the EOF to all nonterminals as each nonterminal can be used as
			// the start symbol.
			head.getFollowSet().add(EOF.getInstance());
		}
	}
	
	private void setTestSets() {
		for(HeadGrammarSlot head : nonterminals) {
			for(Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
				while(!currentSlot.isLastSlot()) {
					if(currentSlot instanceof NonterminalGrammarSlot) {
						Set<Terminal> testSet = new HashSet<>();
						addFirstSet(testSet, currentSlot, false);
						if(testSet.contains(Epsilon.getInstance())) {
							testSet.addAll(head.getFollowSet());
						}
						testSet.remove(Epsilon.getInstance());
						((NonterminalGrammarSlot) currentSlot).setTestSet(testSet);
					}
					currentSlot = currentSlot.next();
				}				
			}
		}
	}
	
	private void setIds() {
		int i = 0;
		for(HeadGrammarSlot head : nonterminals) {
			head.setId(i++);
		}
		i = 0;
		for(BodyGrammarSlot slot : slots) {
			slot.setId(i++);
		}
	}
		
	public void filter() {
		for(Entry<String, Set<Filter>> entry : filtersMap.entrySet()) {
			log.debug("Filtering {} with {} filters.", entry.getKey(), entry.getValue().size());
			
			filterFirstLevel(nonterminalsMap.get(entry.getKey()), entry.getValue());
			filterDeep(nonterminalsMap.get(entry.getKey()), entry.getValue());
		}
		
		for(Filter filter : oneLevelOnlyFilters) {
			onlyFirstLevelFilter(nonterminalsMap.get(filter.getNonterminal()), oneLevelOnlyFilters);
		}
			
		nonterminals.addAll(newNonterminals);
	}
	
	private void onlyFirstLevelFilter(HeadGrammarSlot head, Set<Filter> filters) {
		for(Alternate alt : head.getAlternates()) {
			for(Filter filter : filters) {
				if(match(filter, alt)) {
					
					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filter.getChild()));
					
					if(newNonterminal == null || newNonterminal.getNonterminal().getName() != filteredNonterminal.getNonterminal().getName()) {
						newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
						newNonterminals.add(newNonterminal);
						
						List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.getAlternates());
						newNonterminal.setAlternates(copy);
						newNonterminal.remove(filter.getChild());
						existingAlternates.put(new HashSet<>(copyAlternates(newNonterminal, copy)), newNonterminal);
						onlyFirstLevelFilter(newNonterminal, filters);
					} 
					else {
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
					}
				}
			}
		}
	}
	
	private Map<Set<Integer>, HeadGrammarSlot> firstLevels = new HashMap<>();
	
	private void filterFirstLevel(HeadGrammarSlot head, Set<Filter> filters) {
		for(Alternate alt : head.getAlternates()) {
			for(Filter filter : filters) {
				if(!filter.isDirect()) {
					continue;
				}
				if(match(filter, alt)) {
					
					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					
					Set<Alternate> without = filteredNonterminal.without(filter.getChild());
					Set<Integer> intSet = new HashSet<>();
					for(Alternate a : without) {
						intSet.add(a.getIndex());
					}
					HeadGrammarSlot newNonterminal = firstLevels.get(intSet);
					
					if(newNonterminal == null || newNonterminal.getNonterminal().getName() != filteredNonterminal.getNonterminal().getName()) {
						newNonterminal = new HeadGrammarSlot(filteredNonterminal.getNonterminal());
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
						newNonterminals.add(newNonterminal);
						
						newNonterminal.setAlternates(filteredNonterminal.getAlternates());
						newNonterminal.remove(filter.getChild());
						firstLevels.put(newNonterminal.getAlternateIndices(), newNonterminal);						
					} 
					else {
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
					}
					
				}
			}
		}
		
		for(HeadGrammarSlot newNonterminal : newNonterminals) {
			List<Alternate> copy = copyAlternates(newNonterminal, newNonterminal.getAlternates());
			newNonterminal.setAlternates(copy);
		}
	}
	
	private void filterDeep(HeadGrammarSlot head, Set<Filter> filters) {
		for(Alternate alt : head.getAlternates()) {
			for(Filter filter : filters) {

				if(alt.match(filter.getParent())) {
				
					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					
					// Indirect filtering
					if(!filter.isDirect()) {
						List<Integer> nontemrinalIndices = new ArrayList<>();
						List<Alternate> alternates = new ArrayList<>();
						getRightEnds(filteredNonterminal, filter.getNonterminal(), nontemrinalIndices, alternates);
						for(int i = 0; i < nontemrinalIndices.size(); i++) {
							HeadGrammarSlot rightEndNonterminal = alternates.get(i).getNonterminalAt(nontemrinalIndices.get(i));
							HeadGrammarSlot newNonterminal = existingAlternates.get(rightEndNonterminal.without(filter.getChild()));
							
							if(newNonterminal == null) {
								rewrite(alternates.get(i), nontemrinalIndices.get(i), filter.getChild());
							} else {
								alternates.get(i).setNonterminalAt(nontemrinalIndices.get(i), newNonterminal);							
							}
						}							
					}
					
					if(filter.isLeftMost() && !filter.isChildBinary()) {
						rewriteRightEnds(filteredNonterminal, filter.getChild());
					}
					
					if(filter.isRightMost() && !filter.isChildBinary()) {
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
		if(alt.match(filter.getParent())) {
			
			HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
			
			// If it the filtered nonterminal is an indirect one
			if(!oneLevelOnlyFilters.contains(filter) && !filter.getNonterminal().equals(filteredNonterminal.getNonterminal().getName())) {
				List<Integer> nonterminals = new ArrayList<>();
				List<Alternate> alternates = new ArrayList<>();
				getRightEnds(filteredNonterminal, filter.getNonterminal(), nonterminals, alternates);
				for(int i = 0; i < alternates.size(); i++) {
					if(alternates.get(i).getNonterminalAt(nonterminals.get(i)).contains(filter.getChild())) {
						return true;
					}
				}
			} 
			else {
				if(filteredNonterminal.contains(filter.getChild())) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	private void rewriteRightEnds(HeadGrammarSlot head, List<Symbol> filteredAlternate) {
		for(Alternate alternate : head.getAlternates()) {
			if(alternate.isBinary(head) || alternate.isUnaryPrefix(head)) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alternate.getLastSlot()).getNonterminal();
				
				if(nonterminal.contains(filteredAlternate)) {
					HeadGrammarSlot filteredNonterminal = alternate.getNonterminalAt(alternate.size() - 1);
					
					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternate));
					if(newNonterminal == null) {
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
		for(Alternate alternate : head.getAlternates()) {
			if(alternate.isBinary(head) || alternate.isUnaryPostfix(head)) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alternate.getFirstSlot()).getNonterminal();
				
				if(nonterminal.contains(filteredAlternate)) {
					HeadGrammarSlot filteredNonterminal = alternate.getNonterminalAt(0);
					
					HeadGrammarSlot newNonterminal = existingAlternates.get(filteredNonterminal.without(filteredAlternate));
					if(newNonterminal == null) {
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
	 * Returns a list of all nonterminals with the given name which are reachable
	 * from head and are on the right-most end.
	 * 
	 * @param head
	 * @param name
	 * @param nonterminals
	 */
	private void getRightEnds(HeadGrammarSlot head, String name, List<Integer> nonterminals, List<Alternate> alternates) {
		for(Alternate alt : head.getAlternates()) {
			if(alt.getLastSlot().isNonterminalSlot()) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot)alt.getLastSlot()).getNonterminal();
				if(nonterminal.getNonterminal().getName().equals(name)) {
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
		for(Alternate alt : list) {
			copyList.add(copyAlternate(alt, head));
		}
		return copyList;
	}
	
	private Alternate copyAlternate(Alternate alternate, HeadGrammarSlot head) {
		BodyGrammarSlot copyFirstSlot = copySlot(alternate.getFirstSlot(), null, head);
		
		BodyGrammarSlot current = alternate.getFirstSlot().next;
		BodyGrammarSlot copy = copyFirstSlot;
		
		while(current != null) {
			copy = copySlot(current, copy, head);
			current = current.next;
		}
		 
		return new Alternate(copyFirstSlot, alternate.getIndex());
	}
	
	private BodyGrammarSlot copySlot(BodyGrammarSlot slot, BodyGrammarSlot previous, HeadGrammarSlot head) {

		BodyGrammarSlot copy;
		
		if(slot.isLastSlot()) {
			copy = new LastGrammarSlot(slot.label, slot.position, previous, head, ((LastGrammarSlot) slot).getObject());
		} 
		else if(slot.isNonterminalSlot()) {
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) slot;
			copy = new NonterminalGrammarSlot(slot.label, slot.position, previous, ntSlot.getNonterminal(), head);
		} 
		else {
			copy = new TerminalGrammarSlot(slot.label, slot.position, previous, ((TerminalGrammarSlot) slot).getTerminal(), head);
		}

		copy.popActions = slot.popActions;
		copy.preConditions = slot.preConditions;
		slots.add(copy);
		return copy;
	}
		
	@SafeVarargs
	protected static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}


	/**
	 * 
	 * Adds the given filter to the set of filters. If a filter with the same nonterminal, alternate index, and
	 * alternate index already exists, only the given filter alternates are added to the existing filter,
	 * effectively updating the filter.
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

		if(name.equals(child.getHead().getName())) {
			if(filtersMap.containsKey(name)) {
				filtersMap.get(name).add(filter);
			} 
			else {
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
		
}