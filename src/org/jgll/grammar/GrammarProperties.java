package org.jgll.grammar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.regex.Automaton;
import org.jgll.regex.AutomatonOperations;
import org.jgll.regex.RegularExpression;

public class GrammarProperties {
	
	public static Map<HeadGrammarSlot, Set<Integer>> calculateFirstSets(Iterable<HeadGrammarSlot> nonterminals) {
		
		Map<HeadGrammarSlot, Set<Integer>> firstSets = new HashMap<>();
		
		for (HeadGrammarSlot head : nonterminals) {
			firstSets.put(head, new HashSet<Integer>());
		}
		
		boolean changed = true;

		while (changed) {
			
			changed = false;
			
			for (HeadGrammarSlot head : nonterminals) {
				for (Alternate alternate : head.getAlternates()) {
					Set<Integer> firstSet = firstSets.get(head);
					changed |= addFirstSet(firstSet, alternate.getFirstSlot(), firstSets);
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
	private static boolean addFirstSet(Set<Integer> firstSet, BodyGrammarSlot currentSlot, Map<HeadGrammarSlot, Set<Integer>> firstSets) {

		boolean changed = false;
		
		if (currentSlot instanceof EpsilonGrammarSlot) {
			changed = firstSet.add(Epsilon.TOKEN_ID);
		}

		else if (currentSlot instanceof TokenGrammarSlot) {
			changed = firstSet.add(((TokenGrammarSlot) currentSlot).getTokenID());
			if(currentSlot.isNullable()) {
				changed |= addFirstSet(firstSet, currentSlot.next(), firstSets);
			}
		}
		
		// Nonterminal
		else if (currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			
			Set<Integer> set = new HashSet<>(firstSets.get(nonterminalGrammarSlot.getNonterminal()));
			set.remove(Epsilon.TOKEN_ID);
			changed = firstSet.addAll(set);
			if (isNullable(nonterminalGrammarSlot.getNonterminal(), firstSets)) {
				changed |= addFirstSet(firstSet, currentSlot.next(), firstSets);
			}
		}
		
		if (isChainNullable(currentSlot, firstSets)) {
			changed |= firstSet.add(Epsilon.TOKEN_ID);
		}
		
		return changed;
	}
	
	private static boolean isNullable(HeadGrammarSlot nt, Map<HeadGrammarSlot, Set<Integer>> firstSets) {
		return firstSets.get(nt).contains(Epsilon.TOKEN_ID);
	}
	
	/**
	 * 
	 * Checks if a grammar slot is nullable. This check is performed until
	 * the end of the alternate: isChainNullable(X ::= alpha . beta) says if the
	 * part beta is nullable.
	 *   
	 */
	private static boolean isChainNullable(BodyGrammarSlot slot, Map<HeadGrammarSlot, Set<Integer>> firstSets) {
		if (!(slot instanceof LastGrammarSlot)) {
			if(slot instanceof TokenGrammarSlot) {
				return slot.isNullable() && isChainNullable(slot.next(), firstSets);
			}
			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return isNullable(ntGrammarSlot.getNonterminal(), firstSets) && isChainNullable(ntGrammarSlot.next(), firstSets);
		}

		return true;
	}
	
	private static void getChainFirstSet(BodyGrammarSlot slot, Set<Integer> set, Map<HeadGrammarSlot, Set<Integer>> firstSets) {
		
		if (!(slot instanceof LastGrammarSlot)) {
			
			if(slot instanceof TokenGrammarSlot) {
				set.add(((TokenGrammarSlot) slot).getTokenID());
				if(slot.isNullable()) {
					getChainFirstSet(slot.next(), set, firstSets);
				}
			}
			else {
				NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
				set.addAll(firstSets.get(ntGrammarSlot.getNonterminal()));
				
				if(isNullable(ntGrammarSlot.getNonterminal(), firstSets)) {
					getChainFirstSet(ntGrammarSlot.next(), set, firstSets);
				}
			}
		}
	}
	
	public static Map<HeadGrammarSlot, Set<Integer>> calculateFollowSets(Iterable<HeadGrammarSlot> nonterminals, 
																		 Map<HeadGrammarSlot, Set<Integer>> firstSets) {
		
		Map<HeadGrammarSlot, Set<Integer>> followSets = new HashMap<>();
		
		for (HeadGrammarSlot head : nonterminals) {
			followSets.put(head, new HashSet<Integer>());
		}
		
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
								Set<Integer> followSet = followSets.get(nonterminalGrammarSlot.getNonterminal());
								changed |= followSet.addAll(followSets.get(head));
								break;
							}

							// For rules of the form X ::= alpha B beta, add the
							// first set of beta to
							// the follow set of B.
							Set<Integer> followSet = followSets.get(nonterminalGrammarSlot.getNonterminal());
							changed |= addFirstSet(followSet, currentSlot.next(), firstSets);

							// If beta is nullable, then add the follow set of X
							// to the follow set of B.
							if (isChainNullable(next, firstSets)) {
								changed |= followSet.addAll(followSets.get(head));
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
			followSets.get(head).remove(Epsilon.TOKEN_ID);

			// Add the EOF to all nonterminals as each nonterminal can be used
			// as the start symbol.
			followSets.get(head).add(EOF.TOKEN_ID);
		}
		
		return followSets;
	}
	
	public static void setNullableHeads(Iterable<HeadGrammarSlot> nonterminals,
										Map<HeadGrammarSlot, Set<Integer>> firstSets) {
		for (HeadGrammarSlot head : nonterminals) {
			head.setNullable(firstSets.get(head).contains(Epsilon.TOKEN_ID));
		}
	}
	
	public static void setPredictionSets(Iterable<HeadGrammarSlot> nonterminals, 
										 List<RegularExpression> regularExpressions,
										 Map<HeadGrammarSlot, Set<Integer>> firstSets,
										 Map<HeadGrammarSlot, Set<Integer>> followSets) {
		
		for (HeadGrammarSlot head : nonterminals) {
			
			for (Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
					if(currentSlot instanceof NonterminalGrammarSlot ||
					   currentSlot instanceof TokenGrammarSlot) {
						Set<Integer> set = new HashSet<>();
						getChainFirstSet(currentSlot, set, firstSets);
						if(isChainNullable(currentSlot, firstSets)) {
							set.addAll(followSets.get(head));
						}
						// Prediction sets should not contain epsilon
						set.remove(Epsilon.TOKEN_ID);
						alternate.setPredictionSet(set);
					} 
					else if(currentSlot instanceof LastGrammarSlot) {
						Set<Integer> set = followSets.get(currentSlot.getHead());
						set.remove(Epsilon.TOKEN_ID);
						alternate.setPredictionSet(set);
					} 
					else {
						throw new RuntimeException("Unexpected grammar slot of type " + currentSlot.getClass());
					}
			}
			
			Set<Integer> predictionSet = new HashSet<>();
			predictionSet.addAll(firstSets.get(head));
			if(head.isNullable()) {
				predictionSet.addAll(followSets.get(head));
			}
			predictionSet.remove(Epsilon.TOKEN_ID);
			head.setPredictionSet(predictionSet, regularExpressions);
		}
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
	
	public static void setLLProperties(Iterable<HeadGrammarSlot> nonterminals, 
									   Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph, 
									   List<RegularExpression> tokens) {
		
		for (HeadGrammarSlot head : nonterminals) {
			head.setLL1(isLL1(head, tokens));
		}
		
		for (HeadGrammarSlot head : nonterminals) {
			if(head.isLL1()) {
				boolean condition = true;
				for(HeadGrammarSlot reachableHead : reachabilityGraph.get(head)) {
					if(!reachableHead.isLL1()) {
						condition = false;
					}
				}
				head.setLL1SubGrammar(condition);
			}
		}
		
	}
	
    private static boolean isLL1(HeadGrammarSlot nonterminal, List<RegularExpression> tokens) {
        if(!arePredictionSetsDistinct(nonterminal)) {
                return false;
        }        
        return true;
}
	
    private static boolean arePredictionSetsDistinct(HeadGrammarSlot nonterminal) {
        if(nonterminal.getAlternates().size() == 1) {
        	return true;
        }
        
        for(Alternate alt1 : nonterminal.getAlternates()) {
        	for(Alternate alt2 : nonterminal.getAlternates()) {
        		if(!alt1.equals(alt2)) {
        			Set<Integer> s1 = new HashSet<>(alt1.getPredictionSet());
        			Set<Integer> s2 = new HashSet<>(alt2.getPredictionSet());
        			
        			s1.retainAll(alt2.getPredictionSet());
        			s2.retainAll(alt1.getPredictionSet());
        			
        			if(s1.isEmpty() && s2.isEmpty()) {
        				return false;
                    }
        		}
        	}
        }

        return false;
    }
	
	/**
	 * 
	 * Calculate the set of nonterminals that are reachable via the alternates of A.
	 * In other words, if A is a nonterminal, reachable nonterminals are A =>* alpha B gamma
	 * 
	 * @param nonterminals
	 * @return
	 */
	public static Map<HeadGrammarSlot, Set<HeadGrammarSlot>> calculateReachabilityGraph(Iterable<HeadGrammarSlot> nonterminals) {
		
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
					changed |= calculateReachabilityGraph(alternate.getFirstSlot(), set, reachabilityGraph);
				}
			}
		}
		
		return reachabilityGraph;
	}
	
	private static boolean calculateReachabilityGraph(BodyGrammarSlot slot, 
												  	  Set<HeadGrammarSlot> set, 
												  	  Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph) {
		
		if(slot instanceof NonterminalGrammarSlot) {
			HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) slot).getNonterminal();
			boolean changed = false;
			changed |= set.add(nonterminal);
			
			Set<HeadGrammarSlot> set2 = reachabilityGraph.get(nonterminal);
			if(set2 == null) {
				set2 = new HashSet<>();
			}
			
			changed |= set.addAll(set2);
			changed |= calculateReachabilityGraph(slot.next(), set, reachabilityGraph);
			
			return changed;
		} 
		
		else if(slot instanceof LastGrammarSlot) {
			return false;
		}
		
		else {
			return calculateReachabilityGraph(slot.next(), set, reachabilityGraph);
		}
	}
	
	public static Map<HeadGrammarSlot, Set<HeadGrammarSlot>> calculateDirectReachabilityGraph(Iterable<HeadGrammarSlot> nonterminals, Map<HeadGrammarSlot, Set<Integer>> firstSets) {
		
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
													  Map<HeadGrammarSlot, Set<Integer>> firstSets) {

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
			
			if (isNullable(nonterminalGrammarSlot.getNonterminal(), firstSets)) {
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
	
	private static BitSet copy(BitSet bitSet) {
		BitSet copy = new BitSet();
		copy.or(bitSet);
		return copy;
	}
}
