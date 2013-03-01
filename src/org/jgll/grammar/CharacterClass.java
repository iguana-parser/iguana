package org.jgll.grammar;

import java.util.Collections;
import java.util.List;

/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9]
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final List<Range> ranges;

	public CharacterClass(List<Range> ranges) {
		if(ranges == null || ranges.size() == 0) {
			throw new IllegalArgumentException("Ranges cannot be null or empty.");
		}
		this.ranges = Collections.unmodifiableList(ranges);
	}
	
	public List<Range> getRanges() {
		return ranges;
	}
	
	@Override
	public boolean match(int i) {
		for(Range range : ranges) {
			if(range.match(i)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String s = "[";
		for(Range range : ranges) {
			s += (char) range.getStart() + "-" + (char) range.getEnd();
		}
		s += "]";
		return s;
	}

	@Override
	public String getMatchCode() {
		StringBuilder sb = new StringBuilder();
		for(Range range : ranges) {
			sb.append(range.getMatchCode()).append(" || ");
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return 31 * 17 + ranges.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CharacterClass)) {
			return false;
		}
		
		CharacterClass other = (CharacterClass) obj;

		return ranges.equals(other.ranges);
	}


}
