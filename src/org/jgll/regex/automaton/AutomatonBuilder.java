package org.jgll.regex.automaton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.grammar.symbol.CharacterRange;

import com.google.common.collect.Multimap;

public class AutomatonBuilder {
	
	private State startState;
	
	private boolean deterministic;
	
	private boolean minimized;
	
	private CharacterRange[] alphabet;
	
	private State[] states;
	
	private Set<State> finalStates;
	
	/**
	 * From transitions to non-overlapping transitions
	 */
	private Multimap<CharacterRange, CharacterRange> rangeMap;
	
	public AutomatonBuilder(Automaton automaton) {
		this.deterministic = automaton.isDeterministic();
		this.minimized = automaton.isMinimized();
		this.states = automaton.getAllStates();
		this.startState = automaton.getStartState();
		this.finalStates = automaton.getFinalStates();
		this.alphabet = automaton.getAlphabet();
		
		if (!deterministic) {
			convertToNonOverlapping(automaton.getStartState());
		}
	}
	
	public AutomatonBuilder(State startState) {
		this.states = getAllStates(startState);
		this.startState = startState;
		this.finalStates = computeFinalStates();
		convertToNonOverlapping(startState);
	}
	
	public Automaton build() {
		setStateIDs();
		return new Automaton(this);
	}
	 
	public CharacterRange[] getAlphabet() {
		return alphabet;
	}
	
	public Set<State> getFinalStates() {
		return finalStates;
	}
	
	public State getStartState() {
		return startState;
	}
	
	public State[] getStates() {
		return states;
	}
	
	public boolean isMinimized() {
		return minimized;
	}
	
	public boolean isDeterministic() {
		return deterministic;
	}
	
	private static Multimap<CharacterRange, CharacterRange> getRangeMap(State startState) {
		return CharacterRange.toNonOverlapping(getAllRanges(startState));
	}
	
	private static CharacterRange[] getAlphabet(State startState, Multimap<CharacterRange, CharacterRange> rangeMap) {
		Set<CharacterRange> values = new LinkedHashSet<>(rangeMap.values());
		CharacterRange[] alphabet = new CharacterRange[values.size()];
		int i = 0;
		for (CharacterRange r : values) {
			alphabet[i++] = r;
		}
		return alphabet;
	}
	
