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

	public int run(Input input, int inputIndex) {
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		if(input.isEmpty() && endStates[stateId]) {
			return 0;
		}
		
		for(int i = inputIndex; i < input.length() - 1; i++) {
			int transitionId = getTransitionId(input.charAt(i));
			
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
		
		if(c < intervals[0] || c > intervals[intervals.length -1]) {
			return -1;
		}
		
		return getTransitionId(c, 0, intervals.length - 1);
	}
	
	private int getTransitionId(int c, int start, int end) {
		
		if(end == start ) {
			return c == intervals[end] ? end : -1;
		}		
		
		// Two elements left
		if(end - start == 1) {
			if(intervals[start] <= c && c < intervals[end]) {
				return start;
			} else {
				return -1;
			}
		}
		
		// Three elements left
		if(end - start == 2) {
			if(intervals[start] <= c && c < intervals[end - 1]) {
				return start;
			} 
			else if(intervals[end - 1] <= c && c < intervals[end]) {
				return end - 1;
			} 
			else {
				return -1;
			}
		}
		
		int n = start + (end - start) / 2;
		
		if(intervals[n] <= c && c < intervals[n + 1]) {
			return n;
		} 
		else if (intervals[n - 1] <= c && c < intervals[n]) {
			return n - 1;
		}
		else if(c < intervals[n - 1]) {
			return getTransitionId(c, start, n - 1);
		}
		else if(c >= intervals[n + 1]) {
			return getTransitionId(c, n + 1, end);
		}
		return -1;
	}
}
