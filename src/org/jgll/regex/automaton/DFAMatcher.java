package org.jgll.regex.automaton;

import org.jgll.regex.Matcher;
import org.jgll.util.Input;
import org.jgll.util.collections.IntRangeTree;

public class DFAMatcher implements Matcher {

	private final IntRangeTree[] table;
	
	private final int start;

	public DFAMatcher(Automaton automaton) {
		
		if (!automaton.isDeterministic()) 
			throw new RuntimeException("The automaton should be deterministic.");

		table = new IntRangeTree[automaton.getCountStates()];
		for (int i = 0; i < table.length; i++) {
			table[i] = new IntRangeTree();
		}

		for (State state : automaton.getAllStates()) {
			for (Transition transition : state.getTransitions()) {
				table[state.getId()].insert(transition.getRange(), transition.getDestination().getId());
			}
		}
		
		this.start = automaton.getStartState().getId();
	}
	
	@Override
	public int match(Input input, int inputIndex) {
		
		int length = 0;
		int state = start;
		
		for (int i = inputIndex; i < input.length(); i++) {
			state = table[state].get(input.charAt(i));

			if (state == -1)
				break;
			
			length++;
		}
		
		return length;
	}
	
}
