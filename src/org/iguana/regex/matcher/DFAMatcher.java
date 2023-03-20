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

package org.iguana.regex.matcher;

import org.iguana.regex.EOF;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.Transition;
import org.iguana.util.Tuple;
import org.iguana.utils.collections.rangemap.IntRangeMap;
import org.iguana.utils.collections.rangemap.RangeMapBuilder;
import org.iguana.utils.input.Input;

import java.util.HashMap;
import java.util.Map;

public class DFAMatcher implements Matcher {

    public static final int ERROR_STATE = -2;

    protected IntRangeMap[] table;

    protected final boolean[] finalStates;

    protected final int start;

    protected final Map<Integer, State> finalStatesMap;

    private int finalStateId;

    public DFAMatcher(RegularExpression regex) {
        this(regex.getAutomaton());
    }

    public DFAMatcher(Automaton automaton) {
        automaton = AutomatonOperations.makeDeterministic(automaton);
        finalStatesMap = new HashMap<>();

        finalStates = new boolean[automaton.getStates().length];

        int size = automaton.getCountStates();
        table = new IntRangeMap[size];

        for (int i = 0; i < size; i++) {
            RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
            State state = automaton.getStates()[i];
            for (Transition transition : state.getTransitions()) {
                builder.put(transition.getRange(), transition.getDestination().getId());
            }
            table[i] = builder.buildIntRangeMap();

            finalStates[state.getId()] = state.isFinalState();
            if (state.isFinalState()) {
                finalStatesMap.put(state.getId(), state);
            }
        }

        this.start = automaton.getStartState().getId();
    }

    @Override
    public int match(Input input, int inputIndex) {
        int length = 0;
        int maximumMatched = -1;
        int state = start;
        finalStateId = -1;

        if (finalStates[state]) {
            maximumMatched = 0;
            finalStateId = state;
        }

        for (int i = inputIndex; i < input.length(); i++) {
            state = table[state].get(input.charAt(i));

            if (state == ERROR_STATE)
                break;

            length++;

            if (finalStates[state]) {
                maximumMatched = length;
                finalStateId = state;
            }
        }

        return maximumMatched;
    }

    public RegularExpression getMatchedRegularExpression() {
        if (finalStateId == -1) {
            throw new IllegalStateException("This method should be called after a successful match.");
        }
        if (finalStatesMap.get(finalStateId).getRegularExpressions().isEmpty()) {
            return EOF.getInstance();
        }

        int min = Integer.MAX_VALUE;
        RegularExpression matchedRegularExpression = null;
        for (Tuple<RegularExpression, Integer> t : finalStatesMap.get(finalStateId).getRegularExpressions()) {
            if (t.getSecond() < min) {
                min = t.getSecond();
                matchedRegularExpression = t.getFirst();
            }
        }
        return matchedRegularExpression;
    }

}
