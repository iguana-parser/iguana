package org.jgll.grammar;

import java.util.Collections;
import java.util.List;

/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9]
 * 
 * 
 * @author Ali Afroozeh <afroozeh@gmail.com>
 *
 */
public class CharacterClass extends Terminal {
	
	private static final long serialVersionUID = 1L;

	private final List<Range> ranges;

	public CharacterClass(List<Range> ranges) {
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
			s += range.getStart() + "-" + range.getEnd();
		}
		s += "]";
		return s;
	}

}
