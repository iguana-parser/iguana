package org.jgll.grammar;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Range extends Terminal {
	
	private static final long serialVersionUID = 1L;

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
		return start <= i  && i <= end;
	}
	
	@Override
	public String toString() {
		return "[" + start + "-" + end + "]";
	}

	@Override
	public String getMatchCode() {
		return "(I[ci] >= " + start + " && I[ci] <= " + end + ")";
	}
	
}
