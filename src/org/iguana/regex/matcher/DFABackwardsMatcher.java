package org.iguana.regex.matcher;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.util.Input;

public class DFABackwardsMatcher extends DFAMatcher {

	public DFABackwardsMatcher(Automaton automaton) {
		super(AutomatonOperations.reverse(automaton));
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
