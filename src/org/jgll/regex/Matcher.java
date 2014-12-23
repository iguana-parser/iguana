package org.jgll.regex;

import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */

@FunctionalInterface
public interface Matcher {
	
	public int match(Input input, int i);
	
	default boolean match(Input input) {
		return match(input, 0) != -1;
	}
	
	default boolean match(Input input, int start, int end) {
		return match(input, start) == (end - start);
	}
}
