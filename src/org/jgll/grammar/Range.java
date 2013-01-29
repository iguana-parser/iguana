package org.jgll.grammar;

public class Range extends Terminal {

	private final int start;
	private final int end;

	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	@Override
	public boolean match(int i) {
		return false;
	}
	
}
