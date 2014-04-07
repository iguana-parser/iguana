package org.jgll.regex.matcher;

import java.io.Serializable;

public class ShortIntervalTransitions implements Transitions, Serializable {
	
	private static final long serialVersionUID = 1L;

	private final int[] transitionIds;
	
	private final int minimum;
	
	private final int maximum;

	public ShortIntervalTransitions(int[] intervals) {
		
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
		if(v < minimum || v > maximum - 1) {
			return -1;
		}
		
		return transitionIds[v - minimum];
	}

}
