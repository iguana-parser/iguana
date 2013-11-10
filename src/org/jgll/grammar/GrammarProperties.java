package org.jgll.grammar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.grammaraction.LongestTerminalChainAction;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.RegularExpressionGrammarSlot;
import org.jgll.grammar.slot.RegularListGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.RegularList;
import org.jgll.grammar.symbol.Terminal;


public class GrammarProperties {
	
	
	public static void calculateFirstSets(Iterable<HeadGrammarSlot> nonterminals) {
		boolean changed = true;

		while (changed) {
			changed = false;
			for (HeadGrammarSlot head : nonterminals) {

				for (Alternate alternate : head.getAlternates()) {
					changed |= addFirstSet(head.getFirstSet(), alternate.getFirstSlot(), changed);
					if(isChainNullable(alternate.getFirstSlot())) {
						changed |= head.getFirstSet().add(Epsilon.getInstance());
					}
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
	private static boolean addFirstSet(Set<Terminal> set, BodyGrammarSlot currentSlot, boolean changed) {

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
			
			changed = set.addAll(nonterminalGrammarSlot.getNonterminal().getFirstSetWithoutEpsilon()) || changed;
			if (isNullable(nonterminalGrammarSlot.getNonterminal())) {
				return addFirstSet(set, currentSlot.next(), changed) || changed;
			}
			return changed;
		}
		
		else if(currentSlot instanceof RegularListGrammarSlot) {
			RegularList regularList = ((RegularListGrammarSlot) currentSlot).getRegularList();
			// Star regular lists are always nullable.
			if(regularList.getMinimum() == 0) {
				changed = set.add(Epsilon.getInstance()) || changed;
			}
			return set.add(regularList.getCharacterClass()) || changed;
		}
		
		else if(currentSlot instanceof RegularExpressionGrammarSlot) {
			RegularExpression regexp = ((RegularExpressionGrammarSlot) currentSlot).getSymbol();
			if(currentSlot.isNullable()) {
				changed = set.add(Epsilon.getInstance()) || changed;
			}
			
			return set.addAll(regexp.getFirstTerminal()) || changed;
		}

		// ignore LastGrammarSlot
		else {
			return changed;
		}
	}
	
	private static boolean isNullable(HeadGrammarSlot nt) {
		return nt.getFirstSet().contains(Epsilon.getInstance());
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
			if (slot instanceof TerminalGrammarSlot || slot instanceof KeywordGrammarSlot) {
				return false;
			}
			
			else if(slot instanceof RegularListGrammarSlot ||
					slot instanceof RegularExpressionGrammarSlot) {
				return slot.isNullable() && isChainNullable(slot.next());
			}

			NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
			return isNullable(ntGrammarSlot.getNonterminal()) && isChainNullable(ntGrammarSlot.next());
		}

		return true;
	}
	
	private static void getChainFirstSet(BodyGrammarSlot slot, Set<Terminal> set) {
		
		if (!(slot instanceof LastGrammarSlot)) {
			
			if (slot instanceof TerminalGrammarSlot) {
				set.add(((TerminalGrammarSlot) slot).getTerminal());
			}
			
			else if(slot instanceof KeywordGrammarSlot) {
				set.add(((KeywordGrammarSlot) slot).getFirstTerminal());
			}
			
			else if(slot instanceof RegularListGrammarSlot) {
				set.add(((RegularListGrammarSlot) slot).getRegularList().getCharacterClass());
				if(slot.isNullable()) {
					getChainFirstSet(slot.next(), set);
				}
			}
			
			else if(slot instanceof RegularExpressionGrammarSlot) {
				set.addAll(((RegularExpressionGrammarSlot) slot).getFirstSet());
				if(slot.isNullable()) {
					getChainFirstSet(slot.next(), set);
				}				
			}
			
			else {
				NonterminalGrammarSlot ntGrammarSlot = (NonterminalGrammarSlot) slot;
				set.addAll(ntGrammarSlot.getNonterminal().getFirstSet());
				
				if(ntGrammarSlot.isNullable()) {
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
	
	public static void setHeadPredictionSets(Iterable<HeadGrammarSlot> nonterminals) {
		for (HeadGrammarSlot head : nonterminals) {
			head.setPredictionSet();
			
			boolean nullable = head.getFirstSet().contains(Epsilon.getInstance());
			boolean directNullable = false;
			
			if(nullable) {
				for(Alternate alt : head.getAlternates()) {
					if(alt.isEpsilon()) {
						directNullable = true;
						head.setEpsilonAlternate(alt);
						break;
					}
				}
			}
			head.setNullable(nullable, directNullable);
		}
	}
	
	public static void setPredictionSets(Iterable<HeadGrammarSlot> nonterminals) {
		for (HeadGrammarSlot head : nonterminals) {
			for (Alternate alternate : head.getAlternates()) {
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				
				while(currentSlot != null) {
					if(currentSlot instanceof TerminalGrammarSlot) {
						currentSlot.setPredictionSet(((TerminalGrammarSlot) currentSlot).getTerminal().asBitSet());					
					} 
					else if(currentSlot instanceof KeywordGrammarSlot) {
						currentSlot.setPredictionSet(((KeywordGrammarSlot) currentSlot).getKeyword().getFirstTerminal().asBitSet());
					} 
					else if(currentSlot instanceof NonterminalGrammarSlot ||
							currentSlot instanceof RegularListGrammarSlot ||
							currentSlot instanceof RegularExpressionGrammarSlot) {
						Set<Terminal> set = new HashSet<>();
						getChainFirstSet(currentSlot, set);
						if(isChainNullable(currentSlot)) {
							set.addAll(head.getFollowSet());
						}
						currentSlot.setPredictionSet(getBitSet(set));
					} 
					else if(currentSlot instanceof LastGrammarSlot) {
						currentSlot.setPredictionSet(currentSlot.getHead().getFollowSetAsBitSet());
					} 
					else {
						System.out.println(currentSlot.getClass());
						throw new RuntimeException("Unexpected grammar slot.");
					}
					currentSlot = currentSlot.next();
				}
			}
		}
	}

	public static BitSet getBitSet(Set<Terminal> set) {
		BitSet bitSet = new BitSet();
		for(Terminal t : set) {
			bitSet.or(t.asBitSet());
		}
		return bitSet;
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
		
		for (HeadGrammarSlot head : nonterminals) {
			head.setLL1();
		}
		
		for (HeadGrammarSlot head : nonterminals) {
			boolean ll1subGrammar = true;
			if(head.isLL1()) {
				for(HeadGrammarSlot reachableHead : reachabilityGraph.get(head)) {
					if(!reachableHead.isLL1()) {
						ll1subGrammar = false;
					}
				}
			} else {
				ll1subGrammar = false;
			}
			
			head.setSubGrammarLL1(ll1subGrammar);
		}
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
		
		for (HeadGrammarSlot head : nonterminals) {
			Set<HeadGrammarSlot> set = reachabilityGraph.get(head);
			if(set == null) {
				set = new HashSet<>();
				reachabilityGraph.put(head, set);
			}

			boolean changed = true;

			while (changed) {
				changed = false;
				
				for (Alternate alternate : head.getAlternates()) {
					BodyGrammarSlot currentSlot = alternate.getFirstSlot();
					while(!(currentSlot instanceof LastGrammarSlot)) {
						
						if(currentSlot instanceof NonterminalGrammarSlot) {
							HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
							changed |= set.add(nonterminal);
							Set<HeadGrammarSlot> reachableNonterminals = reachabilityGraph.get(nonterminal);
							if(reachableNonterminals != null) {
								changed |= set.addAll(reachabilityGraph.get(nonterminal));
							}
							
						}
						
						currentSlot = currentSlot.next();
					}
				}
			}
		}
		
		return reachabilityGraph;
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

		else if (currentSlot instanceof TerminalGrammarSlot) {
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

		// ignore LastGrammarSlot
		else {
			return changed;
		}	
	}

	
//	private void setDirectNullables() {
//		
//		for (HeadGrammarSlot head : nonterminals) {
//						
//			for (Alternate alternate : head.getAlternates()) {
//
//				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
//
//				while (!(currentSlot instanceof LastGrammarSlot)) {
//					if (currentSlot instanceof NonterminalGrammarSlot) {
//						// Replaces a nonterminal with an instance of direct nullable nonterminal.
//						NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) currentSlot;
//						if(ntSlot.getNonterminal().isDirectNullable()) {
//							NonterminalGrammarSlot directNullableSlot = 
//									new DirectNullableNonterminalGrammarSlot(ntSlot.getPosition(), ntSlot.previous(), ntSlot.getNonterminal(), ntSlot.getHead());
//							ntSlot.next().setPrevious(directNullableSlot);
//							directNullableSlot.setNext(ntSlot.next());
//							directNullableSlot.setId(ntSlot.getId());
//							directNullableSlot.setPredictionSet(ntSlot.getPredictionSet());
//							directNullableSlot.setLabel(ntSlot.toString());
//							slots.remove(ntSlot);
//							slots.add(directNullableSlot);
//							copyActions(ntSlot, directNullableSlot);
//						}
//					}
//					currentSlot = currentSlot.next();
//				}
//			}
//		}
//	}
	
	/**
	 * Calculates the length of the longest chain of terminals in a body of production rules.
	 */
	public static int calculateLongestTerminalChain(Iterable<HeadGrammarSlot> nonterminals) {
		LongestTerminalChainAction action = new LongestTerminalChainAction();
		GrammarVisitor.visit(nonterminals, action);
		return action.getLongestTerminalChain();
	}
	
	public static void setPredictionSetsForConditionals(Iterable<BodyGrammarSlot> conditionSlots) {
		for(BodyGrammarSlot slot : conditionSlots) {
			while(slot != null) {
				if(slot instanceof TerminalGrammarSlot) {
					slot.setPredictionSet(((TerminalGrammarSlot) slot).getTerminal().asBitSet());					
				} 
				else if(slot instanceof KeywordGrammarSlot) {
					slot.setPredictionSet(((KeywordGrammarSlot) slot).getKeyword().getFirstTerminal().asBitSet());
				} 
				else if(slot instanceof NonterminalGrammarSlot) {
					Set<Terminal> set = new HashSet<>();
					getChainFirstSet(slot, set);
					// TODO: fix and uncomment it later
//					if(isChainNullable(slot)) {
//						set.addAll(head.getFollowSet());
//					}
					slot.setPredictionSet(getBitSet(set));
				} 
				else if(slot instanceof LastGrammarSlot) {
					// TODO: fix and uncomment it later
					//slot.setPredictionSet(slot.getHead().getFollowSetAsBitSet());
				}
				else {
					System.out.println(slot.getClass());
					throw new RuntimeException("Unexpected grammar slot.");
				}
				slot = slot.next();
			}
		}
	}

}
