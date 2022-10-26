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
import java.util.stream.Collectors;

public class AutomatonBuilder {
	
	private State startState;
	
	private boolean deterministic;
	
	private boolean minimized;
	
	private CharRange[] alphabet;
	
	private State[] states;

	private Set<State> finalStates;

	/**
	 * From transitions to non-overlapping transitions
	 */
	private Map<CharRange, List<CharRange>> rangeMap;

	public AutomatonBuilder(Automaton automaton) {
		this.deterministic = automaton.isDeterministic();
		this.minimized = automaton.isMinimized();
		this.states = automaton.getStates();
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

	public CharRange[] getAlphabet() {
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

	private static Map<CharRange, List<CharRange>> getRangeMap(State startState) {
		return CharacterRanges.toNonOverlapping(getAllRanges(startState));
	}

	private static CharRange[] getAlphabet(Map<CharRange, List<CharRange>> rangeMap) {
		Set<CharRange> values = rangeMap.values().stream()
				                                      .flatMap(Collection::stream)
				                                      .collect(Collectors.toCollection(LinkedHashSet::new));

		CharRange[] alphabet = new CharRange[values.size()];
		int i = 0;
		for (CharRange r : values) {
			alphabet[i++] = r;
		}
		return alphabet;
	}

	public AutomatonBuilder makeDeterministic() {
		Automaton dfa = AutomatonOperations.makeDeterministic(startState, alphabet);
		this.startState = dfa.getStartState();
		this.finalStates = dfa.getFinalStates();
		this.states = dfa.getStates();
		this.deterministic = true;
		return this;
	}

	public AutomatonBuilder setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
		return this;
	}

	/**
	 *
	 * Note: unreachable states are already removed as we gather the states
	 * reachable from the start state of the given NFA.
	 *
	 */
	public AutomatonBuilder minimize() {
		if (!deterministic)
			makeDeterministic();

		Automaton automaton = AutomatonOperations.minimize(alphabet, states, startState);
		this.startState = automaton.getStartState();
		this.finalStates = automaton.getFinalStates();
		this.states = automaton.getStates();
		this.minimized = true;
		return this;
	}

	public AutomatonBuilder setMinimized(boolean minimized) {
		this.minimized = minimized;
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

	public static BitSet getCharacters(Automaton automaton) {
		final BitSet bitSet = new BitSet();

		AutomatonVisitor.visit(automaton.getStartState(), state -> {
				for (Transition transition : state.getTransitions()) {
					bitSet.set(transition.getStart(), transition.getEnd() + 1);
				}
			});

		return bitSet;
	}

	public static int[] merge(int[] i1, int[] i2) {
		Set<Integer> set = new HashSet<>();

		for (int i = 0; i < i1.length; i++) {
			set.add(i1[i]);
		}

		for (int i = 0; i < i2.length; i++) {
			set.add(i2[i]);
		}

		int i = 0;
		int[] merged = new int[set.size()];
		for (int c : set) {
			merged[i++] = c;
		}

		Arrays.sort(merged);

		return merged;
	}

	private void convertToNonOverlapping(State startState) {
		this.rangeMap = getRangeMap(startState);
		for (State state : states) {
			List<Transition> removeList = new ArrayList<>();
			List<Transition> addList = new ArrayList<>();
			for (Transition transition : state.getTransitions()) {
				if (!transition.isEpsilonTransition()) {
					removeList.add(transition);
					for (CharRange range : rangeMap.get(transition.getRange())) {
						addList.add(new Transition(range, transition.getDestination()));
					}
				}
			}
			state.removeTransitions(removeList);
			state.addTransitions(addList);
		}
		this.alphabet = getAlphabet(rangeMap);
	}

	private static List<CharRange> getAllRanges(State startState) {
		final Set<CharRange> ranges = new HashSet<>();

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


	public State getState(int i) {
		return states[i];
	}
	
	/**
	 * Makes the transition function complete, i.e., from each state 
	 * there will be all outgoing transitions.
	 */
	public AutomatonBuilder makeComplete() {
		
		makeDeterministic();
		
		State dummyState = new State();
		
		AutomatonVisitor.visit(startState, s -> {
			for (CharRange r : alphabet) {
				if (!s.hasTransition(r)) {
					s.addTransition(new Transition(r, dummyState));
				}
			}
		});
			
		return this;
	}

}
