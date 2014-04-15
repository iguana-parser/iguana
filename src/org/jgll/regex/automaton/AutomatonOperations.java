package org.jgll.regex.automaton;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.regex.matcher.LargeIntervalTransitions;
import org.jgll.regex.matcher.ShortIntervalTransitions;
import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Tuple;

public class AutomatonOperations {
	
	public static Automaton makeDeterministic(Automaton automaton) {
		
		Set<Set<State>> visitedStates = new HashSet<>();
		Deque<Set<State>> processList = new ArrayDeque<>();
		
		Set<State> initialState = new HashSet<>();
		initialState.add(automaton.getStartState());
		initialState = epsilonClosure(initialState);
		visitedStates.add(initialState);
		processList.add(initialState);
		
		/**
		 * A map from the set of NFA states to the new state in the produced DFA.
		 * This map is used for sharing DFA states.
		 */
		Map<Set<State>, State> newStatesMap = new HashMap<>();
		
		
		State startState = new State();
		addActions(initialState, startState);
		
		newStatesMap.put(initialState, startState);
		
		Map<Tuple<State, Integer>, Set<State>> cache = new HashMap<>();
		
		while(!processList.isEmpty()) {
			Set<State> stateSet = processList.poll();
			
			Set<MoveResult> transitionsSet = move(stateSet, cache);

			for(MoveResult r : transitionsSet) {
				Set<State> states = r.states;
				Set<State> newState = epsilonClosure(states);
				
				State destination = newStatesMap.get(newState);
				if(destination == null) {
					destination = new State();
					
					addActions(states, destination);
					
					newStatesMap.put(newState, destination);
				}
				
				State source = newStatesMap.get(stateSet);
				
				// The state should have been created before.
				assert source != null;
				
				Transition transition = new Transition(r.start, r.end, destination, r.actions);
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
		
		// Setting the reject states. If during the process of making a DFA deterministic,
		// a reject and a final state are merged, consider it as a final state.
		outer:
		for(Entry<Set<State>, State> e : newStatesMap.entrySet()) {
			for(State s : e.getKey()) {
				if(s.isRejectState()) {
					State state = e.getValue();
					if(!state.isFinalState()) {
						state.setRejectState(true);
					}
					continue outer;					
				}
			}			
		}
		
		return new Automaton(startState);
	}

	private static void addActions(Set<State> initialState, State startState) {
		for (State s : initialState) {
			startState.addActions(s.getActions());
		}
	}
	
	public static RunnableAutomaton createRunnableAutomaton(Automaton a) {
		
		final Map<State, RunnableState> map = new HashMap<>();
		
		// Create a new runnable state for each state
		for (State state : a.getAllStates()) {
			if (state.getCountTransitions() > 0) {
				map.put(state, new RunnableState(state.isFinalState(), state.isRejectState(), state.getActions()));
			} else {
				map.put(state, new FinalRunnableState(state.isFinalState(), state.isRejectState()));
			}
		}
		
		final Map<Transition, RunnableState> transitionsMap = new HashMap<>();
		
		AutomatonVisitor.visit(a, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for (Transition t : state.getTransitions()) {
					transitionsMap.put(t, map.get(t.getDestination()));
				}
			}
		});

		for (Entry<State, RunnableState> e : map.entrySet()) {
			Transition[] sortedTransitions = e.getKey().getSortedTransitions();
			
			if (sortedTransitions.length > 0) {
				Transitions transitions;
				
				if (sortedTransitions[sortedTransitions.length - 1].getEnd() - sortedTransitions[0].getStart() <= Character.MAX_VALUE) {
					transitions = new ShortIntervalTransitions(sortedTransitions, transitionsMap);
				} else {
					transitions = new LargeIntervalTransitions(sortedTransitions, transitionsMap);
				}
				
				e.getValue().setTransitions(transitions);				
			}
			
		}
		
		return new RunnableAutomaton(map.get(a.getStartState()));
	}
	
