package org.jgll.regex.matcher;

import java.io.Serializable;

import org.jgll.regex.automaton.State;

public class LargeIntervalTransitions implements Transitions, Serializable {

	private static final long serialVersionUID = 1L;

	private final int[] transitionIds;
	
	private final int minimum;
	
	private final int maximum;
	
	private int[] intervals;

	public LargeIntervalTransitions(int[] intervals) {
		
		this.intervals = intervals;
		minimum = intervals[0];
		maximum = intervals[intervals.length - 1];
		transitionIds = new int[maximum - minimum];
		
		int k = 0;
		for(int i = 0; i < intervals.length - 1; i++) {
			for(int j = intervals[i]; j < intervals[i+1]; j++) {
				transitionIds[k++] = i;
			}
		}
	}
	
	@Override
	public int getTransitionId(int v) {
		if(v < minimum || v > maximum) {
			return -1;
		}
		
		return getTransitionId(v, 0, intervals.length - 1);
	}

	@Override
	public State move(int v) {
		return null;
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