	public AutomatonBuilder makeDeterministic() {
		
		if (deterministic)
			return this;

		Set<Set<State>> visitedStates = new HashSet<>();
		Deque<Set<State>> processList = new ArrayDeque<>();
		
		Set<State> initialState = new HashSet<>();
		initialState.add(startState);
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

			Set<State> destState = new HashSet<>();
			for (CharacterRange r : alphabet) {
				for (State state : stateSet) {
					State dest = state.getState(r);
					if (dest != null)
						destState.add(dest);
				}
				
				destState = epsilonClosure(destState);
				if (!destState.isEmpty()) {
					State source = newStatesMap.get(stateSet);
					State dest = newStatesMap.computeIfAbsent(destState, s -> new State());
					source.addTransition(new Transition(r, dest));
				}
			}
							
			if (!visitedStates.contains(destState)) {
				visitedStates.add(destState);
				processList.add(destState);
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
				
		deterministic = true;
		
		return this;
	}
		
	private static Set<State> epsilonClosure(Set<State> states) {
		Set<State> newStates = new HashSet<>(states);
		
		for(State state : states) {
			Set<State> s = state.getEpsilonStates();
			if(!s.isEmpty()) {
				newStates.addAll(s);
				newStates.addAll(epsilonClosure(s));
			}
		}
		
		return newStates;
	}
	
	/**
	 * 
	 * Note: unreachable states are already removed as we gather the states
	 * reachable from the start state of the given NFA.
	 * 
	 * @param automaton
	 * @return
	 */
	public AutomatonBuilder minimize() {
		
		if (minimized)
			return this;
		
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
		
		while(changed) {
			changed = false;

				for (int i = 0; i < table.length; i++) {
					for (int j = 0; j < i; j++) {
						
						// If two states i and j are distinct
						if(table[i][j] == EMPTY) {
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
		
		for(State state : states) {
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
				
				if(startState == state) {
					startState = newState;
				}
				if(state.isFinalState()) {
					newState.setStateType(StateType.FINAL);
				}
				newStates.put(state, newState);
			}
		}
		
		for(State state : states) {
			for(Transition t : state.getTransitions()) {
				newStates.get(state).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(t.getDestination())));;				
			}
		}
		
		return this;
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
	
	private void convertToNonOverlapping(State startState) {
		this.rangeMap = getRangeMap(startState);
		for (State state : states) {
			for (Transition transition : state.getTransitions()) {
				state.removeTransition(transition);
				for (CharacterRange range : rangeMap.get(transition.getRange())) {
					state.addTransition(new Transition(range, transition.getDestination()));
				}
			}
		}
		this.alphabet = getAlphabet(startState, rangeMap);
	}
		
	private static List<CharacterRange> getAllRanges(State startState) {
		final Set<CharacterRange> ranges = new HashSet<>();

		AutomatonVisitor.visit(startState, state -> {
				for (Transition transition : state.getTransitions()) {
					if (!transition.isEpsilonTransition()) {
						ranges.add(transition.getRange());
					}
				}
			});
		
		return new ArrayList<>(ranges);
	}
 	
	public static int getCountStates(Automaton automaton) {
		final int[] count = new int[1];
		
		AutomatonVisitor.visit(automaton, s -> count[0]++);
		
		return count[0];
	}
	
	private static State[] getAllStates(State startState) {
		final Set<State> set = new LinkedHashSet<>();
		AutomatonVisitor.visit(startState, s -> set.add(s));
		
		State[] states = new State[set.size()];
		int i = 0;
		for (State s : set) {
			states[i++] = s; 
		}
		return states;
	}
	
	public void setStateIDs() {
		for (int i = 0; i < states.length; i++) {
			states[i].setId(i);
		}
	}
	
	private Set<State> computeFinalStates() {
		
		final Set<State> finalStates = new HashSet<>();
		
		AutomatonVisitor.visit(startState, s -> {
				if (s.isFinalState()) {
					finalStates.add(s);
				}
			});
		
		return finalStates;
	}
	
	/**
	 * Creates the reverse of the given automaton. A reverse automaton 
	 * accept the reverse language accepted by the original automaton. To construct
	 * a reverse automaton, all final states of the original automaton are becoming 
	 * start states, transitions are reversed and the start state becomes the
	 * only final state.
	 * 
	 */
	public AutomatonBuilder reverse() {

		// 0. creating new states for each state of the original automaton
		final Map<State, State> newStates = new HashMap<>();
		
		AutomatonVisitor.visit(startState, state -> newStates.put(state, new State()));
		
		// 1. creating a new start state and adding epsilon transitions to the final
		// states of the original automata
		State startState = new State();
		
		for(State finalState : finalStates) {
			startState.addTransition(Transition.epsilonTransition(newStates.get(finalState)));
		}
		
		// 2. Reversing the transitions
		AutomatonVisitor.visit(startState, state -> {
				for(Transition t : state.getTransitions()) {
					newStates.get(t.getDestination()).addTransition(new Transition(t.getStart(), t.getEnd(), newStates.get(state)));
				}
		});
		
		// 2. making the start state final
		newStates.get(startState).setStateType(StateType.FINAL);
		 
		return this;
	}
	
	/**
	 *  A state in the resulting intersection automata is final
	 *  if all its composing states are final. 
	 * 
	 */
	public AutomatonBuilder intersect(Automaton other) {
		
		State startState = null;
		
		State[][] product = product(other);
		
		for (int i = 0; i < product.length; i++) {
			 for (int j = 0; j < product[i].length; j++) {
				State state = product[i][j];
				
				State state1 = getState(i);
				State state2 = other.getState(j);
				
				if (state1.isFinalState() && state2.isFinalState()) {
					state.setStateType(StateType.FINAL);
				}
				
				if (state1 == startState && state2 == other.getStartState()) {
					startState = state;
				}
			 }
		}
		
		return this;
	}
	
	public AutomatonBuilder union(Automaton...automatons) {
		throw new UnsupportedOperationException();
	}
	
	public AutomatonBuilder difference(Automaton...automaton) {
		throw new UnsupportedOperationException();
	}
		
	/**
	 * Produces the Cartesian product of the states of an automata.
	 */
	private State[][] product(Automaton a2) {
		
		State[] states1 = states;
		State[] states2 = a2.getAllStates();
		
		State[][] newStates = new State[states1.length][states2.length];
		
		for(int i = 0; i < states.length; i++) {
			for(int j = 0; j < states2.length; j++) {
				newStates[i][j] = new State();
			}
		}

		CharacterRange[] joinAlphabet = merge(alphabet, a2.getAlphabet()); 
		
		for(int i = 0; i < states1.length; i++) {
			for(int j = 0; j < states2.length; j++) {
				
				State state = newStates[i][j];
				State state1 = states1[i];
				State state2 = states2[j];
				
				for (CharacterRange r : joinAlphabet) {
					State s1 = state1.getState(r);
					State s2 = state2.getState(r);
					state.addTransition(new Transition(r, newStates[s1.getId()][s2.getId()]));
				}
			}
		}
		
		return newStates;
	}
	
	public State getState(int i) {
		return states[i];
	}
	
	public CharacterRange[] merge(CharacterRange[] alphabet1, CharacterRange[] alphabet2) {
		Set<CharacterRange> alphabets = new HashSet<>();
		for (CharacterRange r : alphabet1) { alphabets.add(r); }
		for (CharacterRange r : alphabet2) { alphabets.add(r); }
		CharacterRange[] merged = new CharacterRange[alphabets.size()];
		int i = 0;
		for (CharacterRange r : alphabets) { merged[i++] = r; };
		return merged;
	}
	
	/**
	 * Makes the transition function complete, i.e., from each state 
	 * there will be all outgoing transitions.
	 */
	public AutomatonBuilder makeComplete() {
		
		makeDeterministic();
		
		State dummyState = new State();
		
		AutomatonVisitor.visit(startState, s -> {
			for (CharacterRange r : alphabet) {
				if (!s.hasTransition(r)) {
					s.addTransition(new Transition(r, dummyState));
				}
			}
		});
			
		return this;
	}

}
