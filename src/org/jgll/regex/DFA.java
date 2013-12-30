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
	
	public boolean match(Input input) {
		return run(input, 0) == input.length() - 1;
	}

	public int run(Input input, int index) {
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		while(true) {
			int transitionId = getTransitionId(input.charAt(index));
			
			if(transitionId == -1) {
				return -1;
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
	
	private int getTransitionId(int c) {
		
		if(c < intervals[0] || c > intervals[intervals.length -1] - 1) {
			return -1;
		}
		
		return getTransitionId(c, 0, intervals.length);
	}
	
	private int getTransitionId(int c, int start, int end) {
		
		if(end - start == 0) {
			if(c == intervals[start]) {
				return start;
			} else {
				return -1;
			}
		}
		
		int n = (end - start) / 2;
		
		if(c == intervals[n]) {
			return n;
		} 
		else if(c >= intervals[n] && c <= intervals[n + 1]) {
			return n;
		} 
		else if(c <= intervals[n]) {
			return getTransitionId(c, start, n);
		}
		else if(c >= intervals[n]) {
			return getTransitionId(c, n, end);
		}
		return -1;
	}
}
