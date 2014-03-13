package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Tuple;

public class GrammarProperties {
	
	public static Map<Nonterminal, Set<RegularExpression>> calculateFirstSets(Map<Nonterminal, Set<List<Symbol>>> definitions) {
		
		Map<Nonterminal, Set<RegularExpression>> firstSets = new HashMap<>();
		
		Set<Nonterminal> nonterminals = definitions.keySet();
		for (Nonterminal head : nonterminals) {
			firstSets.put(head, new HashSet<RegularExpression>());
		}
		
		boolean changed = true;

		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {
				for (List<Symbol> alternate : definitions.get(head)) {
					Set<RegularExpression> firstSet = firstSets.get(head);
					changed |= addFirstSet(firstSet, alternate, 0, firstSets);
				}
			}
		}
		
		return firstSets;
	}
	
	/**
	 * Adds the first set of the current slot to the given set.
	 * 
	 * @param firstSet
	 * @param currentSlot
	 * @param changed
	 * 
	 * @return true if adding any new terminals are added to the first set.
	 */
	private static boolean addFirstSet(Set<RegularExpression> firstSet, 
									   List<Symbol> alternate, int index,
									   Map<Nonterminal, Set<RegularExpression>> firstSets) {

		boolean changed = false;
		
		if(alternate == null) {
			return false;
		}
		 
		//TODO: check if it is allowed or is it a good idea to enforce the instantiation of Epsilon.
		if (alternate.size() == 0) {
			return firstSet.add(Epsilon.getInstance());
		}
		
		for(int i = index; i < alternate.size(); i++) {
			Symbol symbol = alternate.get(i);

			if (symbol instanceof RegularExpression) {
				RegularExpression regularExpression = (RegularExpression) symbol;
				changed |= firstSet.add(regularExpression);
				if(!regularExpression.isNullable()) {
					break;
				}
			}
			
			// Nonterminal
			else if (symbol instanceof Nonterminal) {
				Nonterminal nonterminal = (Nonterminal) symbol;
				
				Set<RegularExpression> set = new HashSet<>(firstSets.get(nonterminal));
				set.remove(Epsilon.getInstance());
				changed |= firstSet.addAll(set);
				if (!isNullable(nonterminal, firstSets)) {
					break;
				}
			}
		}
		
		if (isChainNullable(alternate, 0, firstSets)) {
			changed |= firstSet.add(Epsilon.getInstance());
		}
		
		return changed;
	}
	
	private static boolean isNullable(Nonterminal nt, Map<Nonterminal, Set<RegularExpression>> firstSets) {
		return firstSets.get(nt).contains(Epsilon.getInstance());
	}
	
	/**
	 * 
	 * Checks if a grammar slot is nullable. This check is performed until
	 * the end of the alternate: isChainNullable(X ::= alpha . beta) says if the
	 * part beta is nullable.
	 *   
	 */
	private static boolean isChainNullable(List<Symbol> alternate, int index, Map<Nonterminal, Set<RegularExpression>> firstSets) {
		
		if(index >= alternate.size()) {
			return true;
		}
		
		for(int i = index; i < alternate.size(); i++) {
			Symbol s = alternate.get(i);
			
			if(s instanceof RegularExpression) {
				if(!((RegularExpression)s).isNullable()) {
					return false;
				}
			}
			
			if(!isNullable((Nonterminal) s, firstSets)) {
				return false;
			}
		}

		return true;
	}
		
	public static Map<Nonterminal, Set<RegularExpression>> calculateFollowSets(Map<Nonterminal, Set<List<Symbol>>> definitions, 
																		 	   Map<Nonterminal, Set<RegularExpression>> firstSets) {
		
		Map<Nonterminal, Set<RegularExpression>> followSets = new HashMap<>();
		
		Set<Nonterminal> nonterminals = definitions.keySet();
		
		for (Nonterminal head : nonterminals) {
			followSets.put(head, new HashSet<RegularExpression>());
		}
		
		boolean changed = true;

		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {

				for (List<Symbol> alternate : definitions.get(head)) {
					
					if(alternate == null || alternate.size() == 0) {
						continue;
					}
					
					for(int i = 0; i < alternate.size(); i++) {
					
						Symbol symbol = alternate.get(i);
						
						if(symbol instanceof Nonterminal) {
							Nonterminal nonterminal = (Nonterminal) symbol;

							// For rules of the form X ::= alpha B beta, add the
							// first set of beta to
							// the follow set of B.
							Set<RegularExpression> followSet = followSets.get(nonterminal);
							changed |= addFirstSet(followSet, alternate, i + 1, firstSets);

							// If beta is nullable, then add the follow set of X
							// to the follow set of B.
							if (isChainNullable(alternate, i + 1, firstSets)) {
								changed |= followSet.addAll(followSets.get(head));
							}
						}
					}
				}
			}
		}

		for (Nonterminal head : nonterminals) {
			// Remove the epsilon which may have been added from nullable
			// nonterminals
			followSets.get(head).remove(Epsilon.getInstance());

			// Add the EOF to all nonterminals as each nonterminal can be used
			// as the start symbol.
			followSets.get(head).add(EOF.getInstance());
		}
		
		return followSets;
	}
	
	public static Map<Nonterminal, List<Set<RegularExpression>>> getPredictionSets(Map<Nonterminal, Set<List<Symbol>>> definitions,
																				  Map<Nonterminal, Set<RegularExpression>> firstSets,
																				  Map<Nonterminal, Set<RegularExpression>> followSets) {

		Map<Nonterminal, List<Set<RegularExpression>>> predictionSets = new HashMap<>();
		
		for(Nonterminal nonterminal : definitions.keySet()) {
			Set<List<Symbol>> alternates = definitions.get(nonterminal);
			
			List<Set<RegularExpression>> list = new ArrayList<>();

			for(List<Symbol> alternate : alternates) {
				
				if(alternate == null) {
					continue;
				}
				
				Set<RegularExpression> predictionSet = new HashSet<>();
				
				for(int i = 0; i < alternate.size(); i++) {
					Symbol symbol = alternate.get(i);
					if(symbol instanceof Nonterminal) {
						predictionSet.addAll(firstSets.get(symbol));
						if(!firstSets.get(symbol).contains(Epsilon.getInstance())) {
							break;
						}
					} 
					else if (symbol instanceof RegularExpression) {
						RegularExpression regex = (RegularExpression) symbol;
						predictionSet.add(regex);
						if(!regex.isNullable()) {
							break;
						}
					}
				}
				
				if(isChainNullable(alternate, 0, firstSets)) {
					predictionSet.addAll(followSets.get(nonterminal));
				}
				predictionSet.remove(Epsilon.getInstance());
				list.add(predictionSet);

			}

			predictionSets.put(nonterminal, list);
		}
		
		return predictionSets;
	}
	
	public static List<BodyGrammarSlot> setSlotIds(Iterable<HeadGrammarSlot> nonterminals, Iterable<BodyGrammarSlot> conditionSlots) {
		
		List<BodyGrammarSlot> slots = new ArrayList<>();
		
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
		
		return slots;
	}
	
	public static Set<Nonterminal> calculateLLNonterminals(Map<Nonterminal, Set<List<Symbol>>> definitions, 
											   Map<Nonterminal, Set<RegularExpression>> firstSets,
											   Map<Nonterminal, Set<RegularExpression>> followSets,
									   		   Map<Nonterminal, Set<Nonterminal>> reachabilityGraph) {

		Set<Nonterminal> nonterminals = definitions.keySet();
		
		Set<Nonterminal> ll1Nonterminals = new HashSet<>();
		
		Set<Nonterminal> ll1SubGrammarNonterminals = new HashSet<>();
		
		// Calculating character level predictions
		Map<Tuple<Nonterminal, Integer>, Set<Integer>> predictions = new HashMap<>();
		
		for (Nonterminal head : nonterminals) {

			int alternateIndex = 0;
			for(List<Symbol> alt : definitions.get(head)) {
			
				// Calculate the prediction set for the alternate
				Set<RegularExpression> s = new HashSet<>();
				addFirstSet(s, alt, 0, firstSets);
				if(s.contains(Epsilon.getInstance())) {
					s.addAll(followSets.get(head));
				}

				// Expand ranges into integers
				Set<Integer> set = new HashSet<>();
				for(RegularExpression r : s) {
					set.addAll(convert(r.getFirstSet()));
				}
				
				predictions.put(Tuple.of(head, alternateIndex), set);
				
				alternateIndex++;
			}			
		}
		
		for (Nonterminal head : nonterminals) {
			if(isLL1(head, predictions, definitions)) {
				ll1Nonterminals.add(head);
			}
		}
		
		for (Nonterminal head : nonterminals) {
			if(ll1Nonterminals.contains(head)) {
				boolean ll1SubGrammar = true;
				for(Nonterminal reachableHead : reachabilityGraph.get(head)) {
					if(!ll1Nonterminals.contains(reachableHead)) {
						ll1SubGrammar = false;
					}
				}
				if(ll1SubGrammar) {
					ll1SubGrammarNonterminals.add(head);
				}
			}
		}
		
		return ll1SubGrammarNonterminals;
	}
	
	/**
	 * Converts a 
	 * @param set
	 * @return
	 */
	private static Set<Integer> convert(Set<Range> set) {
		Set<Integer> integerSet = new HashSet<>();
		for(Range range : set) {
			for(int i = range.getStart(); i < range.getEnd(); i++) {
				integerSet.add(i);
			}
		}
		return integerSet;
	}
	
    private static boolean isLL1(Nonterminal nonterminal, Map<Tuple<Nonterminal, Integer>, Set<Integer>> predictions,
    							 Map<Nonterminal, Set<List<Symbol>>> definitions) {
    	
    	int size = definitions.get(nonterminal).size();
    	
    	// If there is only one alternate
		if(size == 1) {
        	return true;
        }
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
            	if(i != j) {
            		HashSet<Integer> intersection = new HashSet<>(predictions.get(Tuple.of(nonterminal, i)));
            		intersection.retainAll(predictions.get(Tuple.of(nonterminal, j)));
        			if(!intersection.isEmpty()) {
        				return false;
                    }
            	}
            }
        }

        return true;
    }
		
	/**
	 * 
	 * Calculate the set of nonterminals that are reachable via the alternates of A.
	 * In other words, if A is a nonterminal, reachable nonterminals are all the B's such as
	 * A =>* alpha B gamma. Note that this method does not calculate direct-nullable reachable
	 * nonterminals.
	 * 
	 */
	public static Map<Nonterminal, Set<Nonterminal>> calculateReachabilityGraph(Map<Nonterminal, Set<List<Symbol>>> definitions) {
		
		Set<Nonterminal> nonterminals = definitions.keySet();
		
		Map<Nonterminal, Set<Nonterminal>> reachabilityGraph = new HashMap<>();
		
		for (Nonterminal head : nonterminals) {
			reachabilityGraph.put(head, new HashSet<Nonterminal>());
		}
		
		boolean changed = true;
		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {
				Set<Nonterminal> set = reachabilityGraph.get(head);
				for (List<Symbol> alternate : definitions.get(head)) {
					
					if(alternate == null) {
						continue;
					}
					
					for(Symbol symbol : alternate) {
						if(symbol instanceof Nonterminal) {
							Nonterminal nonterminal = (Nonterminal) symbol;
							changed |= set.add(nonterminal);
							changed |= set.addAll(reachabilityGraph.get(nonterminal));
						} 
					}
				}
			}
		}
		
		return reachabilityGraph;
	}
	
	public static Map<HeadGrammarSlot, Set<HeadGrammarSlot>> calculateDirectReachabilityGraph(Iterable<HeadGrammarSlot> nonterminals, 
																							  Map<Nonterminal, Set<RegularExpression>> firstSets) {
		
		Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph = new HashMap<>();
		
		boolean changed = true;

		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : nonterminals) {

				Set<HeadGrammarSlot> set = reachabilityGraph.get(head);
				if(set == null) {
					set = new HashSet<>();
					reachabilityGraph.put(head, set);
				}
				
				for (Alternate alternate : head.getAlternates()) {
					changed |= calculateDirectReachabilityGraph(set, alternate.getFirstSlot(), changed, reachabilityGraph, firstSets);
				}
			}
		}
		
		return reachabilityGraph;
	}

	/**
	 * 
	 * Calculates the set of nonterminals that are directly reachable from a given nonterminal.
	 * In other words, if A is a nonterminal, direct reachable nonterminals from A are the set of B's 
	 * such as A =>* B gamma.
	 * 
	 * @param set
	 * @param currentSlot
	 * @param changed
	 * @param reachabilityGraph
	 * @return
	 */
	private static boolean calculateDirectReachabilityGraph(Set<HeadGrammarSlot> set, BodyGrammarSlot currentSlot, boolean changed, 
													  Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph, 
													  Map<Nonterminal, Set<RegularExpression>> firstSets) {

		if (currentSlot instanceof EpsilonGrammarSlot) {
			return false;
		}
		
		else if (currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			
			changed = set.add(nonterminalGrammarSlot.getNonterminal()) || changed;
			
			Set<HeadGrammarSlot> set2 = reachabilityGraph.get(nonterminalGrammarSlot.getNonterminal());
			if(set2 == null) {
				set2 = new HashSet<>();
				reachabilityGraph.put(nonterminalGrammarSlot.getNonterminal(), set2);
			}
			
			changed = set.addAll(set2) || changed;
			
			if (isNullable(nonterminalGrammarSlot.getNonterminal().getNonterminal(), firstSets)) {
				return calculateDirectReachabilityGraph(set, currentSlot.next(), changed, reachabilityGraph, firstSets) || changed;
			}
			return changed;
		}
		
		// TODO: check the effect of adding TokenGrammarSlot
		// ignore LastGrammarSlot
		else {
			return changed;
		}	
	}
	
	public static void setPredictionSetsForConditionals(Iterable<BodyGrammarSlot> conditionSlots) {
		// TODO: rewrite this.
//		for(BodyGrammarSlot slot : conditionSlots) {
//			while(slot != null) {
//				if(slot instanceof NonterminalGrammarSlot || slot instanceof TokenGrammarSlot) {
//					BitSet set = new BitSet();
//					getChainFirstSet(slot, set);
//					// TODO: fix and uncomment it later
////					if(isChainNullable(slot)) {
////						set.addAll(head.getFollowSet());
////					}
//					slot.setPredictionSet(set);
//				} 
//				else if(slot instanceof LastGrammarSlot) {
//					// TODO: fix and uncomment it later
//					//slot.setPredictionSet(slot.getHead().getFollowSetAsBitSet());
//				}
//				else {
//					throw new RuntimeException("Unexpected grammar slot: " + slot.getClass());
//				}
//				slot = slot.next();
//			}
//		}
	}

}
