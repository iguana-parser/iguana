package org.jgll.regex;

import org.jgll.util.Input;


public class DFA {
	
	private final int[][] transitionTable;
	
	private final boolean[] endStates;

	private final int startStateId;
	
	private final int[] intervals;

	public DFA(int[][] transitionTable, boolean[] endStates, int startStateId, int[] intervals) {
		this.transitionTable = transitionTable;
		this.endStates = endStates;
		this.startStateId = startStateId;
		this.intervals = intervals;
	}

	public int run(Input input, int index) {
		int length = 0;

		int stateId = startStateId;
		
		while(true) {
			stateId = transitionTable[stateId][getTransitionId(index)];
			if(stateId == -1) {
				break;
			}

			if(endStates[stateId]) {
				length++;
			}
		}
		
		return length;
	}
	
	private int getTransitionId(int inputIndex) {
		return getTransitionId(inputIndex, 0, intervals.length);
	}
	
	private int getTransitionId(int inputIndex, int start, int end) {
		
		int n = (start - end) / 2;
		
		if(inputIndex == intervals[n]) {
			return n;
		} 
		else if(inputIndex >= intervals[n] && inputIndex <= intervals[n + 1]) {
			return n;
		} 
		else if(inputIndex <= intervals[n]) {
			return getTransitionId(inputIndex, start, n);
		}
		else if(inputIndex >= intervals[n]) {
			return getTransitionId(inputIndex, n, end);
		}
		return 0;
	}
}
