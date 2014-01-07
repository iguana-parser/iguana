package org.jgll.regex;

public class ShortIntervalMatcher extends AbstractMatcher {
	
	private final int[] transitionIds;
	
	private final int minimum;
	
	private final int maximum;

	public ShortIntervalMatcher(int[][] transitionTable, boolean[] endStates, int startStateId, int[] intervals) {
		super(transitionTable, endStates, startStateId, intervals);
		
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
	protected int getTransitionId(int c) {
		
		if(c < minimum || c > maximum - 1) {
			return -1;
		}
		
		return transitionIds[c - minimum];
	}

}