	private static Set<State> epsilonClosure(Set<State> states) {
		Set<State> newStates = new HashSet<>(states);
		
		for(State state : states) {
			Set<State> s = state.getEpsilonClosure();
			if(!s.isEmpty()) {
				newStates.addAll(s);
				newStates.addAll(epsilonClosure(s));
			}
		}
		
		return newStates;
	}
	
	private static class MoveResult {

		int start;
		int end;
		Set<State> states;
		Set<Action> actions;
		
		public MoveResult(int start, int end, Set<State> states, Set<Action> actions) {
			this.start = start;
			this.end = end;
			this.states = states;
			this.actions = actions;
		}
		
		@Override
		public String toString() {
			return String.format("(%d, %d, %s, %s)", start, end, states, actions);
		}
	}
	
	private static Set<MoveResult> move(Set<State> states, Map<Tuple<State, Integer>, Set<State>> cache) {
		
		Map<Integer, Set<Action>> transitionActions = new HashMap<>();
		int[] intervals = getIntervalsOfStates(states, transitionActions);
		
		Set<MoveResult> resultSet = new HashSet<>();
		
		for(int i = 0; i < intervals.length; i++) {
			
			Set<State> reachableStates = new HashSet<>();

			for(State state : states) {
				
				Tuple<State, Integer> key = new Tuple<>(state, i);
				Set<State> set = cache.get(key);
				
				if(set == null) {
					set = new HashSet<>();
					for(Transition transition : state.getTransitions()) {
						if(transition.canMove(intervals[i])) {
							set.add(transition.getDestination());						
						}
					}
					cache.put(key, set);
				}
				
				reachableStates.addAll(set);
			}
			
			// Creating the transitions for the reachable states based on the transition intervals.
			if(!reachableStates.isEmpty()) {
				Set<Action> actions = transitionActions.get(intervals[i]);
				actions = actions == null ? new HashSet<Action>() : actions;
				if(i + 1 < intervals.length) {
					resultSet.add(new MoveResult(intervals[i], intervals[i + 1] - 1, reachableStates, actions));
				} 
				if(i + 1 == intervals.length) {
					resultSet.add(new MoveResult(intervals[i] - 1, intervals[i] - 1, reachableStates, actions));
				}
			}			
		}
		
		return resultSet;
	}
	
