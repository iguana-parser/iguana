package org.jgll.regex;

import java.util.HashSet;
import java.util.Set;

import org.jgll.util.Input;

public class LargeIntervalMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = 1L;

	public LargeIntervalMatcher(int[][] transitionTable, 
								boolean[] endStates, 
								int startStateId, 
								int[] intervals, 
								Set<StateAction>[] matchActions) {
		super(transitionTable, endStates, startStateId, intervals, matchActions);
	}

	@Override
	public boolean match(Input input) {
		return match(input, 0) == input.length() - 1;
	}

	@Override
	protected int getTransitionId(int c) {
		
		if(c < intervals[0] || c > intervals[intervals.length - 1]) {
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
	
	@Override
	public Matcher copy() {
		@SuppressWarnings("unchecked")
		Set<StateAction>[] actions = new Set[matchActions.length];
		for(int i = 0; i < actions.length; i++) {
			actions[i] = new HashSet<>(matchActions[i]);
		}
		LargeIntervalMatcher matcher = new LargeIntervalMatcher(transitionTable, endStates, startStateId, intervals, actions);
		return matcher;
	}

}
