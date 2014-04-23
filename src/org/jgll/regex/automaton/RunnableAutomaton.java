package org.jgll.regex.automaton;

import java.io.Serializable;

import org.jgll.util.Input;

public class RunnableAutomaton implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private RunnableState state;

	public RunnableAutomaton(RunnableState startState) {
		this.state = startState;
	}
	
	public boolean match(Input input) {
		return match(input, 0) == input.length() - 1;
	}

	public boolean match(Input input, int start, int end) {
		return match(input, start) == end - start;
	}
	
	public int match(Input input, int inputIndex) {
		int maximumMatched = -1;

		int length = 0;
		
		RunnableState currentState = state;
		
		loop:
		while (true) {
			
			switch (currentState.getStateType()) {
				case REJECT:
					maximumMatched = -1;
					break loop;
					
				case LOOKAHEAD_ACCEPT:
					maximumMatched = -1;
					break loop;
					
				case LOOKAHEAD_REJECT:
					 maximumMatched = -1;
					 break loop;

				case FINAL:
					maximumMatched = length;
					 
				case NORMAL:
					currentState = currentState.move(input, inputIndex++);
					if (currentState == null) break loop;
					length++;
					break;
			}
		}
		
		return maximumMatched;
	}
	
	public int matchBackwards(Input input, int index) {
		int maximumMatched = -1;
		int length = 0;
		
		RunnableState currentState = state;
		
		while (true) {
			
//			if (currentState.isFinalState()) maximumMatched = length;
//			
//			if (currentState.isRejectState()) maximumMatched = -1;

			currentState = currentState.move(input, index--);
			
			if (currentState == null) break;
			
			length++;
		}
		
		return maximumMatched;
	}
}
