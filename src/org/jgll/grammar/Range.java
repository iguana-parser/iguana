package org.jgll.grammar;

import java.util.BitSet;

import org.jgll.parser.HashFunctions;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Range implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final int start;
	
	private final int end;
	
	private BitSet testSet;

	public Range(int start, int end) {
		
		if(end < start) {
			throw new IllegalArgumentException("Start cannot be less than end.");
		}
		
		this.start = start;
		this.end = end;
		testSet = new BitSet();
		testSet.set(start, end + 1);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	@Override
	public boolean match(int i) {
		return testSet.get(i);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getMatchCode() {
		return "(I[ci] >= " + start + " && I[ci] <= " + end + ")";
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(start, end);
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

	@Override
	public String getName() {
		return  "[" + (char) start + "-" + (char) end + "]";
	}

	@Override
	public BitSet asBitSet() {
		return testSet;
	}

	@Override
	public int getMinimumValue() {
		return start;
	}

	@Override
	public int getMaximumValue() {
		return end;
	}
	
}
