package org.jgll.regex.automaton;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgll.grammar.symbol.CharacterRange;

public class AutomatonOperations {
	
	public static Automaton makeDeterministic(Automaton automaton) {
		if (automaton.isDeterministic())
			return automaton;
		
		return makeDeterministic(automaton.getStartState(), automaton.getAlphabet());
	}

	public static Automaton makeDeterministic(State start, CharacterRange[] alphabet) {
		
		Set<Set<State>> visitedStates = new HashSet<>();
		Deque<Set<State>> processList = new ArrayDeque<>();
		
		Set<State> initialState = new HashSet<>();
		initialState.add(start);
		initialState = epsilonClosure(initialState);
		visitedStates.add(initialState);
		processList.add(initialState);
		
		/**
		 * A map from the set of NFA states to the new state in the produced DFA.
		 * This map is used for sharing DFA states.
		 */
		Map<Set<State>, State> newStatesMap = new HashMap<>();
		
		State startState = new State();
		
		newStatesMap.put(initialState, startState);
		
		while (!processList.isEmpty()) {
			Set<State> stateSet = processList.poll();

			for (CharacterRange r : alphabet) {
				Set<State> destState = move(stateSet, r);
				
				if (destState.isEmpty())
					continue;
				
				State source = newStatesMap.get(stateSet);
				State dest = newStatesMap.computeIfAbsent(destState, s -> new State());
				source.addTransition(new Transition(r, dest));

				
				if (!visitedStates.contains(destState)) {
					visitedStates.add(destState);
					processList.add(destState);
				}
			}
		}
		
		// Setting the final states.
		outer:
		for (Entry<Set<State>, State> e : newStatesMap.entrySet()) {
			for (State s : e.getKey()) {
				if (s.getStateType() == StateType.FINAL) {
					e.getValue().setStateType(StateType.FINAL);
					continue outer;
				}
			}			
		}

		return Automaton.builder(startState).setDeterministic(true).build();
	}
	
	private static Set<State> move(Set<State> state, CharacterRange r) {
		Set<State> result = new HashSet<>();
		for (State s: state) {
			State dest = s.getState(r);
			if (dest != null) {
				result.add(dest);
			}
		}
		
		return epsilonClosure(result);
	}

	public static Automaton minimize(Automaton automaton) {
		if (automaton.isMinimized())
			return automaton;

		return minimize(automaton.getAlphabet(), automaton.getStates());
	}
	
	/**
	 * 
	 * Note: unreachable states are already removed as we gather the states
	 * reachable from the start state of the given NFA.
	 * 
	 * @param automaton
	 * @return
	 */
	public static Automaton minimize(CharacterRange[] alphabet, State[] states) {
		
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
								State q1 = states[i].getState(alphabet[t]);
								State q2 = states[j].getState(alphabet[t]);

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
		
		Map<State, Set<State>> partitionsMap = new HashMap<>();
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if (table[i][j] == EMPTY) {
					State stateI = states[i];
					State stateJ = states[j];
					
					Set<State> partitionI = partitionsMap.get(stateI);
					Set<State> partitionJ = partitionsMap.get(stateJ);
					
					if(partitionI == null && partitionJ == null) {
						Set<State> set = new HashSet<>();
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
		
		HashSet<Set<State>> partitions = new HashSet<Set<State>>(partitionsMap.values());
		
		State startState = null;
		
		for (State state : states) {
			if (partitionsMap.get(state) == null) {
				Set<State> set = new HashSet<>();
				set.add(state);
				partitions.add(set);
			}
		} 
		
		Map<State, State> newStates = new HashMap<>();

		for (Set<State> set : partitions) {
			State newState = new State();
			for (State state : set) {
				
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
		
		for (State state : states) {
			for (Transition t : state.getTransitions()) {
				newStates.get(state).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(t.getDestination())));;				
			}
		}
		
		return Automaton.builder(startState).build();
	}

	
	private static Set<State> epsilonClosure(Set<State> states) {
		Set<State> newStates = new HashSet<>(states);
		
		for(State state : states) {
			Set<State> s = state.getEpsilonSates();
			if(!s.isEmpty()) {
				newStates.addAll(s);
				newStates.addAll(epsilonClosure(s));
			}
		}
		
		return newStates;
	}

	
}
