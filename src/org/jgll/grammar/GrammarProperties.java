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
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;

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
	
	private static boolean isChainNullable(BodyGrammarSlot slot, Map<Nonterminal, Set<RegularExpression>> firstSets) {
		if (!(slot instanceof LastGrammarSlot)) {
			if(slot instanceof TokenGrammarSlot) {
				return slot.isNullable() && isChainNullable(slot.next(), firstSets);
			}
			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return isNullable(ntGrammarSlot.getNonterminal().getNonterminal(), firstSets) && isChainNullable(ntGrammarSlot.next(), firstSets);
		}

		return true;
	}
	
	private static void getChainFirstSet(BodyGrammarSlot slot, 
										 Set<RegularExpression> set, 
										 Map<Nonterminal, Set<RegularExpression>> firstSets) {
		
		if (!(slot instanceof LastGrammarSlot)) {
			
			if(slot instanceof TokenGrammarSlot) {
				set.add(((TokenGrammarSlot) slot).getSymbol());
				if(slot.isNullable()) {
					getChainFirstSet(slot.next(), set, firstSets);
				}
			}
			else {
				NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
				set.addAll(firstSets.get(ntGrammarSlot.getNonterminal().getNonterminal()));
				
				if(isNullable(ntGrammarSlot.getNonterminal().getNonterminal(), firstSets)) {
					getChainFirstSet(ntGrammarSlot.next(), set, firstSets);
				}
			}
		}
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
					
					if(alternate.size() == 0) {
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
	
	public static void setNullableHeads(Iterable<HeadGrammarSlot> nonterminals,
										Map<Nonterminal, Set<RegularExpression>> firstSets) {
		for (HeadGrammarSlot head : nonterminals) {
			head.setNullable(firstSets.get(head.getNonterminal()).contains(Epsilon.getInstance()));
		}
	}
	
	public static void setPredictionSets(Iterable<HeadGrammarSlot> nonterminals,
										 Map<Nonterminal, Set<RegularExpression>> firstSets,
										 Map<Nonterminal, Set<RegularExpression>> followSets) {
		
		for (HeadGrammarSlot head : nonterminals) {
			
			for (Alternate alternate : head.getAlternates()) {
				
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
					if(currentSlot instanceof NonterminalGrammarSlot ||
					   currentSlot instanceof TokenGrammarSlot) {
						Set<RegularExpression> set = new HashSet<>();
						getChainFirstSet(currentSlot, set, firstSets);
						if(isChainNullable(currentSlot, firstSets)) {
							set.addAll(followSets.get(head.getNonterminal()));
						}
						// Prediction sets should not contain epsilon
						set.remove(Epsilon.getInstance());
						alternate.setPredictionSet(set);
					} 
					else if(currentSlot instanceof LastGrammarSlot) {
						Set<RegularExpression> set = followSets.get(currentSlot.getHead().getNonterminal());
						set.remove(Epsilon.getInstance());
						alternate.setPredictionSet(set);
					} 
					else {
						throw new RuntimeException("Unexpected grammar slot of type " + currentSlot.getClass());
					}
			}
			
			head.setPredictionSet();
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
									   Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph) {

		// Calculating character level predictions
		Map<Alternate, Set<Integer>> predictions = new HashMap<>();
		for (HeadGrammarSlot head : nonterminals) {
			for(Alternate alt : head.getAlternates()) {
				Set<Integer> set = new HashSet<>();
				for(RegularExpression r : alt.getPredictionSet()) {
					set.addAll(convert(r.getFirstSet()));
				}
				predictions.put(alt, set);
			}
		}
		
		for (HeadGrammarSlot head : nonterminals) {
			head.setLL1(isLL1(head, predictions));
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
	
	/**
	 * Converts a 
	 * @param set
	 * @return
	 */
	private static Set<Integer> convert(Set<Range> set) {
		return null;
	}
	
    private static boolean isLL1(HeadGrammarSlot nonterminal, Map<Alternate, Set<Integer>> predictions) {
        if(nonterminal.getAlternates().size() == 1) {
        	return true;
        }
        
        for(Alternate alt1 : nonterminal.getAlternates()) {
        	for(Alternate alt2 : nonterminal.getAlternates()) {
        		if(!alt1.equals(alt2)) {
        			HashSet<Integer> intersection = new HashSet<>(predictions.get(alt1));
					intersection.retainAll(predictions.get(alt2));
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
				}
			}
		}
		
		return reachabilityGraph;
	}
	
	private static boolean calculateReachabilityGraph(List<Symbol> list, 
												  	  Set<Nonterminal> set, 
												  	  Map<Nonterminal, Set<Nonterminal>> reachabilityGraph) {
		
		boolean changed = false;
		
		for(Symbol symbol : list) {
			if(symbol instanceof Nonterminal) {
				Nonterminal nonterminal = (Nonterminal) symbol;
				changed |= set.add(nonterminal);
				
				Set<Nonterminal> set2 = reachabilityGraph.get(nonterminal);
				if(set2 == null) {
					set2 = new HashSet<>();
				}
				
				changed |= set.addAll(set2);				
			} 
		}
		
		return changed;

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
