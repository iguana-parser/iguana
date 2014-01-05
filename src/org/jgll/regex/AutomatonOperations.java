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
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;

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
			Set<State> reachableStates = new HashSet<>();

			for(State state : states) {
				for(Transition transition : state.getTransitions()) {
					if(intervals[i] >= transition.getStart() && intervals[i] <= transition.getEnd()) {
						reachableStates.add(transition.getDestination());
					}
				}
			}
			
			// Creating the transitions for the reachable states based on the transition intervals.
			if(!reachableStates.isEmpty()) {
				if(i + 1 < intervals.length) {
					map.put(new Tuple<>(intervals[i], intervals[i+1] - 1), reachableStates);
				} 
				if(i + 1 == intervals.length) {
					map.put(new Tuple<>(intervals[i] - 1, intervals[i] - 1), reachableStates);
				}
			}			
		}
		
		return map;
	}
		
	/**
	 * 
	 * Note: unreachable states are already removed as we gather the states
	 * reachable from the start state of the given NFA.
	 * 
	 * @param nfa
	 * @return
	 */
	public static NFA minimize(NFA nfa) {
		
		int[][] table = new int[nfa.getCountStates()][nfa.getCountStates()];
		
		final int EMPTY = -2;
		final int EPSILON = -1;
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				table[i][j] = EMPTY;
			}
 		}
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if(nfa.getState(i).isFinalState() && !nfa.getState(j).isFinalState()) {
					table[i][j] = EPSILON;
				}
				if(nfa.getState(j).isFinalState() && !nfa.getState(i).isFinalState()) {
					table[i][j] = EPSILON;
				}
			}
		}
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				System.out.print(table[i][j] + " ");
			}
			System.out.println("\n");
		}

		
		GraphVizUtil.generateGraph(NFAToDot.toDot(nfa.getStartState()), "/Users/aliafroozeh/output", "nfa", GraphVizUtil.LEFT_TO_RIGHT);
		
		int[] intervals = nfa.getIntervals();
		
		boolean changed = true;
		
		while(changed) {
			changed = false;

				for (int i = 0; i < table.length; i++) {
					for (int j = 0; j < i; j++) {
						
						// If two states i and j are distinct
						if(table[i][j] == EMPTY) {
							for(int t = 0; t < intervals.length; t++) {
								State q1 = moveTransition(nfa.getState(i), intervals[t]);
								State q2 = moveTransition(nfa.getState(j), intervals[t]);
								
								if(q1 == null || q2 == null) {
									continue;
								}
								
								if(q1.getId() == q2.getId()) {
									continue;
								}
								
								int a;
								int b;
								if(q1.getId() > q2.getId()) {
									a = q1.getId();
									b = q2.getId();
								} else {
									a = q2.getId();
									b = q1.getId();
								}
								
								if(table[a][b] != EMPTY) {
									table[i][j] = t;
									changed = true;
									break;
								}
							}
						}
					}
				}
				
				for (int i = 0; i < table.length; i++) {
					for (int j = 0; j < i; j++) {
						System.out.print(table[i][j] + " ");
					}
					System.out.println("\n");
				}
		}
		
		
		Set<State> partitined = new HashSet<>();
		
		Set<Set<State>> partitions = new HashSet<>();
		
		for (int i = 0; i < table.length; i++) {
			
			if(partitined.contains(nfa.getState(i))) {
				continue;
			}
			
			partitined.add(nfa.getState(i));
			
			Set<State> set = new HashSet<>();
			set.add(nfa.getState(i));
			for (int j = 0; j < i; j++) {
				if(table[i][j] == EMPTY) {
					set.add(nfa.getState(j));
					partitined.add(nfa.getState(j));
				}
			}
			partitions.add(set);
		}
		
		Map<State, State> newStates = new HashMap<>();

		State startState = null;
		
		for(Set<State> set : partitions) {
			State newState = new State();
			for(State state : set) {
				if(nfa.getStartState() == state) {
					startState = newState;
				}
				newStates.put(state, newState);
			}
		}
		
		for(State state : nfa.getAllStates()) {
			for(Transition t : state.getTransitions()) {
				newStates.get(state).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(t.getDestination())));;				
			}
		}
		
		return new NFA(startState);
	}


	private static State moveTransition(State state, int i) {
		for(Transition transition : state.getTransitions()) {
			if(transition.getStart() >= i && i < transition.getEnd()) {
				return transition.getDestination();
			}
		}
		return null;
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
