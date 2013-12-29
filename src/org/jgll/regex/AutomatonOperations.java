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
	
	public static DFA convertNFAtoDFA(NFA nfa) {
		
		Set<Set<State>> visitedStates = new HashSet<>();
		Deque<Set<State>> processList = new ArrayDeque<>();
		
		Set<State> initialState = new HashSet<>();
		initialState.add(nfa.getStartState());
		Set<State> newState = epsilonClosure(initialState);
		visitedStates.add(newState);
		processList.add(newState);
		
		int[] intervals = nfa.getIntervals();
		
		Map<Integer, Integer> intervalIds = new HashMap<>();
		
		for(int i = 0; i < intervals.length; i++) {
			intervalIds.put(intervals[i], i);
		}

		// For sharing states.
		Map<Set<State>, State> newStatesMap = new HashMap<>();
		
		State startState = null;
		
		while(!processList.isEmpty()) {
			Set<State> stateSet = processList.poll();
			State source = new State();
			
			if(startState == null) {
				startState = source;
			}
			
			Map<Tuple<Integer, Integer>, Set<State>> transitionsMap = move(stateSet, intervals);

			for(Entry<Tuple<Integer, Integer>, Set<State>> e : transitionsMap.entrySet()) {
				newState = epsilonClosure(e.getValue());
				
				State destination = newStatesMap.get(newState);
				if(destination == null) {
					destination = new State();
					newStatesMap.put(e.getValue(), destination);
					
					for(State s : newState) {
						if(s.isFinalState()){
							destination.setFinalState(true);
							break;
						}
					}
				}
				
				Transition transition = new Transition(e.getKey().getFirst(), e.getKey().getSecond(), destination);
				transition.setId(intervalIds.get(e.getKey().getFirst()));
				source.addTransition(transition);
				
				if(!visitedStates.contains(newState)) {
					processList.add(newState);
				}
			}
		}
		
		setStateIDs(startState);
		
		int[][] transitionTable = new int[newStatesMap.size() + 1][intervals.length];
		boolean[] endStates = new boolean[newStatesMap.size() + 1];
		
		for(int i = 0; i < transitionTable.length; i++) {
			for(int j = 0; j < transitionTable[i].length; j++) {
				transitionTable[i][j] = -1;
			}
		}

		for(Transition transition : startState.getTransitions()) {
			transitionTable[startState.getId()][transition.getId()] = transition.getDestination().getId();
		}
		
		for(State state : newStatesMap.values()) {
			for(Transition transition : state.getTransitions()) {
				transitionTable[state.getId()][transition.getId()] = transition.getDestination().getId();
			}
			endStates[state.getId()] = true;
		}
		
		return new DFA(transitionTable, endStates, startState.getId(), intervals);
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
	
}
