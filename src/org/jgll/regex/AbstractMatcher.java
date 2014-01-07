package org.jgll.regex;

import org.jgll.util.Input;

public abstract class AbstractMatcher implements Matcher {
	
	private final int[][] transitionTable;
	
	private final boolean[] endStates;

	private final int startStateId;
	
	protected final int[] intervals;
	
	private int id;

	public AbstractMatcher(int[][] transitionTable, boolean[] endStates, int startStateId, int[] intervals) {
		this.transitionTable = transitionTable;
		this.endStates = endStates;
		this.startStateId = startStateId;
		this.intervals = intervals;
	}

	@Override
	public boolean match(Input input) {
		return match(input, 0) == input.length() - 1;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int match(Input input, int inputIndex) {
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		if(input.isEmpty() && endStates[stateId]) {
			return 0;
		}
		
		for(int i = inputIndex; i < input.length() - 1; i++) {
			int transitionId = getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				break;
			}
			
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				maximumMatched = length;
			}
		}
		
		return maximumMatched;
	}
	
	protected abstract int getTransitionId(int c);

}
