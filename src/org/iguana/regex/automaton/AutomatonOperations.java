/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.regex.automaton;

import org.iguana.regex.CharRange;
import org.iguana.regex.CharacterRanges;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AutomatonOperations {
	
	public static Automaton makeDeterministic(Automaton automaton) {
		if (automaton.isDeterministic())
			return automaton;
		
		return makeDeterministic(automaton.getStartState(), automaton.getAlphabet());
	}

	public static Automaton makeDeterministic(org.iguana.regex.automaton.State start, org.iguana.regex.CharRange[] alphabet) {

		Set<Set<org.iguana.regex.automaton.State>> visitedStates = new HashSet<>();
		Deque<Set<org.iguana.regex.automaton.State>> processList = new ArrayDeque<>();
		
		Set<org.iguana.regex.automaton.State> initialState = new HashSet<>();
		initialState.add(start);
		initialState = epsilonClosure(initialState);
		visitedStates.add(initialState);
		processList.add(initialState);
		
		/*
		 * A map from the set of NFA states to the new state in the produced DFA.
		 * This map is used for sharing DFA states.
		 */
		Map<Set<org.iguana.regex.automaton.State>, org.iguana.regex.automaton.State> newStatesMap = new HashMap<>();
		
		org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();
		
		newStatesMap.put(initialState, startState);
		
		while (!processList.isEmpty()) {
			Set<org.iguana.regex.automaton.State> stateSet = processList.poll();

			for (org.iguana.regex.CharRange r : alphabet) {
				Set<org.iguana.regex.automaton.State> destState = move(stateSet, r);
				
				if (destState.isEmpty())
					continue;
				
				org.iguana.regex.automaton.State source = newStatesMap.get(stateSet);
				org.iguana.regex.automaton.State dest = newStatesMap.computeIfAbsent(destState, s -> {
					org.iguana.regex.automaton.State state = new org.iguana.regex.automaton.State();
					for (org.iguana.regex.automaton.State ds : destState) {
						state.addRegularExpressions(ds.getRegularExpressions());
					}
					return state;
				});
				source.addTransition(new Transition(r, dest));

				if (!visitedStates.contains(destState)) {
					visitedStates.add(destState);
					processList.add(destState);
				}
			}
		}
		
		// Setting the final states.
		outer:
		for (Entry<Set<org.iguana.regex.automaton.State>, org.iguana.regex.automaton.State> e : newStatesMap.entrySet()) {
			for (org.iguana.regex.automaton.State s : e.getKey()) {
				if (s.getStateType() == org.iguana.regex.automaton.StateType.FINAL) {
					e.getValue().setStateType(org.iguana.regex.automaton.StateType.FINAL);
					continue outer;
				}
			}			
		}

		return Automaton.builder(startState).setDeterministic(true).build();
	}
	
	public static Automaton union(Automaton a1, Automaton a2) {
		return op(a1, a2, (s1, s2) -> s1.isFinalState() || s2.isFinalState());
	}
	
	public static Automaton intersect(Automaton a1, Automaton a2) {
		return op(a1, a2, (s1, s2) -> s1.isFinalState() && s2.isFinalState());
	}
	
	public static Automaton difference(Automaton a1, Automaton a2) {
		return op(a1, a2, (s1, s2) -> s1.isFinalState() && !s2.isFinalState());
	}
	
	private static Automaton op(Automaton a1, Automaton a2, Op op) {
		a1 = makeDeterministic(a1);
		a2 = makeDeterministic(a2);
		
		Map<org.iguana.regex.CharRange, List<org.iguana.regex.CharRange>> rangeMap = merge(a1.getAlphabet(), a2.getAlphabet());
		convertToNonOverlapping(a1, rangeMap);
		convertToNonOverlapping(a2, rangeMap);
		
		Set<org.iguana.regex.CharRange> values = rangeMap.values().stream()
				                                      .flatMap(List::stream)
				                                      .collect(Collectors.toSet());
		
		a1 = makeComplete(a1, values);
		a2 = makeComplete(a2, values);

		return product(a1, a2, values, op);
	}
	
	/**
	 * Produces the Cartesian product of the states of an automata.
	 */
	private static Automaton product(Automaton a1, Automaton a2, Set<org.iguana.regex.CharRange> values, Op op) {
		
		org.iguana.regex.automaton.State[] states1 = a1.getStates();
		org.iguana.regex.automaton.State[] states2 = a2.getStates();
		
		org.iguana.regex.automaton.State[][] newStates = new org.iguana.regex.automaton.State[states1.length][states2.length];
		
		org.iguana.regex.automaton.State startState = null;

		for (int i = 0; i < states1.length; i++) {
			for (int j = 0; j < states2.length; j++) {
				
				org.iguana.regex.automaton.State state = getState(newStates, i, j);
				org.iguana.regex.automaton.State state1 = states1[i];
				org.iguana.regex.automaton.State state2 = states2[j];
				
				for (org.iguana.regex.CharRange r : values) {
					org.iguana.regex.automaton.State dest1 = state1.getState(r);
					org.iguana.regex.automaton.State dest2 = state2.getState(r);
					if (dest1 != null && dest2 != null) {
						org.iguana.regex.automaton.State dest = getState(newStates, dest1.getId(), dest2.getId());
						state.addTransition(new Transition(r, dest));
					}
				}
				
				if (op.execute(state1, state2)) {
					state.setStateType(org.iguana.regex.automaton.StateType.FINAL);
				}
				
				if (state1 == a1.getStartState() && state2 == a2.getStartState()) {
					startState = state;
				}
			}
		}
		
		return Automaton.builder(startState).build();
	}

	private static org.iguana.regex.automaton.State getState(org.iguana.regex.automaton.State[][] newStates, int i, int j) {
		org.iguana.regex.automaton.State state = newStates[i][j];
		if (state == null) {
			state = new org.iguana.regex.automaton.State();
			newStates[i][j] = state;
		}
		return state;
	}
	
	public static void convertToNonOverlapping(Automaton a, Map<org.iguana.regex.CharRange, List<org.iguana.regex.CharRange>> rangeMap) {
		for (org.iguana.regex.automaton.State state : a.getStates()) {
			List<Transition> removeList = new ArrayList<>();
			List<Transition> addList = new ArrayList<>();
			for (Transition transition : state.getTransitions()) {
				if (!transition.isEpsilonTransition()) {
					removeList.add(transition);
					for (org.iguana.regex.CharRange range : rangeMap.get(transition.getRange())) {
						addList.add(new Transition(range, transition.getDestination()));
					}					
				}
			}
			state.removeTransitions(removeList);
			state.addTransitions(addList);
		}
	}
	
	
	public static Automaton makeComplete(Automaton automaton, Iterable<org.iguana.regex.CharRange> alphabet) {
		
		org.iguana.regex.automaton.State dummyState = new org.iguana.regex.automaton.State();
		alphabet.forEach(r -> dummyState.addTransition(new Transition(r, dummyState)));
		
		for (org.iguana.regex.automaton.State state : automaton.getStates()) {
			for (org.iguana.regex.CharRange r : alphabet) {
				if (!state.hasTransition(r)) {
					state.addTransition(new Transition(r, dummyState));
				}
			}			
		}
		
		return Automaton.builder(automaton.getStartState()).build();
	}
	
	private static Map<org.iguana.regex.CharRange, List<org.iguana.regex.CharRange>> merge(org.iguana.regex.CharRange[] alphabet1, org.iguana.regex.CharRange[] alphabet2) {
		List<org.iguana.regex.CharRange> alphabets = new ArrayList<>();
		for (org.iguana.regex.CharRange r : alphabet1) { alphabets.add(r); }
		for (org.iguana.regex.CharRange r : alphabet2) { alphabets.add(r); }
		
		return CharacterRanges.toNonOverlapping(alphabets);
	}
	
	
	public static Automaton minimize(Automaton automaton) {
		if (automaton.isMinimized())
			return automaton;

		return minimize(automaton.getAlphabet(), automaton.getStates());
	}
	
	
	/**
	 * Creates the reverse of the given automaton. A reverse automaton 
	 * accept the reverse language accepted by the original automaton. To construct
	 * a reverse automaton, all final states of the original automaton are becoming 
	 * start states, transitions are reversed and the start state becomes the
	 * only final state.
	 * 
	 */
	public static Automaton reverse(Automaton automaton) {

		// 1. Creating new states for each state of the original automaton
		final Map<org.iguana.regex.automaton.State, org.iguana.regex.automaton.State> newStates = new HashMap<>();
		
		for (org.iguana.regex.automaton.State s : automaton.getStates()) {
			newStates.put(s, new org.iguana.regex.automaton.State());
		}
		
		// 2. Creating a new start state and adding epsilon transitions to the final
		// states of the original automata
		org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();
		
		for (org.iguana.regex.automaton.State finalState : automaton.getFinalStates()) {
			startState.addEpsilonTransition(newStates.get(finalState));
		}
		
		// 3. Reversing the transitions
		for (org.iguana.regex.automaton.State state : automaton.getStates()) {
			for (Transition t : state.getTransitions()) {
				newStates.get(t.getDestination()).addTransition(new Transition(t.getRange(), newStates.get(state)));
			}
		}
		
		// 4. Making the start state final
		newStates.get(automaton.getStartState()).setStateType(org.iguana.regex.automaton.StateType.FINAL);
		 
		return Automaton.builder(startState).build();
	}
	
	/**
	 * 
	 * Note: unreachable states are already removed as we gather the states
	 * reachable from the start state of the given NFA.
	 * 
	 * @return
	 */
	public static Automaton minimize(org.iguana.regex.CharRange[] alphabet, org.iguana.regex.automaton.State[] states) {
		
		int size = states.length;
		int[][] table = new int[size][size];
		
		final int EMPTY = -2;
		final int EPSILON = -1;
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				table[i][j] = EMPTY;
			}
 		}
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if(states[i].isFinalState() && !states[j].isFinalState()) {
					table[i][j] = EPSILON;
				}
				if(states[j].isFinalState() && !states[i].isFinalState()) {
					table[i][j] = EPSILON;
				}
				
				// Differentiate between final states
				if(states[i].isFinalState() && 
				   states[j].isFinalState()) {
					table[i][j] = EPSILON;
				}
			}
		}
		
		boolean changed = true;
		
		while (changed) {
			changed = false;

				for (int i = 0; i < table.length; i++) {
					for (int j = 0; j < i; j++) {
						
						// If two states i and j are distinct
						if (table[i][j] == EMPTY) {
							for (int t = 0; t < alphabet.length; t++) {
								org.iguana.regex.automaton.State q1 = states[i].getState(alphabet[t]);
								org.iguana.regex.automaton.State q2 = states[j].getState(alphabet[t]);

								// If both states i and j have no outgoing transitions on the interval t, continue with the
								// next transition.
								if(q1 == null && q2 == null) {
									continue;
								}

								// If the transition t can be applied on state i but not on state j, two states are
								// disjoint. Continue with the next pair of states.
								if((q1 == null && q2 != null) || (q2 == null && q1 != null)) {
									table[i][j] = t;
									changed = true;
									break;
								}
								
								if(q1.getId() == q2.getId()) {
									continue;
								}
								
								int a;
								int b;
								if (q1.getId() > q2.getId()) {
									a = q1.getId();
									b = q2.getId();
								} else {
									a = q2.getId();
									b = q1.getId();
								}
								
								if (table[a][b] != EMPTY) {
									table[i][j] = t;
									changed = true;
									break;
								}
							}
						}
					}
				}
		}
		
		Map<org.iguana.regex.automaton.State, Set<org.iguana.regex.automaton.State>> partitionsMap = new HashMap<>();
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if (table[i][j] == EMPTY) {
					org.iguana.regex.automaton.State stateI = states[i];
					org.iguana.regex.automaton.State stateJ = states[j];
					
					Set<org.iguana.regex.automaton.State> partitionI = partitionsMap.get(stateI);
					Set<org.iguana.regex.automaton.State> partitionJ = partitionsMap.get(stateJ);
					
					if(partitionI == null && partitionJ == null) {
						Set<org.iguana.regex.automaton.State> set = new HashSet<>();
						set.add(stateI);
						set.add(stateJ);
						partitionsMap.put(stateI, set);
						partitionsMap.put(stateJ, set);
					}
					else if(partitionI == null && partitionJ != null) {
						partitionJ.add(stateI);
						partitionsMap.put(stateI, partitionJ);
					} 
					else if(partitionJ == null && partitionI != null) {
						partitionI.add(stateJ);
						partitionsMap.put(stateJ, partitionI);
					}
					else { 
						partitionJ.addAll(partitionI);
						partitionI.addAll(partitionJ);
					}
				}
			}
		}
		
		HashSet<Set<org.iguana.regex.automaton.State>> partitions = new HashSet<Set<org.iguana.regex.automaton.State>>(partitionsMap.values());
		
		org.iguana.regex.automaton.State startState = null;
		
		for (org.iguana.regex.automaton.State state : states) {
			if (partitionsMap.get(state) == null) {
				Set<org.iguana.regex.automaton.State> set = new HashSet<>();
				set.add(state);
				partitions.add(set);
			}
		} 
		
		Map<org.iguana.regex.automaton.State, org.iguana.regex.automaton.State> newStates = new HashMap<>();

		for (Set<org.iguana.regex.automaton.State> set : partitions) {
			org.iguana.regex.automaton.State newState = new org.iguana.regex.automaton.State();
			for (org.iguana.regex.automaton.State state : set) {
				
				newState.addRegularExpressions(state.getRegularExpressions());
				
				if (startState == state) {
					startState = newState;
				}
				if (state.isFinalState()) {
					newState.setStateType(StateType.FINAL);
				}
				newStates.put(state, newState);
			}
		}
		
		for (org.iguana.regex.automaton.State state : states) {
			for (Transition t : state.getTransitions()) {
				newStates.get(state).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(t.getDestination())));;				
			}
		}
		
		return Automaton.builder(startState).build();
	}

	
	private static Set<org.iguana.regex.automaton.State> epsilonClosure(Set<org.iguana.regex.automaton.State> states) {
		Set<org.iguana.regex.automaton.State> newStates = new HashSet<>(states);
		
		for(org.iguana.regex.automaton.State state : states) {
			Set<org.iguana.regex.automaton.State> s = state.getEpsilonSates();
			if(!s.isEmpty()) {
				newStates.addAll(s);
				newStates.addAll(epsilonClosure(s));
			}
		}
		
		return newStates;
	}
	
	private static Set<org.iguana.regex.automaton.State> move(Set<org.iguana.regex.automaton.State> state, CharRange r) {
		Set<org.iguana.regex.automaton.State> result = new HashSet<>();
		for (org.iguana.regex.automaton.State s: state) {
			org.iguana.regex.automaton.State dest = s.getState(r);
			if (dest != null) {
				result.add(dest);
			}
		}
		
		return epsilonClosure(result);
	}
	
	@FunctionalInterface
	private static interface Op {
		public boolean execute(org.iguana.regex.automaton.State s1, State s2);
	}
	
}
