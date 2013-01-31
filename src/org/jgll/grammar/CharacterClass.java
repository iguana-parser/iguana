package org.jgll.grammar;

import java.util.Collections;
import java.util.List;

/**
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
	
	@Override
	public boolean match(int i) {
		return false;
	}

}
