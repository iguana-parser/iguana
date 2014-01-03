package org.jgll.regex;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.util.Tuple;

public class AutomatonOperations {
	
	public static NFA makeDeterministic(NFA nfa) {
		
		Set<Set<State>> visitedStates = new HashSet<>();
		Deque<Set<State>> processList = new ArrayDeque<>();
		
		Set<State> initialState = new HashSet<>();
		initialState.add(nfa.getStartState());
		initialState = epsilonClosure(initialState);
		visitedStates.add(initialState);
		processList.add(initialState);
		
		int[] intervals = nfa.getIntervals();
		
		Map<Integer, Integer> intervalIds = new HashMap<>();
		
		for(int i = 0; i < intervals.length; i++) {
			intervalIds.put(intervals[i], i);
		}
		
		/**
		 * A map from the set of NFA states to the new state in the produced DFA.
		 * This map is used for sharing DFA states.
		 */
		Map<Set<State>, State> newStatesMap = new HashMap<>();
		
		State startState = new State();
		
		newStatesMap.put(initialState, startState);
		
		while(!processList.isEmpty()) {
			Set<State> stateSet = processList.poll();
			State source = newStatesMap.get(stateSet);
			
			// The state should have been created before.
			assert source != null;
			
			Map<Tuple<Integer, Integer>, Set<State>> transitionsMap = move(stateSet, intervals);

			for(Entry<Tuple<Integer, Integer>, Set<State>> e : transitionsMap.entrySet()) {
				Set<State> newState = epsilonClosure(e.getValue());
				
				State destination = newStatesMap.get(newState);
				if(destination == null) {
					destination = new State();
					newStatesMap.put(newState, destination);
				}
				
				Transition transition = new Transition(e.getKey().getFirst(), e.getKey().getSecond(), destination);
				transition.setId(intervalIds.get(e.getKey().getFirst()));
				source.addTransition(transition);
				
				if(!visitedStates.contains(newState)) {
					visitedStates.add(newState);
					processList.add(newState);
				}
			}
		}
		
		setStateIDs(startState);
		
		
		// Setting the final states.
		outer:
		for(Entry<Set<State>, State> e : newStatesMap.entrySet()) {
			for(State s : e.getKey()) {
				if(s.isFinalState()) {
					e.getValue().setFinalState(true);
					continue outer;
				}
			}			
		}
		
		return new NFA(startState);
	}
	
	public static NFA minimize(NFA nfa) {
		return nfa;
	}
	
	public static DFA createDFA(NFA nfa) {
		int statesCount = nfa.getCountStates();
		int inputLength = nfa.getIntervals().length;
		int[][] transitionTable = new int[statesCount][inputLength];
		boolean[] endStates = new boolean[statesCount];
		
		for(int i = 0; i < transitionTable.length; i++) {
			for(int j = 0; j < transitionTable[i].length; j++) {
				transitionTable[i][j] = -1;
			}
		}

		for(State state : nfa.getAllStates()) {
			for(Transition transition : state.getTransitions()) {
				transitionTable[state.getId()][transition.getId()] = transition.getDestination().getId();
			}
			
			if(state.isFinalState()) {
				endStates[state.getId()] = true;
			}
		}
		
		return new DFA(transitionTable, endStates, nfa.getStartState().getId(), nfa.getIntervals());		
	}
	
	private static Set<State> epsilonClosure(Set<State> states) {
		Set<State> newStates = new HashSet<>();
		for(State state : states) {
			newStates.addAll(epsilonClosure(state));
		}
		return newStates;
	}
	
	private static Set<State> epsilonClosure(State state) {
		Set<State> newStates = new HashSet<>();
		newStates.add(state);
		for(Transition t : state.getTransitions()) {
			if(t.isEpsilonTransition()) {
				State destination = t.getDestination();
				newStates.add(destination);
				newStates.addAll(epsilonClosure(destination));
			}
		}	
		
		return newStates;
	}
	
	private static Map<Tuple<Integer, Integer>, Set<State>> move(Set<State> states, int[] intervals) {
		
		Map<Tuple<Integer, Integer>, Set<State>> map = new HashMap<>();
		
		for(int i = 0; i < intervals.length; i++) {
			Set<State> newStates = new HashSet<>();

			for(State state : states) {
				for(Transition transition : state.getTransitions()) {
					if(intervals[i] >= transition.getStart() && intervals[i] <= transition.getEnd()) {
						newStates.add(transition.getDestination());
					}
				}
			}
			if(!newStates.isEmpty()) {
				if(i + 1 < intervals.length) {
					map.put(new Tuple<>(intervals[i], intervals[i+1] - 1), newStates);
				} 
				if(i + 1 == intervals.length) {
					map.put(new Tuple<>(intervals[i] - 1, intervals[i] - 1), newStates);
				}
			}			
		}
		
		return map;
	}
	
