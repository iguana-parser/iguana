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
import org.jgll.regex.Automaton;
import org.jgll.regex.AutomatonOperations;
import org.jgll.regex.RegularExpression;

public class GrammarProperties {
	
	private static final int EPSILON = 0;
	private static final int EOF = 1;

	public static void calculateFirstSets(Iterable<HeadGrammarSlot> nonterminals) {
		boolean changed = true;

		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : nonterminals) {

				for (Alternate alternate : head.getAlternates()) {
					BitSet firstSet = head.getFirstSet();
					BitSet copy = copy(firstSet);
					addFirstSet(firstSet, alternate.getFirstSlot());
					changed |= !copy.equals(firstSet);
				}
			}
		}
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
	private static void addFirstSet(BitSet firstSet, BodyGrammarSlot currentSlot) {

		if (currentSlot instanceof EpsilonGrammarSlot) {
			firstSet.set(EPSILON);
		}

		else if (currentSlot instanceof TokenGrammarSlot) {
			firstSet.set(((TokenGrammarSlot) currentSlot).getTokenID());
			if(currentSlot.isNullable()) {
				addFirstSet(firstSet, currentSlot.next());
			}
		}
		// Nonterminal
		else if (currentSlot instanceof NonterminalGrammarSlot) {
			NonterminalGrammarSlot nonterminalGrammarSlot = (NonterminalGrammarSlot) currentSlot;
			
			firstSet.or(nonterminalGrammarSlot.getNonterminal().getFirstSetWithoutEpsilon());
			if (isNullable(nonterminalGrammarSlot.getNonterminal())) {
				addFirstSet(firstSet, currentSlot.next());
			}
		}
		
		if (isChainNullable(currentSlot)) {
			firstSet.set(0);
		}
	}
	
	private static boolean isNullable(HeadGrammarSlot nt) {
		return nt.getFirstSet().get(0);
	}
	
	/**
	 * 
	 * Checks if a grammar slot is nullable. This check is performed until
	 * the end of the alternate: isChainNullable(X ::= alpha . beta) says if the
	 * part beta is nullable.
	 *   
	 */
	private static boolean isChainNullable(BodyGrammarSlot slot) {
		if (!(slot instanceof LastGrammarSlot)) {
			if(slot instanceof TokenGrammarSlot) {
				return slot.isNullable() && isChainNullable(slot.next());
			}
			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return isNullable(ntGrammarSlot.getNonterminal()) && isChainNullable(ntGrammarSlot.next());
		}

		return true;
	}
	
	private static void getChainFirstSet(BodyGrammarSlot slot, BitSet set) {
		
		if (!(slot instanceof LastGrammarSlot)) {
			
			if(slot instanceof TokenGrammarSlot) {
				set.set(((TokenGrammarSlot) slot).getTokenID());
				if(slot.isNullable()) {
					getChainFirstSet(slot.next(), set);
				}
			}
			else {
				NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
				set.or(ntGrammarSlot.getNonterminal().getFirstSet());
				
				if(isNullable(ntGrammarSlot.getNonterminal())) {
					getChainFirstSet(ntGrammarSlot.next(), set);
				}
			}
		}
	}
	
	public static void calculateFollowSets(Iterable<HeadGrammarSlot> nonterminals) {
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
								BitSet followSet = nonterminalGrammarSlot.getNonterminal().getFollowSet();
								BitSet copy = copy(followSet);
								followSet.or(head.getFollowSet());
								changed |= !followSet.equals(copy);
								break;
							}

							// For rules of the form X ::= alpha B beta, add the
							// first set of beta to
							// the follow set of B.
							BitSet followSet = nonterminalGrammarSlot.getNonterminal().getFollowSet();
							BitSet copy = copy(followSet);
							addFirstSet(followSet, currentSlot.next());
							changed |= !followSet.equals(copy);

							// If beta is nullable, then add the follow set of X
							// to the follow set of B.
							if (isChainNullable(next)) {
								copy = copy(followSet);
								followSet.or(head.getFollowSet());
								changed |= !followSet.equals(copy);
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
			head.getFollowSet().clear(EPSILON);

			// Add the EOF to all nonterminals as each nonterminal can be used
			// as the start symbol.
			head.getFollowSet().set(EOF);
		}
	}
	
	public static void setNullableHeads(Iterable<HeadGrammarSlot> nonterminals) {
		for (HeadGrammarSlot head : nonterminals) {
			head.setNullable(head.getFirstSet().get(EPSILON));
		}
	}
	
	public static void setPredictionSets(Iterable<HeadGrammarSlot> nonterminals, 
										 Automaton a, 
										 List<RegularExpression> regularExpressions) {
		
		for (HeadGrammarSlot head : nonterminals) {
			
			for (Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
					if(currentSlot instanceof NonterminalGrammarSlot ||
					   currentSlot instanceof TokenGrammarSlot) {
						BitSet set = new BitSet();
						getChainFirstSet(currentSlot, set);
						if(isChainNullable(currentSlot)) {
							set.or(head.getFollowSet());
						}
						// Prediction sets should not contain epsilon
						set.clear(EPSILON);
						alternate.setPredictionSet(set);
					} 
					else if(currentSlot instanceof LastGrammarSlot) {
						BitSet set = currentSlot.getHead().getFollowSetAsBitSet();
						set.clear(EPSILON);
						alternate.setPredictionSet(set);
					} 
					else {
						throw new RuntimeException("Unexpected grammar slot of type " + currentSlot.getClass());
					}
			}
			
			BitSet predictionSet = new BitSet();
			predictionSet.or(head.getFirstSet());
			if(head.isNullable()) {
				predictionSet.or(head.getFollowSet());
			}
			predictionSet.clear(EPSILON);
			head.setPredictionSet(a, regularExpressions);
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
			boolean ll1 = isLL1(head, tokens);
			if(ll1) {
				head.setLL1(ll1);
				head.setLL1Map(tokens.size());
			}
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
        
        Automaton[] automatonMap = new Automaton[tokens.size()];
        for(int i = 0; i < tokens.size(); i++) {
                automatonMap[i] = tokens.get(i).toAutomaton().minimize();
        }
        
        for(Alternate alt1 : nonterminal.getAlternates()) {
                for(Alternate alt2 : nonterminal.getAlternates()) {
                        if(!alt1.equals(alt2)) {
                                BitSet set1 = alt1.getPredictionSet();
                                BitSet set2 = alt2.getPredictionSet();
                                
                                 for (int i = set1.nextSetBit(0); i >= 0; i = set1.nextSetBit(i+1)) {
                                         for (int j = set2.nextSetBit(0); j >= 0; j = set2.nextSetBit(j+1)) {
                                                 
                                                 // the automaton for EOF is a subset of any dfa, so skip it.
                                                 if(i == EOF || j == EOF) {
                                                         continue;
                                                 }
                                                 
                                                 if(i != j) {
                                                         if(AutomatonOperations.prefix(automatonMap[i], automatonMap[j]) ||
                                                                AutomatonOperations.prefix(automatonMap[j], automatonMap[i])) {
                                                                 return false;
                                                         }
                                                 }
                                         }
                                 }
                        }
                }
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
        			if(alt1.getPredictionSet().intersects(alt2.getPredictionSet())) {
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
	
	public static Map<HeadGrammarSlot, Set<HeadGrammarSlot>> calculateDirectReachabilityGraph(Iterable<HeadGrammarSlot> nonterminals) {
		
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
					changed |= calculateDirectReachabilityGraph(set, alternate.getFirstSlot(), changed, reachabilityGraph);
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
													  Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph) {

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
			
			if (isNullable(nonterminalGrammarSlot.getNonterminal())) {
				return calculateDirectReachabilityGraph(set, currentSlot.next(), changed, reachabilityGraph) || changed;
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
