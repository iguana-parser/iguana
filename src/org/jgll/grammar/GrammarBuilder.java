package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GrammarBuilder {

	private Map<Nonterminal, HeadGrammarSlot> nonterminalsMap;
	
	List<BodyGrammarSlot> slots;
	
	List<HeadGrammarSlot> nonterminals;
	
	int longestTerminalChain;
	
	String name;
	
	public GrammarBuilder(String name) {
		this.name = name;
		nonterminals = new ArrayList<>();
		slots = new ArrayList<>();
		nonterminalsMap = new HashMap<>();
	}
	
	public Grammar build() {
		initializeGrammarProrperties();
		return new Grammar(this);
	}
	
	public GrammarBuilder addRule(Rule rule) {
		
		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();
		
		HeadGrammarSlot headGrammarSlot = getHeadGrammarSlot(head);
		
		BodyGrammarSlot currentSlot = null;
		
		if(body.size() == 0) {
			currentSlot = new EpsilonGrammarSlot(nextSlotId(), grammarSlotToString(head, body, 0), 0, new HashSet<Terminal>(), headGrammarSlot, rule.getObject());
			headGrammarSlot.addAlternate(new Alternate(currentSlot));
			slots.add(currentSlot);
		} 
		
		else {
			int index = 0;
			BodyGrammarSlot firstSlot = null;
			for (Symbol symbol : body) {
				if (symbol instanceof Terminal) {
					currentSlot = new TerminalGrammarSlot(nextSlotId(), grammarSlotToString(head, body, index), index, currentSlot, (Terminal) symbol, headGrammarSlot);
				} else {
					currentSlot = new NonterminalGrammarSlot(nextSlotId(), grammarSlotToString(head, body, index), index, currentSlot, getHeadGrammarSlot((Nonterminal) symbol), headGrammarSlot);
				}
				slots.add(currentSlot);

				if (index == 0) {
					firstSlot = currentSlot;
				}
				index++;
			}
			
			LastGrammarSlot lastGrammarSlot = new LastGrammarSlot(nextSlotId(), grammarSlotToString(head, body, index), index, currentSlot, headGrammarSlot, rule.getObject());
			slots.add(lastGrammarSlot);
			headGrammarSlot.addAlternate(new Alternate(firstSlot));

		}
		
		return this;
	}
	
	private int nextSlotId() {
		return nonterminals.size() + slots.size();
	}
	
	private HeadGrammarSlot getHeadGrammarSlot(Nonterminal nonterminal) {
		HeadGrammarSlot headGrammarSlot = nonterminalsMap.get(nonterminal);
		
		if(headGrammarSlot == null) {
			headGrammarSlot = new HeadGrammarSlot(nonterminals.size(), nonterminal);
			nonterminalsMap.put(nonterminal, headGrammarSlot);
			nonterminals.add(headGrammarSlot);
		}
		
		return headGrammarSlot;
	}
	
	private void initializeGrammarProrperties() {
		calculateLongestTerminalChain();
		calculateFirstSets();
		calculateFollowSets();
		setTestSets();
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
				
				if(currentSlot instanceof NonterminalGrammarSlot) {
					Set<Terminal> testSet = new HashSet<>();
					addFirstSet(testSet, currentSlot, false);
					if(testSet.contains(Epsilon.getInstance())) {
						testSet.addAll(head.getFollowSet());
					}
					testSet.remove(Epsilon.getInstance());
					((NonterminalGrammarSlot) currentSlot).setTestSet(testSet);
				}
			}
		}
	}

	
}