	private static void removeUnreachableStates(State startState) {
		
		// 1. Calculate the set of reachable states.
		final Set<State> reachableStates = new HashSet<>();
		reachableStates.add(startState);
		
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				reachableStates.add(state);
			}
		});
		
		// 2. Remove the states that are not in the set
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				Set<Transition> transitionsToBeRemoved = new HashSet<>();
				for(Transition t : state.getTransitions()) {
					if(!reachableStates.contains(t.getDestination())) {
						transitionsToBeRemoved.add(t);
					}
				}
				state.removeTransitions(transitionsToBeRemoved);
			}
		});
	}
	
	private static void minimize(State startState, int[] intervals) {
		removeUnreachableStates(startState);
		
		final Set<State> finalStates = new HashSet<>();
		final Set<State> nonFinalStates = new HashSet<>();
		
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				if(state.isFinalState()) {
					finalStates.add(state);
				} else {
					nonFinalStates.add(state);
				}
			}
		});

		Set<Set<State>> partitions = new HashSet<>();
		partitions.add(finalStates);
		partitions.add(nonFinalStates);
		
		
		// A map from each state to its partition
		Map<State, Set<State>> partitionsMap = new HashMap<>();
		for(State state : finalStates) {
			partitionsMap.put(state, finalStates);
		}
		for(State state : nonFinalStates) {
			partitionsMap.put(state, nonFinalStates);
		}
		
		boolean changed = true;
		
		while(changed) {
			changed = false;
			
			l:
			for(Set<State> partition : partitions) {
				for(int i = 0; i < intervals.length; i++) {

					Set<State> p = null;

					for(State state : partition) {
						
						// Only one transition moves based on the input (the input is a DFA)
						for(Transition transition : state.getTransitions()) {
							if(intervals[i] >= transition.getStart() && intervals[i] <= transition.getEnd()) {
								Set<State> pp = partitionsMap.get(transition.getDestination());
								if(p == null) {
									p = pp;
								}
								else if(p != pp) {
									partition.remove(partition);
									partitionsMap.remove(state);
									
									Set<State> newPartition = new HashSet<>();
									newPartition.add(state);
									partitions.add(newPartition);
									partitionsMap.put(state, newPartition);
									
									break l;
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	public static String toJavaCode(Automaton automaton) {
		State startState = automaton.getStartState();
		StringBuilder sb = new StringBuilder();
		Map<State, Integer> visitedStates = new HashMap<>();
		visitedStates.put(startState, 1);
		toJavaCode(startState, sb, visitedStates);
		return sb.toString();
	}
	
	private static void toJavaCode(State state, StringBuilder sb, Map<State, Integer> visitedStates) {
		sb.append("State state" + visitedStates.get(state) + " = new State();\n");
		for(Transition transition : state.getTransitions()) {
			State destination = transition.getDestination();
			
			if(!visitedStates.keySet().contains(destination)) {
				visitedStates.put(destination, visitedStates.size() + 1);
				toJavaCode(destination, sb, visitedStates);
			}
			
			sb.append("state" + visitedStates.get(state) + ".addTransition(new Transition(" + transition.getStart() + 
					                                               ", " + transition.getEnd() + ", state" + visitedStates.get(destination) + ");\n");
		}
	}
	
	public static BitSet getCharacters(Automaton automaton) {
		final BitSet bitSet = new BitSet();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(Transition transition : state.getTransitions()) {
					bitSet.set(transition.getStart(), transition.getEnd() + 1);
				}
			}
		});
		
		return bitSet;
	}
	
	public static int[] getIntervals(Automaton automaton) {
		
		final Set<Integer> set = new HashSet<>();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(Transition transition : state.getTransitions()) {
					if(!transition.isEpsilonTransition()) {
						set.add(transition.getStart());
						set.add(transition.getEnd() + 1);						
					}
				}
			}
		});
		
		Integer[] array = set.toArray(new Integer[] {});
		Arrays.sort(array);
		
		int[] result = new int[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		
		return result;
	}
 	
	public static int getCountStates(Automaton automaton) {
		
		final int[] count = new int[1];
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				count[0]++;
			}
		});

		return count[0];
	}
	
	public static Set<State> getAllStates(Automaton automaton) {
		final Set<State> states = new HashSet<>();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				states.add(state);
			}
		});
		
		return states;
	}
	
	public static void setStateIDs(State startState) {
				
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			int id = 0;

			@Override
			public void visit(State state) {
				state.setId(id++);
			}
		});
	}
	
	public static void setStateIDs(Automaton automaton) {
		
		AutomatonVisitor.visit(automaton, new VisitAction() {

			int id = 0;
			
			@Override
			public void visit(State state) {
				state.setId(id++);
			}
		});
	}
	
	public static void removeDeadStates(Automaton nfa) {
		AutomatonVisitor.visit(nfa, new VisitAction() {

			@Override
			public void visit(State state) {

				Set<Transition> transitionsToBeRemoved = new HashSet<>();
				
				main:
				for(Transition transition : state.getTransitions()) {
					State destination = transition.getDestination();
					
					if(destination.getTransitions().size() == 0) {
						transitionsToBeRemoved.add(transition);
					} else {
						for(Transition t : destination.getTransitions()) {
							if(t.isLoop(destination)) {
								continue main;
							}
						}
						transitionsToBeRemoved.add(transition);
					}
				}
				
				state.removeTransitions(transitionsToBeRemoved);
			}
		});
	}

	public static Set<State> getFinalStates(NFA nfa) {
		
		final Set<State> finalStates = new HashSet<>();
		
		AutomatonVisitor.visit(nfa, new VisitAction() {
			
			@Override
			public void visit(State state) {
				if(state.isFinalState()) {
					finalStates.add(state);
				}
			} 
		});
		
		return finalStates;
	}
	
}