	/**
	 * 
	 * Note: unreachable states are already removed as we gather the states
	 * reachable from the start state of the given NFA.
	 * 
	 * @param automaton
	 * @return
	 */
	public static Automaton minimize(Automaton automaton) {
		
		int[][] table = new int[automaton.getCountStates()][automaton.getCountStates()];
		
		final int EMPTY = -2;
		final int EPSILON = -1;
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				table[i][j] = EMPTY;
			}
 		}
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if(automaton.getState(i).isFinalState() && !automaton.getState(j).isFinalState()) {
					table[i][j] = EPSILON;
				}
				if(automaton.getState(j).isFinalState() && !automaton.getState(i).isFinalState()) {
					table[i][j] = EPSILON;
				}
				
				// Differentiate between final states
				if(automaton.getState(i).isFinalState() && 
				   automaton.getState(j).isFinalState()) {
					table[i][j] = EPSILON;
				}
			}
		}
		
		int[] intervals = automaton.getIntervals();
		
		boolean changed = true;
		
		while(changed) {
			changed = false;

				for (int i = 0; i < table.length; i++) {
					for (int j = 0; j < i; j++) {
						
						// If two states i and j are distinct
						if(table[i][j] == EMPTY) {
							for(int t = 0; t < intervals.length; t++) {
								State q1 = moveTransition(automaton.getState(i), intervals[t]);
								State q2 = moveTransition(automaton.getState(j), intervals[t]);

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
		}
		
		
		Map<State, Set<State>> partitionsMap = new HashMap<>();
		
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if(table[i][j] == EMPTY) {
					State stateI = automaton.getState(i);
					State stateJ = automaton.getState(j);
					
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
		
		for(State state : automaton.getAllStates()) {
			if(partitionsMap.get(state) == null) {
				Set<State> set = new HashSet<>();
				set.add(state);
				partitions.add(set);
			}
		} 
		
		Map<State, State> newStates = new HashMap<>();

		for(Set<State> set : partitions) {
			State newState = new State();
			for(State state : set) {
				
				newState.addRegularExpressions(state.getRegularExpressions());
				
				if(automaton.getStartState() == state) {
					startState = newState;
				}
				if(state.isFinalState()) {
					newState.setFinalState(true);
				}
				newStates.put(state, newState);
			}
		}
		
		for(State state : automaton.getAllStates()) {
			for(Transition t : state.getTransitions()) {
				newStates.get(state).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(t.getDestination())));;				
			}
		}
		
		return new Automaton(startState);
	}

	/**
	 * For debugging purposes 
	 */
	@SuppressWarnings("unused")
	private static void printMinimizationTable(int[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				System.out.print(table[i][j] + " ");
			}
			System.out.println("\n");
		}
	}


	private static State moveTransition(State state, int i) {
		for(Transition transition : state.getTransitions()) {
			if(transition.canMove(i)) {
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
		
		if(state.isFinalState()) {
			sb.append("State state" + visitedStates.get(state) + " = new State(true);\n");			
		} else {
			sb.append("State state" + visitedStates.get(state) + " = new State();\n");
		}
		
		for(Transition transition : state.getTransitions()) {
			State destination = transition.getDestination();
			
			if(!visitedStates.keySet().contains(destination)) {
				visitedStates.put(destination, visitedStates.size() + 1);
				toJavaCode(destination, sb, visitedStates);
			}
			
			sb.append("state" + visitedStates.get(state) + ".addTransition(new Transition(" + transition.getStart() + 
					                                               ", " + transition.getEnd() + ", state" + visitedStates.get(destination) + "));\n");
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
	
	public static int[] merge(int[] i1, int[] i2) {
		Set<Integer> set = new HashSet<>();
		
		for(int i = 0; i < i1.length; i++) {
			set.add(i1[i]);
		}
		
		for(int i = 0; i < i2.length; i++) {
			set.add(i2[i]);
		}
		
		int i = 0;
		int[] merged = new int[set.size()];
		for(int c : set) {
			merged[i++] = c;
		}

		Arrays.sort(merged);
		
		return merged;
	}
	
	public static int[] getIntervalsOfStates(Set<State> states, Map<Integer, Set<Action>> map) {
		
		Set<Transition> transitions = new HashSet<>();
		for (State state : states) {
			transitions.addAll(state.getTransitions());
		}
		
		return getIntervals(transitions, map);
		
	}
	
	public static int[] getIntervals(Set<Transition> transitions, Map<Integer, Set<Action>> map) {
		
		Set<Integer> set = new HashSet<>();
		
		for(Transition transition : transitions) {
			if(!transition.isEpsilonTransition()) {
				set.add(transition.getStart());
				set.add(transition.getEnd() + 1);
			}
		}
		
		Integer[] array = set.toArray(new Integer[] {});
		Arrays.sort(array);
		
		int[] result = new int[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		
		for (Transition transition : transitions) {
			for (int i : result) {

				if (transition.getStart() <= i && i <= transition.getEnd() ) {
					Set<Action> s = map.get(i);
					if (s == null) {
						s = new HashSet<>();
						map.put(i, s);
					}
					s.addAll(transition.getActions());
				} else {
					Set<Action> s = map.get(i);
					if (s == null) {
						s = new HashSet<>();
						map.put(i, s);
					}
				}
			}
			
			if (result.length > 0) {
				int last = result[result.length - 1] - 1;
				if (transition.getStart() <= last && last <= transition.getEnd()) {
					Set<Action> s = map.get(last);
					if (s == null) {
						s = new HashSet<>();
						map.put(last, s);
					}
					s.addAll(transition.getActions());
				} else {
					Set<Action> s = map.get(last);
					if (s == null) {
						s = new HashSet<>();
						map.put(last, s);
					}
				}
			}
 		}
		
		return result;
	}

	
	public static int[] getIntervals(Automaton automaton) {		
		return getIntervals(getAllTransitions(automaton.getStartState()), new HashMap<Integer, Set<Action>>());
	}
	
	public static int[] getIntervals(State startState) {		
		return getIntervals(getAllTransitions(startState), new HashMap<Integer, Set<Action>>());
	}	
	
	public static Set<Transition> getAllTransitions(Automaton a) {
		return getAllTransitions(a.getStartState());
	}
	
	public static Set<Transition> getAllTransitions(State startState) {
		final Set<Transition> transitions = new HashSet<>();

		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(Transition transition : state.getTransitions()) {
					if(!transition.isEpsilonTransition()) {
						transitions.add(transition);
					}
				}
			}
		});
		
		return transitions;
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
		return getAllStates(automaton.getStartState());
	}
	
	public static Set<State> getAllStates(State startState) {
		final Set<State> states = new HashSet<>();
		
		AutomatonVisitor.visit(startState, new VisitAction() {
			
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
	
	public static void setTransitionIDs(Automaton automaton) {
		int[] intervals = automaton.getIntervals();
		
		/*
		 * A map from each interval's start to the interval id.
		 */
		final Map<Integer, Integer> intervalIds = new HashMap<>();
		
		for(int i = 0; i < intervals.length; i++) {
			intervalIds.put(intervals[i], i);
		}
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(Transition transition : state.getTransitions()) {
					if(transition.isEpsilonTransition()) {
						transition.setId(-1);
					} else {
						transition.setId(intervalIds.get(transition.getStart()));
					}
				}
			}
		});
	}
	
	public static void setStateIDs(Automaton automaton) {
		setStateIDs(automaton.getStartState());
	}

	public static Set<State> getFinalStates(Automaton nfa) {
		
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
	
	public static Set<State> getRejectStates(Automaton automaton) {
		
		final Set<State> rejectStates = new HashSet<>();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				if(state.isRejectState()) {
					rejectStates.add(state);
				}
			} 
		});
		
		return rejectStates;
	}	
	
	/**
	 * Merges consecutive outgoing transitions of a state that are of the form\
	 * [a - b] and [b+1 - c] to [a - c]. 
	 * 
	 * Note: This method should be used with caution because it may break the 
	 * working of Matchers in case of overlapping transitions in different states.
	 * Transitions are indexed globally, not per state. Therefore, during the working
	 * of the matcher, because of overlapping, a wrong transition that does not exist
	 * at a state may be chosen.  
	 * 
	 */
	public static Automaton mergeTransitions(final Automaton automaton) {
		
		final State[] startStates = new State[1];
		
		final Map<State, State> newStates = new HashMap<>();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {

			@Override
			public void visit(State state) {
				State newState;
				if(state.isFinalState()) {
					newState = new State(true);
				} else {
					newState = new State();					
				}
				newStates.put(state, newState);
				
				if(automaton.getStartState() == state) {
					startStates[0] = newState;
				}
 			}
		});
		
		AutomatonVisitor.visit(automaton, new VisitAction() {

			@Override
			public void visit(State state) {
				Transition[] t = state.getSortedTransitions();
				int i = 0;
				while(i < t.length) {
					int j = i;
					while(i < t.length - 1 && 
						  t[i + 1].getStart() == t[i].getEnd() + 1 &&
						  t[i].getDestination() == t[i + 1].getDestination()) {
						i++;
					}
					State newState = newStates.get(state);
					newState.addTransition(new Transition(t[j].getStart(), t[i].getEnd(), newStates.get(t[j].getDestination())));
					i++;
				}
			}
		});
		
		return new Automaton(startStates[0]);
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

		// 0. creating new states for each state of the original automaton
		final Map<State, State> newStates = new HashMap<>();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				newStates.put(state, new State());
			}
		});
		
		// 1. creating a new start state and adding epsilon transitions to the final
		// states of the original automata
		State startState = new State();
		
		for(State finalState : automaton.getFinalStates()) {
			startState.addTransition(Transition.epsilonTransition(newStates.get(finalState)));
		}
		
		// 2. Reversing the transitions
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(Transition t : state.getTransitions()) {
					newStates.get(t.getDestination()).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(state)));
				}
			}
		});
		
		// 2. making the start state final
		newStates.get(automaton.getStartState()).setFinalState(true);
		 
		return new Automaton(startState);
	}
	
	/**
	 * Union(a1, a2) = a1 | a2 
	 */
	public static Automaton union(Automaton a1, Automaton a2) {
		return union(new Automaton[] {a1, a2});
	}
	
	public static Automaton union(Iterable<Automaton> automatons) {
		
		State startState = new State();
		State finalState = new State(true);
		
		for(Automaton automaton : automatons) {
			automaton = automaton.copy();
			startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
			
			Set<State> finalStates = automaton.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.epsilonTransition(finalState));				
			}
		}
		
		return new Automaton(startState);
	}
	
	public static Automaton union(Automaton...automatons) {
		return union(Arrays.asList(automatons));
	}
	
	/**
	 *  A state in the resulting intersection automata is final
	 *  if all its composing states are final. 
	 * 
	 */
	public static Automaton intersection(Automaton a1, Automaton a2) {
		
		State startState = null;
		
		Map<Tuple<Integer, Integer>, State> map = product(a1, a2);
		
		for(Entry<Tuple<Integer, Integer>, State> e : map.entrySet()) {
			int i = e.getKey().getFirst();
			int j = e.getKey().getSecond();
			State state = e.getValue();
			
			State state1 = a1.getState(i);
			State state2 = a2.getState(j);
			
			if(state1.isFinalState() && state2.isFinalState()) {
				state.setFinalState(true);
			}
			
			if(state1 == a1.getStartState() && state2 == a2.getStartState()) {
				startState = state;
			}
		}
		
		return new Automaton(startState);
	}
	
	/**
	 *  A state in the resulting intersection automata is final
	 *  if the first state is final but the second one is not. 
	 * 
	 */
	public static Automaton difference(Automaton a1, Automaton a2) {
		
		Map<Integer, Set<Action>> transitionActionsMap = new HashMap<>();
		Set<Transition> transitions = getAllTransitions(a1);
		transitions.addAll(getAllTransitions(a2));
		int[] intervals = getIntervals(transitions, transitionActionsMap);
		
		a1 = makeComplete(a1, intervals, transitionActionsMap);
		a2 = makeComplete(a2, intervals, transitionActionsMap);
		
		Map<Tuple<Integer, Integer>, State> map = product(a1, a2);
		
		State startState = null;
		
		for(Entry<Tuple<Integer, Integer>, State> e : map.entrySet()) {
			int i = e.getKey().getFirst();
			int j = e.getKey().getSecond();
			State state = e.getValue();
			
			State state1 = a1.getState(i);
			State state2 = a2.getState(j);
			
			
			if(state1.isRejectState() || state2.isRejectState()) {
				state.setRejectState(true);
			}
			else if (state1.isFinalState() && !state2.isFinalState()) {
				state.setFinalState(true);
			}
			else if (state1.isFinalState() && state2.isFinalState()) {
				state.setRejectState(true);
			}
			
			if(state1 == a1.getStartState() && state2 == a2.getStartState()) {
				startState = state;
			}
		}
		
		return new Automaton(startState);
	}
	
	/**
	 * Produces the Cartesian product of the states of an automata.
	 */
	private static Map<Tuple<Integer, Integer>, State> product(Automaton a1, Automaton a2) {
		
		a1.determinize();
		a2.determinize();
		
		State[] states1 = a1.getAllStates();
		State[] states2 = a2.getAllStates();
		
		Map<Tuple<Integer, Integer>, State> newStates = new HashMap<>();
		
		for(int i = 0; i < states1.length; i++) {
			for(int j = 0; j < states2.length; j++) {
				newStates.put(Tuple.of(i, j), new State());
			}
		}
		
		Set<Transition> transitions = getAllTransitions(a1);
		transitions.addAll(getAllTransitions(a2));
		
		Map<Integer, Set<Action>> map = new HashMap<>();
		int[] intervals = getIntervals(transitions, map);
		
		for(int i = 0; i < states1.length; i++) {
			for(int j = 0; j < states2.length; j++) {
				
				State state = newStates.get(Tuple.of(i, j));
				State state1 = states1[i];
				State state2 = states2[j];
				
				for(int t = 0; t < intervals.length - 1; t++) {
					Set<State> reachableStates1 = state1.move(intervals[t]);
					Set<State> reachableStates2 = state2.move(intervals[t]);
					
					// If one of the states is a dead state
					if(reachableStates1.size() == 0 || reachableStates2.size() == 0) {
						continue;
					}
					
					// Automatons are already determinized.
					assert reachableStates1.size() == 1; 
					assert reachableStates2.size() == 1;

					State s1 = reachableStates1.iterator().next();
					State s2 = reachableStates2.iterator().next();
					state.addTransition(new Transition(intervals[t], 
													   intervals[t + 1] - 1, 
													   newStates.get(Tuple.of(s1.getId(), s2.getId())),
													   map.get(intervals[t])));
				}
				
			}
		}
		
		return newStates;
	}
	
	public static Automaton copy(final Automaton automaton) {
		
		final Map<State, State> newStates = new HashMap<>();
		
		final State[] startState = new State[1];
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				State newState = new State();
				
				newState.addRegularExpressions(state.getRegularExpressions());
				
				newStates.put(state, newState);
				if(state.isFinalState()) {
					newState.setFinalState(true);
				}
				if(state.isRejectState()) {
					newState.setRejectState(true);
				}
				if(state == automaton.getStartState()) {
					startState[0] = newState;
				}
			}
		});
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(Transition transition : state.getTransitions()) {
					State newState = newStates.get(state);
					newState.addTransition(new Transition(transition.getStart(), 
														  transition.getEnd(), 
														  newStates.get(transition.getDestination()),
														  transition.getActions()));
				}
			}
		});
		
		return new Automaton(startState[0]);
	}
	
	public static Automaton or(Automaton...automatons) {
		return or(Arrays.asList(automatons));
	}
	
	/**
	 * Creates an automaton which is the result of applying the or operator to the list
	 * of automatons. 
	 */
	public static Automaton or(List<Automaton> automatons) {
		State startState = new State();
		
		for(Automaton a : automatons) {
			Automaton c = a.copy();
			startState.addTransition(Transition.epsilonTransition(c.getStartState()));
		}
		
		return new Automaton(startState);
	}
	
	
	/**
	 * Returns true if there is a sentence accepted by the automaton a1 which is is a prefix of
	 * a sentence accepted by the automaton a2. 
	 */
	public static boolean prefix(final Automaton a1, final Automaton a2) {
		Automaton copy = makeAllStatesFinal(a2);
		return !copy.intersection(a1).isLanguageEmpty();
	}
	
	/**
	 * Returns a copy of the given automaton where all its states are set to final. 
	 */
	private static Automaton makeAllStatesFinal(final Automaton a) {
		
		Automaton copy = a.copy();
		
		AutomatonVisitor.visit(copy, new VisitAction() {
			
			@Override
			public void visit(State state) {
				state.setFinalState(true);
			}
		});
		
		return copy;
	}
	
	/**
	 * Makes the transition function complete, i.e., from each state 
	 * there will be all outgoing transitions.
	 */
	public static Automaton makeComplete(Automaton a, final int[] intervals, final Map<Integer, Set<Action>> map) {
		
		a.determinize();
		a = a.copy();
		
		final State dummyState = new State();
		
		AutomatonVisitor.visit(a.getStartState(), new VisitAction() {
			
			@Override
			public void visit(State state) {
				for(int i = 0; i < intervals.length; i++) {
					if(!state.hasTransition(intervals[i])) {
						if(i + 1 == intervals.length) {
							if (map.get(intervals[i] - 1) == null) {
								System.out.println("");
							}
							state.addTransition(new Transition(intervals[i] - 1, intervals[i] - 1, dummyState, map.get(intervals[i] - 1)));
						} else {
							if (map.get(intervals[i]) == null) {
								System.out.println("");
							}
							state.addTransition(new Transition(intervals[i], intervals[i + 1] - 1 , dummyState, map.get(intervals[i])));
						}
					}
				}
			}
		});
		
		return new Automaton(a.getStartState());
	}
}
