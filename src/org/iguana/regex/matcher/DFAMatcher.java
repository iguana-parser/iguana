package org.iguana.regex.matcher;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.Transition;
import org.iguana.util.Input;
import org.iguana.util.collections.IntRangeTree;

public class DFAMatcher implements Matcher {

	protected final IntRangeTree[] table;

	protected final boolean[] finalStates;
	
	protected final int start;

	public DFAMatcher(Automaton automaton) {
		automaton = AutomatonOperations.makeDeterministic(automaton);

		table = new IntRangeTree[automaton.getCountStates()];
		for (int i = 0; i < table.length; i++) {
			table[i] = new IntRangeTree();
		}

		finalStates = new boolean[automaton.getStates().length];
		for (State state : automaton.getStates()) {
			
			for (Transition transition : state.getTransitions()) {
				table[state.getId()].insert(transition.getRange(), transition.getDestination().getId());
			}

			finalStates[state.getId()] = state.isFinalState();
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
