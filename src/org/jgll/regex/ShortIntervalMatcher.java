package org.jgll.regex;

import java.util.List;

public class ShortIntervalMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = 1L;

	private final int[] transitionIds;
	
	private final int minimum;
	
	private final int maximum;

	public ShortIntervalMatcher(int[][] transitionTable, 
								boolean[] endStates, 
								int startStateId, 
								int[] intervals,
								List<MatchAction>[] matchActions) {
		
		super(transitionTable, endStates, startStateId, intervals, matchActions);
		
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
