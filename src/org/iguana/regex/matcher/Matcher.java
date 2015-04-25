package org.iguana.regex.matcher;

import org.iguana.util.Input;

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
		return match(input, 0) == input.length() - 1;
	}
	
	default boolean matchPrefix(Input input) {
		return match(input, 0) > -1;
	}
	
	default boolean match(Input input, int start, int end) {
		return match(input, start) == (end - start);
	}
}
