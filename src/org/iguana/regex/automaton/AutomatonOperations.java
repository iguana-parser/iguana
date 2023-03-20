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

    public static Automaton makeDeterministic(State start, CharRange[] alphabet) {

        Set<Set<State>> visitedStates = new HashSet<>();
        Deque<Set<State>> processList = new ArrayDeque<>();

        Set<State> initialState = new HashSet<>();
        initialState.add(start);
        initialState = epsilonClosure(initialState);
        visitedStates.add(initialState);
        processList.add(initialState);

        /*
         * A map from the set of NFA states to the new state in the produced DFA.
         * This map is used for sharing DFA states.
         */
        Map<Set<State>, State> newStatesMap = new HashMap<>();

        State startState = new State();

        newStatesMap.put(initialState, startState);

        while (!processList.isEmpty()) {
            Set<State> stateSet = processList.poll();

            for (CharRange r : alphabet) {
                Set<State> destState = move(stateSet, r);

                if (destState.isEmpty())
                    continue;

                State source = newStatesMap.get(stateSet);
                State dest = newStatesMap.computeIfAbsent(destState, s -> {
                    State state = new State();
                    for (State ds : destState) {
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

        Map<CharRange, List<CharRange>> rangeMap = merge(a1.getAlphabet(), a2.getAlphabet());
        convertToNonOverlapping(a1, rangeMap);
        convertToNonOverlapping(a2, rangeMap);

        Set<CharRange> values = rangeMap.values().stream()
                                                      .flatMap(List::stream)
                                                      .collect(Collectors.toSet());

        a1 = makeComplete(a1, values);
        a2 = makeComplete(a2, values);

        return product(a1, a2, values, op);
    }

    /**
     * Produces the Cartesian product of the states of an automata.
     */
    private static Automaton product(Automaton a1, Automaton a2, Set<CharRange> values, Op op) {

        State[] states1 = a1.getStates();
        State[] states2 = a2.getStates();

        State[][] newStates = new State[states1.length][states2.length];

        State startState = null;

        for (int i = 0; i < states1.length; i++) {
            for (int j = 0; j < states2.length; j++) {

                State state = getState(newStates, i, j);
                State state1 = states1[i];
                State state2 = states2[j];

                for (CharRange r : values) {
                    State dest1 = state1.getState(r);
                    State dest2 = state2.getState(r);
                    if (dest1 != null && dest2 != null) {
                        State dest = getState(newStates, dest1.getId(), dest2.getId());
                        state.addTransition(new Transition(r, dest));
                    }
                }

                if (op.execute(state1, state2)) {
                    state.setStateType(StateType.FINAL);
                }

                if (state1 == a1.getStartState() && state2 == a2.getStartState()) {
                    startState = state;
                }
            }
        }

        return Automaton.builder(startState).build();
    }

    private static State getState(State[][] newStates, int i, int j) {
        State state = newStates[i][j];
        if (state == null) {
            state = new State();
            newStates[i][j] = state;
        }
        return state;
    }

    public static void convertToNonOverlapping(Automaton a, Map<CharRange, List<CharRange>> rangeMap) {
        for (State state : a.getStates()) {
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
    }


    public static Automaton makeComplete(Automaton automaton, Iterable<CharRange> alphabet) {

        State dummyState = new State();
        alphabet.forEach(r -> dummyState.addTransition(new Transition(r, dummyState)));

        for (State state : automaton.getStates()) {
            for (CharRange r : alphabet) {
                if (!state.hasTransition(r)) {
                    state.addTransition(new Transition(r, dummyState));
                }
            }
        }

        return Automaton.builder(automaton.getStartState()).build();
    }

    private static Map<CharRange, List<CharRange>> merge(CharRange[] alphabet1, CharRange[] alphabet2) {
        List<CharRange> alphabets = new ArrayList<>();
        for (CharRange r : alphabet1) { alphabets.add(r); }
        for (CharRange r : alphabet2) { alphabets.add(r); }

        return CharacterRanges.toNonOverlapping(alphabets);
    }


    public static Automaton minimize(Automaton automaton) {
        if (automaton.isMinimized())
            return automaton;

        return minimize(automaton.getAlphabet(), automaton.getStates(), automaton.getStartState());
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
        final Map<State, State> newStates = new HashMap<>();

        for (State s : automaton.getStates()) {
            newStates.put(s, new State());
        }

        // 2. Creating a new start state and adding epsilon transitions to the final
        // states of the original automata
        State startState = new State();

        for (State finalState : automaton.getFinalStates()) {
            startState.addEpsilonTransition(newStates.get(finalState));
        }

        // 3. Reversing the transitions
        for (State state : automaton.getStates()) {
            for (Transition t : state.getTransitions()) {
                newStates.get(t.getDestination()).addTransition(new Transition(t.getRange(), newStates.get(state)));
            }
        }

        // 4. Making the start state final
        newStates.get(automaton.getStartState()).setStateType(StateType.FINAL);

        return Automaton.builder(startState).build();
    }

    /**
     *
     * Note: unreachable states are already removed as we gather the states
     * reachable from the start state of the given NFA.
     *
     */
    public static Automaton minimize(CharRange[] alphabet, State[] states, State startState) {

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
                if (states[i].isFinalState() && !states[j].isFinalState()) {
                    table[i][j] = EPSILON;
                }
                if (states[j].isFinalState() && !states[i].isFinalState()) {
                    table[i][j] = EPSILON;
                }

                // Differentiate between final states
                if (states[i].isFinalState() &&
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

                                // If both states i and j have no outgoing transitions on the interval t, continue with
                                // the next transition.
                                if (q1 == null && q2 == null) {
                                    continue;
                                }

                                // If the transition t can be applied on state i but not on state j, two states are
                                // disjoint. Continue with the next pair of states.
                                if ((q1 == null && q2 != null) || (q2 == null && q1 != null)) {
                                    table[i][j] = t;
                                    changed = true;
                                    break;
                                }

                                if (q1.getId() == q2.getId()) {
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

                    if (partitionI == null && partitionJ == null) {
                        Set<State> set = new HashSet<>();
                        set.add(stateI);
                        set.add(stateJ);
                        partitionsMap.put(stateI, set);
                        partitionsMap.put(stateJ, set);
                    }
                    else if (partitionI == null && partitionJ != null) {
                        partitionJ.add(stateI);
                        partitionsMap.put(stateI, partitionJ);
                    }
                    else if (partitionJ == null && partitionI != null) {
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

        HashSet<Set<State>> partitions = new HashSet<>(partitionsMap.values());

        State newStartState = null;

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

                if (set.contains(startState)) {
                    newStartState = newState;
                }
                if (state.isFinalState()) {
                    newState.setStateType(StateType.FINAL);
                }
                newStates.put(state, newState);
            }
        }

        for (State state : states) {
            for (Transition t : state.getTransitions()) {
                newStates.get(state).addTransition(new Transition(t.getStart(), t.getEnd(),
                    newStates.get(t.getDestination())));
            }
        }

        return Automaton.builder(newStartState).build();
    }


    private static Set<State> epsilonClosure(Set<State> states) {
        Set<State> newStates = new HashSet<>(states);

        for (State state : states) {
            Set<State> s = state.getEpsilonSates();
            if (!s.isEmpty()) {
                newStates.addAll(s);
                newStates.addAll(epsilonClosure(s));
            }
        }

        return newStates;
    }

    private static Set<State> move(Set<State> state, CharRange r) {
        Set<State> result = new HashSet<>();
        for (State s: state) {
            State dest = s.getState(r);
            if (dest != null) {
                result.add(dest);
            }
        }

        return epsilonClosure(result);
    }

    @FunctionalInterface
    private interface Op {
        boolean execute(State s1, State s2);
    }

}
