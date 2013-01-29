package org.jgll.grammar;

import java.util.List;

/**
 * 
 * 
 * @author Ali Afroozeh <afroozeh@gmail.com>
 *
 */
public class CharacterClass extends Terminal {

	private final List<Range> ranges;

	public CharacterClass(List<Range> ranges) {
		this.ranges = ranges;
	}
	
	@Override
	public boolean match(int i) {
		return false;
	}

}
