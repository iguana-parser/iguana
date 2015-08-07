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

import org.iguana.regex.RegularExpression;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.Transition;
import org.iguana.util.Input;
import org.iguana.util.collections.rangemap.AVLIntRangeTree;
import org.iguana.util.collections.rangemap.ArrayIntRangeTree;
import org.iguana.util.collections.rangemap.IntRangeTree;

public class DFAMatcher implements Matcher {

	protected final IntRangeTree[] table;

	protected final boolean[] finalStates;

	protected final int start;

	public DFAMatcher(RegularExpression regex) {
		this(regex.getAutomaton());
	}

	public DFAMatcher(Automaton automaton) {
		automaton = AutomatonOperations.makeDeterministic(automaton);

		IntRangeTree[] tmp = new AVLIntRangeTree[automaton.getCountStates()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = new AVLIntRangeTree();
		}

		finalStates = new boolean[automaton.getStates().length];
		for (State state : automaton.getStates()) {

			for (Transition transition : state.getTransitions()) {
				tmp[state.getId()].insert(transition.getRange(), transition.getDestination().getId());
			}

			finalStates[state.getId()] = state.isFinalState();
		}
		
		table = new ArrayIntRangeTree[automaton.getCountStates()];
		for (int i = 0; i < tmp.length; i++) {
			table[i] = new ArrayIntRangeTree(tmp[i]);
		}

		this.start = automaton.getStartState().getId();
	}

	@Override
	public int match(Input input, int inputIndex) {

		int length = 0;
		int maximumMatched = -1;
		int state = start;

		if (finalStates[state])
			maximumMatched = 0;

		for (int i = inputIndex; i < input.length(); i++) {
			state = table[state].get(input.charAt(i));

			if (state == -1)
				break;

			length++;

			if (finalStates[state])
				maximumMatched = length;
		}

		return maximumMatched;
	}

}
