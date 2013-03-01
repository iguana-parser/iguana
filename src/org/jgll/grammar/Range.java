package org.jgll.grammar;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Range implements Terminal {
	
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
		return "[" + (char) start + "-" + (char) end + "]";
	}

	@Override
	public String getMatchCode() {
		return "(I[ci] >= " + start + " && I[ci] <= " + end + ")";
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + end;
		result = 31 * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Range)) {
			return false;
		}
		
		Range other = (Range) obj;
		
		return start == other.start && end == other.end;
	}
	
	
}
