package org.jgll.regex.matcher;

import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;

public class DFABackwardsMatcher extends DFAMatcher {

	public DFABackwardsMatcher(Automaton automaton) {
		super(automaton.builder().reverse().build());
	}

	@Override
	public int match(Input input, int inputIndex) {
		
		if (inputIndex == 0)
			return -1;
		
		int length = 0;
		int maximumMatched = -1;
		int state = start;
		
		if (finalStates[state])
			maximumMatched = 0;
		
		for (int i = inputIndex - 1; i >= 0; i--) {
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
